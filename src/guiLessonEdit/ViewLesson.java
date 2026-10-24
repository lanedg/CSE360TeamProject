package guiLessonEdit;

import entityClasses.User;
import entityClasses.LessonsLearned;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.SQLException;

/**
* Shows the window where a Contributor can edit a lesson.
*/
public class ViewLesson {

	Stage window = new Stage();
	TextField title = new TextField();
	TextArea experience = new TextArea();
	TextField time = new TextField();
	TextField effort = new TextField();
	LessonModelEditor editor;

	/**
	 * Loads the selected lesson and opens the edit window.
	 *
	 * @param stage the current application window
	 * @param user the logged-in Contributor
	 * @param lessonID the ID of the lesson to edit
	 */
	public static void editLessonGUI(Stage stage, User user, int lessonID) {
		LessonModelEditor editor = new LessonModelEditor();
		try { editor.lesson = editor.loadLesson(user, lessonID); }
		catch (IllegalArgumentException error) { LessonController.showError(stage, error.getMessage()); return; }
		catch (SQLException error) { LessonController.showError(stage, "Can't load lesson, check database"); return; }
		new ViewLesson(stage, user, editor).window.showAndWait();
	}

	/**
	 * Creates the edit window and its input fields and buttons.
	 *
	 * @param stage the current application window
	 * @param user the logged-in Contributor
	 * @param editor the model holding the loaded lesson
	 */
	private ViewLesson(Stage stage, User user, LessonModelEditor editor) {
		this.editor = editor;
		window.initOwner(stage);
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("Edit lesson");
		LessonsLearned lesson = editor.lesson;
		Label details = new Label("Lesson ID: " + lesson.getId() + "   Author: " + lesson.getAuthorID());
		details.setWrapText(true);
		experience.setWrapText(true);
		experience.setPrefRowCount(5);
		Button saveButton = new Button("Save");
		Button resetButton = new Button("Reset");
		Button closeButton = new Button("Close");
		saveButton.setOnAction(event -> LessonController.handleSave(this, user));
		resetButton.setOnAction(event -> populateFields());
		closeButton.setOnAction(event -> window.close());
		HBox buttons = new HBox(10, saveButton, resetButton, closeButton);
		buttons.setAlignment(Pos.CENTER_RIGHT);
		VBox form = new VBox(10, details, new Label("Title *"), title,
			new Label("Experience"), experience, new Label("Hours spent"), time,
			new Label("Effort"), effort, buttons);
		form.setPadding(new Insets(18));
		VBox.setVgrow(experience, Priority.ALWAYS);
		populateFields();
		window.setScene(new Scene(form, 500, 480));
		window.setMinWidth(440);
		window.setMinHeight(460);
	}

	/**
	 * Restores the input fields to the last saved lesson values.
	 */
	void populateFields() {
		LessonsLearned lesson = editor.lesson;
		setField(title, lesson.getLessonTitle(), lesson.isLessonTitleLocked());
		setField(experience, lesson.getExperienceDescription(), lesson.isExperienceDescriptionLocked());
		setField(time, Double.toString(lesson.getHoursSpent()), lesson.isHoursSpentLocked());
		setField(effort, lesson.getEffortUsed(), lesson.isEffortUsedLocked());
	}

	/**
	 * Sets an input field's text and blocks editing if it is locked.
	 *
	 * @param field the input field to update
	 * @param value the text to show in the field
	 * @param locked whether the field is locked
	 */
	private void setField(TextInputControl field, String value, boolean locked) {
		field.setText(value == null ? "" : value);
		field.setEditable(!locked);
		field.setStyle(locked ? "-fx-control-inner-background: #e5e5e5; -fx-text-fill: #555555;" : ""); //makes it gray
		field.setOnMouseClicked(event -> {if (locked) LessonController.blockedInterraction(window);});
	}
}
