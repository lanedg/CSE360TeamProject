package guiCuratorHome;

import javafx.scene.control.Alert;

/*******
 * <p> Title: ControllerCuratorHome Class. </p>
 *
 * <p> Description: The controller actions for the Curator Home Page.  Each method here responds
 * to one of the buttons defined by the View and hands control to the package that performs that
 * curator task, so this class holds the routing for the curator's work and none of the work
 * itself.
 *
 * The class has been written assuming that the View or the Model are the only classes that can
 * invoke these methods.  This is why each has been declared as "protected".  Do not change any
 * of these methods to public.</p>
 *
 * <p> Copyright: Beck 2026 </p>
 *
 * @author Beck
 *
 * @version 1.00		2026-11-26 Initial version
 *
 */

public class ControllerCuratorHome {

	/**
	 * Default constructor is not used.
	 */
	public ControllerCuratorHome() {
	}


	/**********
	 * <p> Method: performUpdate() </p>
	 *
	 * <p> Description: Take the curator to the account update page.</p>
	 *
	 */
	protected static void performUpdate() {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewCuratorHome.theStage,
				ViewCuratorHome.theUser);
	}


	/**********
	 * <p> Method: performLockLessons() </p>
	 *
	 * <p> Description: Open the page on which a curator locks a whole lesson or one of its
	 * fields so the contributor can no longer change or delete it.
	 *
	 * That page opens a window of its own rather than replacing the content of this stage, which
	 * is why no stage or user is passed to it.  It is called here all the same, so that every
	 * curator task is started from this one page.</p>
	 *
	 * <p> Validated by: Manual Tests.pdf (MT-C2) </p>
	 *
	 */
	protected static void performLockLessons() {
		guiCuratorLock.ViewCuratorLock.display();
	}


	/**********
	 * <p> Method: performMergeData() </p>
	 *
	 * <p> Description: Open the page on which a curator merges the time and effort data of two
	 * or more lessons into a statistically useful form.
	 *
	 * The calculations for this task live in guiCuratorMerge.CuratorDataMerger, and the database
	 * support lives in LessonsLearnedHelper.updateMergedStats, but at the time of writing there
	 * is no page that calls them.  Until that page exists this button reports that the task is
	 * not yet available, rather than appearing to work and doing nothing.</p>
	 *
	 */
	protected static void performMergeData() {
		showNotYetAvailable("Merge Time and Effort Data");
	}


	/**********
	 * <p> Method: performSearchTerms() </p>
	 *
	 * <p> Description: Open the page on which a curator reviews the search terms an AI tool
	 * suggests for a lesson, so that a viewer whose wording differs from the author's can still
	 * find it.</p>
	 *
	 */
	protected static void performSearchTerms() {
		showNotYetAvailable("Manage Search Terms");
	}


	/**********
	 * <p> Method: performEditLesson() </p>
	 *
	 * <p> Description: Open the page on which a curator asks an AI tool to suggest clearer
	 * wording for a lesson and then accepts, edits or rejects that suggestion.</p>
	 *
	 */
	protected static void performEditLesson() {
		showNotYetAvailable("Refine a Lesson");
	}


	/**********
	 * <p> Method: showNotYetAvailable(String taskName) </p>
	 *
	 * <p> Description: Report that a curator task has not been implemented yet.  The button is
	 * left on the page rather than removed, so the set of curator tasks is visible from the
	 * first release and each one can be connected as its page is finished.</p>
	 *
	 * @param taskName is the name of the task the curator asked for
	 *
	 */
	private static void showNotYetAvailable(String taskName) {
		System.out.println("\n*** WARNING ***: " + taskName + " Not Yet Implemented");
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("*** WARNING ***");
		alert.setHeaderText(taskName);
		alert.setContentText(taskName + " is not yet implemented.");
		alert.showAndWait();
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Log the curator out and return to the login page.</p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewCuratorHome.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminate the execution of the program.</p>
	 *
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}