package guiLessonEdit;

import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;
import entityClasses.User;
import applicationMain.FoundationsMain;
import java.sql.SQLException;
import java.util.ArrayList;

class LessonModelEditor {

	LessonsLearned lesson;

	LessonsLearned loadLesson(User user, int lessonID) throws SQLException {
		if (user == null || !user.getNewRole1() || !FoundationsMain.database.loginRole1(user)) {
			throw new IllegalArgumentException("Can't verify account");
		}
		LessonsLearned lesson = LessonsLearnedHelper.getLesson(lessonID);
		if (lesson == null) throw new IllegalArgumentException("Lesson not found");
		if (!user.getUserName().equals(lesson.getAuthorID())) { throw new IllegalArgumentException("You can only edit your lessons"); }
		return lesson;
	}

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
