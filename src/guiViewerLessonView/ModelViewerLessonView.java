package guiViewerLessonView;

import java.sql.SQLException;

import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;

/*******
 * <p> Title: ModelViewerLessonView Class. </p>
 * 
 * <p> Description: The ViewerLessonView Page Model. Handles fetching a single
 * LessonsLearned record from the database when needed. </p>
 * 
 * @author Lane Gebbie
 * 
 * @version 1.00		2026-11-25 Initial version
 *  
 */

public class ModelViewerLessonView {

	/**********
	 * <p> Constructor: ModelViewerLessonView() </p>
	 * 
	 * <p> Description: Default constructor for the Viewer Lesson View model. </p>
	 */
	public ModelViewerLessonView() {
	}

	/**********
	 * <p> Method: loadLessonById(int lessonId) </p>
	 * 
	 * <p> Description: Retrieves a single LessonsLearned record by ID from the database. </p>
	 * 
	 * @param lessonId the unique ID of the lesson to retrieve
	 * @return the matching LessonsLearned object, or null if not found or on SQL error
	 */
	protected static LessonsLearned loadLessonById(int lessonId) {
		try {
			return LessonsLearnedHelper.getLesson(lessonId);
		} catch (SQLException e) {
			System.err.println("Error loading lesson " + lessonId + ": " + e.getMessage());
			return null;
		}
	}
}