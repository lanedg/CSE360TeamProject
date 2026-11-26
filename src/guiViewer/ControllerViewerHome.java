package guiViewer;

import java.util.ArrayList;
import java.util.List;

import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import javafx.collections.FXCollections;

/*******
 * <p> Title: ControllerViewerHome Class. </p>
 * 
 * <p> Description: The ViewerHome Page Controller. Handles refreshing and searching lessons
 * learned, tracking lesson selection, opening the lesson detail view, updating account details,
 * logging out, and quitting the application.</p>
 * 
 * @author Lane Gebbie
 * 
 * @version 1.00		2026-11-25 Initial version
 *  
 */

public class ControllerViewerHome {

	private static LessonsLearnedList currentDisplayedLessons = new LessonsLearnedList();

	/**********
	 * <p> Constructor: ControllerViewerHome() </p>
	 * 
	 * <p> Description: Default constructor is not used. </p>
	 */
	public ControllerViewerHome() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: Navigates the current user to the User Update page to edit account
	 * attributes. </p>
	 */
	protected static void performUpdate() {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewViewerHome.theStage, ViewViewerHome.theUser);
	}

	/**********
	 * <p> Method: refreshLessonList() </p>
	 * 
	 * <p> Description: Reloads all lessons from the model and applies the current search
	 * filter to update the displayed list. </p>
	 */
	protected static void refreshLessonList() {
		ViewViewerHome.allLessons = ModelViewerHome.loadAllLessons();
		performSearch();
	}

	/**********
	 * <p> Method: performSearch() </p>
	 * 
	 * <p> Description: Filters loaded lessons using the text in the search field, resets the
	 * current selection state, and repopulates the ListView in ViewViewerHome. </p>
	 */
	protected static void performSearch() {
		String keyword = ViewViewerHome.text_Search.getText();
		currentDisplayedLessons = ModelViewerHome.filterLessons(ViewViewerHome.allLessons, keyword);

		List<String> displayItems = new ArrayList<>();
		for (LessonsLearned lesson : currentDisplayedLessons.getLessonsList()) {
			String title = lesson.getLessonTitle() != null ? lesson.getLessonTitle() : "Untitled";
			String author = lesson.getAuthorID() != null ? lesson.getAuthorID() : "Unknown";
			displayItems.add(String.format("#%d - %s (Author: %s)", lesson.getId(), title, author));
		}

		ViewViewerHome.list_Lessons.getSelectionModel().clearSelection();
		ViewViewerHome.selectedLesson = null;
		ViewViewerHome.list_Lessons.setItems(FXCollections.observableArrayList(displayItems));
		ViewViewerHome.button_ViewLesson.setDisable(true);
	}

	/**********
	 * <p> Method: performSelectLesson() </p>
	 * 
	 * <p> Description: Updates the currently selected lesson based on the ListView selection
	 * index and enables or disables the View Lesson button accordingly. </p>
	 */
	protected static void performSelectLesson() {
		int selectedIndex = ViewViewerHome.list_Lessons.getSelectionModel().getSelectedIndex();
		List<LessonsLearned> list = currentDisplayedLessons.getLessonsList();
		if (selectedIndex >= 0 && selectedIndex < list.size()) {
			ViewViewerHome.selectedLesson = list.get(selectedIndex);
			ViewViewerHome.button_ViewLesson.setDisable(false);
		} else {
			ViewViewerHome.selectedLesson = null;
			ViewViewerHome.button_ViewLesson.setDisable(true);
		}
	}

	/**********
	 * <p> Method: performViewLesson() </p>
	 * 
	 * <p> Description: Opens the individual lesson viewer UI for the currently selected
	 * lesson. </p>
	 */
	protected static void performViewLesson() {
		if (ViewViewerHome.selectedLesson == null) {
			return;
		}
		guiViewerLessonView.ViewViewerLessonView.displayViewerLessonView(
				ViewViewerHome.theStage, ViewViewerHome.theUser, ViewViewerHome.selectedLesson);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: Logs out the current user and returns to the User Login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewViewerHome.theStage);
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