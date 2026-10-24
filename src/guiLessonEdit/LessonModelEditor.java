package guiLessonEdit;

import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;
import entityClasses.User;
import applicationMain.FoundationsMain;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Loads lessons, checks edits, and saves changes for Contributors.
*/
public class LessonModelEditor {

	LessonsLearned lesson;

	/**
	 * Loads a lesson after checking the Contributor and lesson owner.
	 *
	 * @param user the logged-in Contributor
	 * @param lessonID the ID of the lesson to load
	 * @return the lesson from the database
	 * @throws SQLException if the lesson cannot be read from the database
	 * @throws IllegalArgumentException if access is denied or the lesson is missing
	 */
	LessonsLearned loadLesson(User user, int lessonID) throws SQLException {
		if (user == null || !user.getNewRole1() || !FoundationsMain.database.loginRole1(user)) {
			throw new IllegalArgumentException("Can't verify account");
		}
		LessonsLearned lesson = LessonsLearnedHelper.getLesson(lessonID);
		if (lesson == null) throw new IllegalArgumentException("Lesson not found");
		if (!user.getUserName().equals(lesson.getAuthorID())) { throw new IllegalArgumentException("You can only edit your lessons"); }
		return lesson;
	}

	/**
	 * Checks the input and saves changes to unlocked lesson fields.
	 *
	 * @param user the logged-in Contributor
	 * @param lessonID the ID of the lesson to save
	 * @param title the title entered in the window
	 * @param experience the experience text entered in the window
	 * @param time the hours spent entered as text
	 * @param effort the effort text entered in the window
	 * @throws SQLException if the database operation fails
	 * @throws IllegalArgumentException if the input or edit request is invalid
	 */
	void saveLesson(User user, int lessonID, String title, String experience, String time, String effort) throws SQLException {
		if (lesson == null || lesson.getId() != lessonID) throw new IllegalArgumentException("Reopen lesson");
		LessonsLearned lesson = loadLesson(user, lessonID);
		if (lesson.isLessonTitleLocked() && lesson.isExperienceDescriptionLocked() && lesson.isHoursSpentLocked() && lesson.isEffortUsedLocked()) {
			throw new IllegalArgumentException("All fields are locked");
		}
		title = title.trim();
		ArrayList<String> error = new ArrayList<>();
		if (!lesson.isLessonTitleLocked()) {
			if (title.isEmpty()) error.add("Title is required");
			else if (title.length() > 255) error.add("Title is too long");
		}
		if (!lesson.isExperienceDescriptionLocked() && experience.length() > 2000) error.add("Experience is too long");
		double hours = 0;
		if (!lesson.isHoursSpentLocked()) {
			try {
				if (!time.trim().isEmpty()) hours = Double.parseDouble(time.trim());
				if (!Double.isFinite(hours) || hours < 0) error.add("Hours must be a number 0+");
			} catch (NumberFormatException exception) { error.add("Hours must be a number 0+"); }
		}
		if (!lesson.isEffortUsedLocked() && effort.length() > 255) error.add("Effort is too long");
		if (!error.isEmpty()) throw new IllegalArgumentException(String.join("\n", error));

		if (!LessonsLearnedHelper.updateContributorLesson(user, lesson, title, experience, hours, effort)) { throw new IllegalArgumentException("Lesson changed, reopen"); }
		if (!lesson.isLessonTitleLocked()) lesson.setLessonTitle(title);
		if (!lesson.isExperienceDescriptionLocked()) lesson.setExperienceDescription(experience);
		if (!lesson.isHoursSpentLocked()) lesson.setHoursSpent(hours);
		if (!lesson.isEffortUsedLocked()) lesson.setEffortUsed(effort);
		this.lesson = lesson;
	}
}
