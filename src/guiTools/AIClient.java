package guiTools;

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

/*******
 * <p> Title: AIClient Class. </p>
 *
 * <p> Description: A single place through which every part of the application talks to the Groq
 * AI service.  The class takes an instruction and some text, sends them to the service, and
 * returns the reply as a String.  It knows nothing about lessons, curators or contributors, so
 * any feature that needs an AI reply can use it without that feature's rules leaking in here.
 *
 * The reason for one shared class rather than an AI call inside each feature is that the parts
 * that are awkward to get right are the same every time: the key must come from the environment
 * and never appear in the source, a slow or failed reply must not be allowed to look like a
 * successful one, and the service, the model and the time limit must be changeable in one place
 * when the team's free Groq allowance is exhausted.  ContributorAI contains an earlier copy of
 * this request code written for the contributor's help window; it should be changed to call this
 * class so there is only one copy to maintain.
 *
 * This method blocks while the service replies, which can take several seconds.  A caller on the
 * JavaFX application thread must not call it directly, as the window would freeze until the
 * reply arrived.  Callers run it on a virtual thread and return to the application thread with
 * Platform.runLater, as ContributorAI does.</p>
 *
 * <p> Copyright: Beck 2026 </p>
 *
 * @author Beck
 *
 * @version 1.00		2026-11-25 Initial version
 *
 */

public class AIClient {

	/** The Groq endpoint every request is sent to */
	private static final String ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";

	/** The model used for every request, matching the one ContributorAI uses */
	private static final String MODEL = "openai/gpt-oss-20b";

	/** How long to wait for a reply before giving up, so a stalled request cannot hang a page */
	private static final int TIMEOUT_SECONDS = 45;

	/** Returned when the reply could not be obtained, so a caller never mistakes a failure for
	 * an answer.  Callers test for this prefix rather than for an empty String, because an empty
	 * reply is a different condition. */
	public static final String ERROR_PREFIX = "AI unavailable: ";


	/**
	 * Default constructor is not used.
	 */
	public AIClient() {
	}


	/*******
	 * <p> Method: String ask(String instruction, String input) </p>
	 *
	 * <p> Description: Send an instruction and the text it applies to, and return the service's
	 *		reply.  The instruction states what the service is to do with the text, and the input
	 *		is the text itself.  Keeping the two apart matters: the instruction is written by the
	 *		application and the input is written by a user, so a user cannot replace the
	 *		instruction by typing one into a lesson.
	 *
	 *		This method never raises an exception to its caller.  A missing key, a refusal by the
	 *		service, or a network failure all return a String beginning with ERROR_PREFIX, so a
	 *		page can show the reason to the user instead of having to catch anything.</p>
	 *
	 * @param instruction	states what the service is to do with the input
	 *
	 * @param input			is the text the instruction applies to
	 *
	 * @return the service's reply, or a message beginning with ERROR_PREFIX
	 *
	 * <p> Validated by: AIClientTest.testMissingKeyReportsError,
	 *		AIClientTest.testBlankInstructionRefused, and Manual Test 1 </p>
	 *
	 */
	public static String ask(String instruction, String input) {

		if (instruction == null || instruction.isBlank()) {
			return ERROR_PREFIX + "no instruction was provided.";
		}
		if (input == null || input.isBlank()) {
			return ERROR_PREFIX + "there is no text to work with.";
		}

		// The key is read from the environment on every call rather than being held in a field,
		// so a key set after the application started is picked up without a restart, and so no
		// key is ever written into the source or committed to the repository
		String key = System.getenv("GROQ_API_KEY");
		if (key == null || key.isBlank()) {
			return ERROR_PREFIX + "GROQ_API_KEY is not set for this run configuration.";
		}

		try (HttpClient client = HttpClient.newHttpClient()) {
			var systemMessage = Map.of("role", "system", "content", instruction);
			var userMessage = Map.of("role", "user", "content", input);
			String json = new Gson().toJson(
					Map.of("model", MODEL, "messages", List.of(systemMessage, userMessage)));

			HttpRequest request = HttpRequest.newBuilder(URI.create(ENDPOINT))
					.header("Authorization", "Bearer " + key.trim())
					.header("Content-Type", "application/json")
					.timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				return ERROR_PREFIX + "the service returned status " + response.statusCode() + ".";
			}

			JsonObject choice = JsonParser.parseString(response.body()).getAsJsonObject()
					.getAsJsonArray("choices").get(0).getAsJsonObject();
			return choice.getAsJsonObject("message").get("content").getAsString().trim();

		} catch (Exception error) {
			// Every failure is reported the same way, because the user can do nothing different
			// about a timeout than about a refused connection, and the detail belongs in the log
			System.err.println("AI request failed: " + error.getMessage());
			return ERROR_PREFIX + "the service could not be reached.";
		}
	}


	/*******
	 * <p> Method: boolean isError(String reply) </p>
	 *
	 * <p> Description: Report whether a reply from ask is a failure rather than an answer.  This
	 *		exists so callers do not each repeat the test against ERROR_PREFIX and so the way a
	 *		failure is signalled can be changed in one place.</p>
	 *
	 * @param reply is the value returned by ask
	 *
	 * @return true when the reply reports a failure, else false
	 *
	 * <p> Validated by: AIClientTest.testMissingKeyReportsError </p>
	 *
	 */
	public static boolean isError(String reply) {
		return reply == null || reply.startsWith(ERROR_PREFIX);
	}
}