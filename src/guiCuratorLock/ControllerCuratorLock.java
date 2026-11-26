package guiCuratorLock;

import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;

/**
 * <p> Title: ControllerCuratorLock Class. </p>
 *
 * <p> Description: Connects ViewCuratorLock actions to LessonsLearnedHelper
 * to persist whole-lesson and per-field lock states in the H2 database. </p>
 *
 * <p> Validated by: CuratorLockTest#testPersistLessonLocks,
 * Manual Tests.pdf (MT-3: Lock Lesson/Fields) </p>
 *
 * @author Koushik Sai Chintha
 */
public class ControllerCuratorLock {

	/**
	 * <p> Method: saveLocks </p>
	 * <p> Description: Updates the lock flags on the selected lesson and saves them to H2. </p>
	 * <p> Validated by: CuratorLockTest#testPersistLessonLocks </p>
	 *
	 * @param lesson the lesson to update
	 * @param lessonLocked true to lock the entire lesson
	 * @param titleLocked true to lock the title field
	 * @param expLocked true to lock the experience description field
	 * @param hoursLocked true to lock the hours spent field
	 * @param effortLocked true to lock the effort used field
	 * @return true if the database update succeeded
	 */
	public static boolean saveLocks(LessonsLearned lesson, boolean lessonLocked,
			boolean titleLocked, boolean expLocked, boolean hoursLocked, boolean effortLocked) {
		if (lesson == null) {
			return false;
		}
		lesson.setLessonTitleLocked(lessonLocked || titleLocked);
		lesson.setExperienceDescriptionLocked(lessonLocked || expLocked);
		lesson.setHoursSpentLocked(lessonLocked || hoursLocked);
		lesson.setEffortUsedLocked(lessonLocked || effortLocked);
		lesson.setLessonLocked(lessonLocked);
		return LessonsLearnedHelper.updateLessonLocks(lesson);
	}
}