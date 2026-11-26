package guiViewerLessonView;

import entityClasses.LessonsLearned;

/*******
 * <p> Title: ControllerViewerLessonView Class. </p>
 * 
 * <p> Description: The Java/FX-based Viewer Lesson View Page controller. Provides
 * actions for returning to the Viewer Home page, updating the current user account,
 * logging out, and quitting. </p>
 * 
 * @author Lane Gebbie
 * 
 * @version 1.00		2026-11-25 Initial version
 *  
 */

public class ControllerViewerLessonView {

	/**********
	 * <p> Constructor: ControllerViewerLessonView() </p>
	 * 
	 * <p> Description: Default constructor for the Viewer Lesson View controller. </p>
	 */
	public ControllerViewerLessonView() {
	}

	/**********
	 * <p> Method: populateLessonFields(LessonsLearned lesson) </p>
	 * 
	 * <p> Description: Populates the title, experience description, hours spent, and effort
	 * used display fields on ViewViewerLessonView from the provided lesson. </p>
	 * 
	 * @param lesson the LessonsLearned record to display, or null to clear fields
	 */
	protected static void populateLessonFields(LessonsLearned lesson) {
		if (lesson == null) {
			ViewViewerLessonView.text_LessonTitle.setText("");
			ViewViewerLessonView.text_ExperienceDescription.setText("");
			ViewViewerLessonView.text_HoursSpent.setText("");
			ViewViewerLessonView.text_EffortUsed.setText("");
			return;
		}

		String title = lesson.getLessonTitle() != null ? lesson.getLessonTitle() : "";
		String description = lesson.getExperienceDescription() != null ? lesson.getExperienceDescription() : "";
		String hours = Double.toString(lesson.getHoursSpent());
		String effort = lesson.getEffortUsed() != null ? lesson.getEffortUsed() : "";

		ViewViewerLessonView.text_LessonTitle.setText(title);
		ViewViewerLessonView.text_ExperienceDescription.setText(description);
		ViewViewerLessonView.text_HoursSpent.setText(hours);
		ViewViewerLessonView.text_EffortUsed.setText(effort);
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: Navigates the current user to the User Update page to edit account
	 * attributes. </p>
	 */
	protected static void performUpdate() {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(
				ViewViewerLessonView.theStage, ViewViewerLessonView.theUser);
	}

	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: Returns the user to the Viewer Home page. </p>
	 */
	protected static void performReturn() {
		guiViewer.ViewViewerHome.displayViewerHome(
				ViewViewerLessonView.theStage, ViewViewerLessonView.theUser);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: Logs out the current user and returns to the User Login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewViewerLessonView.theStage);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: Terminates execution of the application. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}