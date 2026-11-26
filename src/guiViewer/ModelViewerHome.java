package guiViewer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;

/*******
 * <p> Title: ModelViewerHome Class. </p>
 * 
 * <p> Description: The ViewerHome Page Model. Handles loading lessons learned from the
 * database and filtering them by keyword. </p>
 * 
 * @author Lane Gebbie
 * 
 * @version 1.00		2026-11-25 Initial version
 *  
 */

public class ModelViewerHome {

	/**********
	 * <p> Constructor: ModelViewerHome() </p>
	 * 
	 * <p> Description: Default constructor for the Viewer Home model. </p>
	 */
	public ModelViewerHome() {
	}

	/**********
	 * <p> Method: loadAllLessons() </p>
	 * 
	 * <p> Description: Ensures the lessons_learned table exists and retrieves all stored
	 * lessons ordered by ID into a LessonsLearnedList. </p>
	 * 
	 * @return a LessonsLearnedList containing all lessons from the database
	 */
	protected static LessonsLearnedList loadAllLessons() {
		LessonsLearnedHelper.initializeTable();
		LessonsLearnedList allLessons = new LessonsLearnedList();
		String sql = "SELECT * FROM lessons_learned ORDER BY id";

		try (Connection conn = LessonsLearnedHelper.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				LessonsLearned lesson = new LessonsLearned(
						rs.getString("author_id"),
						rs.getString("lesson_title"));
				lesson.setId(rs.getInt("id"));
				lesson.setLessonTitleLocked(rs.getBoolean("lesson_title_locked"));
				lesson.setExperienceDescription(rs.getString("experience_description"));
				lesson.setExperienceDescriptionLocked(rs.getBoolean("experience_description_locked"));
				lesson.setHoursSpent(rs.getDouble("hours_spent"));
				lesson.setHoursSpentLocked(rs.getBoolean("hours_spent_locked"));
				lesson.setEffortUsed(rs.getString("effort_used"));
				lesson.setEffortUsedLocked(rs.getBoolean("effort_used_locked"));
				allLessons.addLesson(lesson);
			}
		} catch (SQLException e) {
			System.err.println("Error loading lessons learned: " + e.getMessage());
		}

		return allLessons;
	}

	/**********
	 * <p> Method: filterLessons(LessonsLearnedList allLessons, String keyword) </p>
	 * 
	 * <p> Description: Filters the provided LessonsLearnedList by matching the keyword
	 * against each lesson's title or experience description. </p>
	 * 
	 * @param allLessons the source list of lessons to search
	 * @param keyword the search term entered by the user
	 * @return a LessonsLearnedList containing matching lessons, or an empty list if source is null
	 */
	protected static LessonsLearnedList filterLessons(LessonsLearnedList allLessons, String keyword) {
		if (allLessons == null) {
			return new LessonsLearnedList();
		}
		return allLessons.searchByKeyword(keyword);
	}
}