package guiTools;

import javafx.stage.Stage; 
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.application.Platform;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** Lesson help for contributors.
 * Manual tests 1 and 2 helpButton, the window, ask and getAnswer
 */
public class ContributorAI {
	TextArea reply = new TextArea();
	HBox buttons = new HBox(10);

	/** Makes the AI help button
	 * @param stage lesson window
	 * @param title title field
	 * @param experience draft field
	 * @return help button
	 */
	public static Button helpButton(Stage stage, TextField title, TextArea experience) {
		Button help = new Button("AI help");
		// Read on click to include unsaved edits.
		help.setOnAction(event -> new ContributorAI(stage, title.getText(), experience.getText()));
		return help;
	}

	/** Opens help for the current title and draft. */
	private ContributorAI(Stage stage, String title, String experience) {
		Stage window = new Stage();
		window.initOwner(stage);
		window.initModality(Modality.WINDOW_MODAL);
		window.setTitle("AI help");
		Button write = new Button("Write");
		Button brainstorm = new Button("Brainstorm");
		Button find = new Button("Find an example");
		write.setOnAction(event -> ask("Write", title, experience));
		brainstorm.setOnAction(event -> ask("Brainstorm", title, experience));
		find.setOnAction(event -> ask("Find an example", title, experience));
		buttons.getChildren().addAll(write, brainstorm, find);
		reply.setEditable(false);
		reply.setWrapText(true);
		VBox form = new VBox(10, buttons, reply);
		form.setPadding(new Insets(18));
		window.setScene(new Scene(form, 460, 250));
		window.show();
	}

	/** Shows the reply for the selected action */
	private void ask(String action, String title, String experience) {
		JsonObject body;
		try { body = makeRequest(action, title, experience); }
		catch (IllegalArgumentException error) { reply.setText(error.getMessage()); return; }
		String key = System.getenv("GROQ_API_KEY");
		if (key == null || key.isBlank()) { reply.setText("Set GROQ_API_KEY dummy."); return; }
		buttons.setDisable(true);
		reply.setText("Working...");
		// Let the window respond while Groq works.
		Thread.startVirtualThread(() -> {
			String answer = getAnswer(action, body, key);
			Platform.runLater(() -> {
				reply.setText(answer);
				buttons.setDisable(false);
			});
		});
	}

	/** Builds the request from the action, title and draft. Tested by write brainstorm and find */
	static JsonObject makeRequest(String action, String title, String experience) {
		String input = "Title: " + title + "\nExperience: " + experience;
		String prompt;
		if (action.equals("Write")) {
			if (experience.isBlank()) throw new IllegalArgumentException("Enter your experience.");
			prompt = "Lightly edit the experience. Keep the wording, order, tone, facts. Leave clear text alone. Return only the edit.";
		} else if (action.equals("Brainstorm")) {
			if (title.isBlank() && experience.isBlank()) throw new IllegalArgumentException("Enter a title or experience.");
			prompt = "Ask only two short questions about what's missing from this lesson.";
		} else {
			if (title.isBlank()) throw new IllegalArgumentException("Enter a title.");
			input = title;
			prompt = "Give one word: merge for a Git conflict, title for a blank lesson title or none.";
		}
		JsonObject body = new JsonObject();
		body.addProperty("system", prompt);
		body.addProperty("prompt", input);
		return body;
	}

	/** Sends the request to Groq and returns its reply */
	private static String getAnswer(String action, JsonObject body, String key) {
		try (HttpClient client = HttpClient.newHttpClient()) {
			var instructions = Map.of("role", "system", "content", body.get("system").getAsString());
			var draft = Map.of("role", "user", "content", body.get("prompt").getAsString());
			String json = new Gson().toJson(Map.of("model", "openai/gpt-oss-20b", "messages", List.of(instructions, draft)));
			HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.groq.com/openai/v1/chat/completions"))
					.header("Authorization", "Bearer " + key.trim())
					.header("Content-Type", "application/json")
					.timeout(Duration.ofSeconds(45))
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) return "Groq error " + response.statusCode();
			JsonObject choice = JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonArray("choices").get(0).getAsJsonObject();
			String answer = choice.getAsJsonObject("message").get("content").getAsString().trim();
			if (action.equals("Find an example")) return example(answer);
			return answer;
		} catch (Exception error) { return "Couldn't get a reply"; }
	}

	/** Returns the example for Groq's answer. Tested by find. */
	static String example(String match) {
		match = match.replace(".", "").trim();
		if (match.equalsIgnoreCase("merge")) return "Resolve a merge conflict\nWe edited the same method. We kept both changes and ran the test.";
		if (match.equalsIgnoreCase("title")) return "Catch an empty title\nWe saved a lesson with no title. We added a title check and tried again.";
		return "No matching example.";
	}
}
