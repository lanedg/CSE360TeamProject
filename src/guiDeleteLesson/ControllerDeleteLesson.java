package guiDeleteLesson;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import database.LessonsLearnedHelper;

/*******
 * <p> Title: ControllerDeleteLesson Class. </p>
 * 
 * <p> Description: The Java/FX-based Delete Lesson Page.  This class provides the controller
 * actions based on the contributor's use of the JavaFX GUI widgets defined by the View class.
 * 
 * Changing the selected lesson changes the layout of the page, so this Controller determines
 * which widgets are shown, as well as performing the deletion itself.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change
 * any of these methods to public.</p>
 * 
 * <p> Copyright: Beck 2026 </p>
 * 
 * @author Beck
 * 
 * @version 1.00		2026-10-21 Initial version
 *  
 */

public class ControllerDeleteLesson {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of
	protected static methods that can be called by the View (which is a singleton instantiated
	object) and the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerDeleteLesson() {
	}

	// The id of the lesson currently selected in the ComboBox.  A value of -1 means that no
	// lesson has been selected, which is the case when the prompt is showing.
	private static int theSelectedLessonId = -1;


	/**********
	 * <p> Method: doSelectLesson() </p>
	 * 
	 * <p> Description: This method uses the ComboBox widget, fetches which item in the ComboBox
	 * was selected (a lesson in this case), establishes the id of that lesson, and reports
	 * whether the lesson may be deleted.  A lesson that has been locked by another role may not
	 * be deleted by its contributor, so the lock state is reported before the contributor
	 * attempts the deletion.</p>
	 * 
	 */
	protected static void doSelectLesson() {
		ViewDeleteLesson.theSelectedLesson =
				(String) ViewDeleteLesson.combobox_SelectLesson.getValue();

		// Establish the id of the selected lesson from the selected list entry
		theSelectedLessonId = extractLessonId(ViewDeleteLesson.theSelectedLesson);

		// When an actual lesson has been selected, report whether it can be deleted
		if (theSelectedLessonId > 0) {
			if (LessonsLearnedHelper.isLessonLocked(theSelectedLessonId)) {
				ViewDeleteLesson.label_LessonStatus.setText(
						"This lesson has a locked field and cannot be deleted.");
				ViewDeleteLesson.button_DeleteLesson.setDisable(true);
			} else {
				ViewDeleteLesson.label_LessonStatus.setText(
						"This lesson is not locked and may be deleted.");
				ViewDeleteLesson.button_DeleteLesson.setDisable(false);
			}
		}

		// Repaint the window showing the widgets for the new selection
		repaintTheWindow();
	}


	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then
	 * establishes the appropriate list of widgets in the Pane to show the proper set of current
	 * values.  Until a lesson has been selected there is nothing to delete, so the status and the
	 * delete button are not shown.</p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewDeleteLesson.theRootPane.getChildren().clear();

		// Determine which of the two views to show to the contributor
		if (theSelectedLessonId <= 0) {
			// Only show the request to select a lesson to be deleted and the ComboBox
			ViewDeleteLesson.theRootPane.getChildren().addAll(
					ViewDeleteLesson.label_PageTitle, ViewDeleteLesson.label_UserDetails,
					ViewDeleteLesson.button_UpdateThisUser, ViewDeleteLesson.line_Separator1,
					ViewDeleteLesson.label_SelectLesson, ViewDeleteLesson.combobox_SelectLesson,
					ViewDeleteLesson.line_Separator4, ViewDeleteLesson.button_Return,
					ViewDeleteLesson.button_Logout, ViewDeleteLesson.button_Quit);
		}
		else {
			// Show all the fields as there is a selected lesson (as opposed to the prompt)
			ViewDeleteLesson.theRootPane.getChildren().addAll(
					ViewDeleteLesson.label_PageTitle, ViewDeleteLesson.label_UserDetails,
					ViewDeleteLesson.button_UpdateThisUser, ViewDeleteLesson.line_Separator1,
					ViewDeleteLesson.label_SelectLesson, ViewDeleteLesson.combobox_SelectLesson,
					ViewDeleteLesson.label_LessonStatus, ViewDeleteLesson.button_DeleteLesson,
					ViewDeleteLesson.line_Separator4, ViewDeleteLesson.button_Return,
					ViewDeleteLesson.button_Logout, ViewDeleteLesson.button_Quit);
		}

		// Add the list of widgets to the stage and show it

		// Set the title for the window
		ViewDeleteLesson.theStage.setTitle("CSE 360 Foundation Code: Delete Lesson Page");
		ViewDeleteLesson.theStage.setScene(ViewDeleteLesson.theDeleteLessonScene);
		ViewDeleteLesson.theStage.show();
	}


	/**********
	 * <p> Method: performDeleteLesson() </p>
	 * 
	 * <p> Description: This method removes the selected lesson from the database once the
	 * contributor has confirmed that this is what they intend.  The helper refuses to remove a
	 * lesson that has a locked field, so the outcome it reports is shown to the contributor and
	 * the list of lessons is then refreshed.</p>
	 * 
	 */
	protected static void performDeleteLesson() {

		// If no actual lesson has been selected there is nothing to delete
		if (theSelectedLessonId <= 0) {
			showAlert("Delete Lesson", "No lesson was selected.");
			return;
		}

		// Require the contributor to confirm before the lesson is removed
		Alert areYouSure = new Alert(Alert.AlertType.CONFIRMATION);
		areYouSure.setTitle("Are you sure?");
		areYouSure.setHeaderText("Delete this lesson learned?");
		areYouSure.setContentText(ViewDeleteLesson.theSelectedLesson);

		Optional<ButtonType> answer = areYouSure.showAndWait();
		if (answer.isEmpty() || answer.get() != ButtonType.OK) {
			// The contributor cancelled, so the lesson is left in place
			return;
		}

		// Perform the removal and report the outcome
		if (LessonsLearnedHelper.deleteLesson(theSelectedLessonId)) {
			showAlert("Delete Lesson", "The lesson was deleted.");
		} else {
			showAlert("Delete Lesson",
					"The lesson could not be deleted.\n" +
					"A lesson that has a locked field may not be removed.");
		}

		// Refresh the list so the deleted lesson is no longer offered, and return the page to
		// the state where no lesson has been selected
		ViewDeleteLesson.refreshLessonList();
		ViewDeleteLesson.combobox_SelectLesson.getSelectionModel().select(0);
		doSelectLesson();
	}


	/**********
	 * <p> Method: int extractLessonId(String listEntry) </p>
	 * 
	 * <p> Description: Each entry of the ComboBox has the form "id - title", so the id is the
	 * text before the first separator.  The first entry of the list is a prompt rather than a
	 * lesson, and a value of -1 is returned for it so the caller knows no lesson was chosen.</p>
	 * 
	 * @param listEntry is the entry currently selected in the ComboBox
	 * 
	 * @return the id of the selected lesson, or -1 when no lesson is selected
	 * 
	 */
	private static int extractLessonId(String listEntry) {
		if (listEntry == null ||
				listEntry.compareTo(ViewDeleteLesson.SELECT_PROMPT) == 0) {
			return -1;
		}
		try {
			return Integer.parseInt(listEntry.split(" - ")[0].trim());
		} catch (NumberFormatException e) {
			// The entry was not in the expected form, so treat it as no selection
			return -1;
		}
	}


	/**********
	 * <p> Method: showAlert(String header, String message) </p>
	 * 
	 * <p> Description: Private helper that displays an informational message to the
	 * contributor.</p>
	 * 
	 * @param header	the heading text for the alert
	 * 
	 * @param message	the body text for the alert
	 * 
	 */
	private static void showAlert(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Contributor");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}


	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the contributor to the page from which this page was
	 * invoked.</p>
	 * 
	 */
	protected static void performReturn() {
		// *** The contributor home page is still being built.  Change this one line to call that
		// *** page once it exists, in the same way that the Add/Remove Roles page returns to the
		// *** Admin Home page.
		guiRole1.ViewRole1Home.displayRole1Home(ViewDeleteLesson.theStage,
				ViewDeleteLesson.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with an invitation code can
	 * start the process of setting up an account.</p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteLesson.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}