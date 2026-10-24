package guiLessonEdit;

import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.sql.SQLException;

/**
 * Handles the Save button and messages in the Edit Lesson window.
*/
public class LessonController { 

	/**
	 * Saves the entered changes or shows an error if saving fails.
	 *
	 * @param view the Edit Lesson window
	 * @param user the logged-in Contributor
	 */
	static void handleSave(ViewLesson view, User user) {
		try { view.editor.saveLesson(user, view.editor.lesson.getId(), view.title.getText(), view.experience.getText(), view.time.getText(), view.effort.getText()); }
		catch (IllegalArgumentException error) { showError(view.window, error.getMessage()); return; } 
		catch (SQLException error) { showError(view.window, "Can't save, check database"); return; }
		view.populateFields();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(view.window);
		alert.setTitle("Edit lesson");
		alert.setHeaderText(null);
		alert.setContentText("Lesson saved");
		alert.showAndWait();
	}

	/**
	 * Shows an error message in a popup.
	 *
	 * @param window the window that owns the popup
	 * @param error the message to display
	 */
	static void showError(Stage window, String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(window);
		alert.setTitle("oops!");
		alert.setHeaderText(null);
		alert.setContentText(error);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

	/**
	 * Shows a message when a user clicks a locked field.
	 *
	 * @param window the window that owns the popup
	 */
	static void blockedInterraction(Stage window) {
		showError(window, "Field is locked, higher privileges required");
	}
}
