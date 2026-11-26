package guiCuratorLock;

import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;

/**
 * <p> Title: CuratorLockTest Class. </p>
 *
 * <p> Description: Automated test suite validating whole-lesson and per-field
 * locking in LessonsLearned, ControllerCuratorLock, and LessonsLearnedHelper,
 * including delete protection for locked lessons. </p>
 *
 * @author Koushik Sai Chintha
 */
public class CuratorLockTest {

	private static int passed = 0;
	private static int failed = 0;

	public static void main(String[] args) {
		LessonsLearnedHelper.initializeTable();

		testLockEntireLesson();
		testHasAnyLockBlocksDelete();
		testPersistLessonLocks();

		System.out.println("\nCuratorLockTest Summary -> Passed: " + passed + " | Failed: " + failed);
	}

	/**
	 * <p> Method: testLockEntireLesson </p>
	 * <p> Description: Validates that setLessonLocked(true) also locks all individual fields. </p>
	 */
	public static void testLockEntireLesson() {
		LessonsLearned l = new LessonsLearned("u1", "Full Lock Test");
		l.setLessonLocked(true);

		boolean ok = l.isLessonLocked()
				&& l.isLessonTitleLocked()
				&& l.isExperienceDescriptionLocked()
				&& l.isHoursSpentLocked()
				&& l.isEffortUsedLocked();
		record("testLockEntireLesson", ok);
	}

	/**
	 * <p> Method: testHasAnyLockBlocksDelete </p>
	 * <p> Description: Validates hasAnyLock() and confirms LessonsLearnedHelper.deleteLesson
	 * refuses to delete a lesson with any active lock. </p>
	 */
	public static void testHasAnyLockBlocksDelete() {
		int id = 0;
		try {
			LessonsLearned l = new LessonsLearned("testLockUser", "Delete Guard Test");
			LessonsLearnedHelper.createLesson(l);
			id = LessonsLearnedHelper.getHighestLessonId();
			l = LessonsLearnedHelper.getLesson(id);

			// Lock only one field
			ControllerCuratorLock.saveLocks(l, false, true, false, false, false);
			boolean lockDetected = l.hasAnyLock() && LessonsLearnedHelper.isLessonLocked(id);
			boolean deleteBlocked = !LessonsLearnedHelper.deleteLesson(id);

			record("testHasAnyLockBlocksDelete", lockDetected && deleteBlocked);
		} catch (Exception e) {
			record("testHasAnyLockBlocksDelete", false);
		} finally {
			cleanupLesson(id);
		}
	}

	/**
	 * <p> Method: testPersistLessonLocks </p>
	 * <p> Description: Validates saving and reloading whole-lesson and per-field locks in H2. </p>
	 */
	public static void testPersistLessonLocks() {
		int id = 0;
		try {
			LessonsLearned l = new LessonsLearned("testLockUser", "Persist Lock Test");
			LessonsLearnedHelper.createLesson(l);
			id = LessonsLearnedHelper.getHighestLessonId();
			l = LessonsLearnedHelper.getLesson(id);

			boolean saved = ControllerCuratorLock.saveLocks(l, false, true, false, true, false);
			LessonsLearned fetched = LessonsLearnedHelper.getLesson(id);

			boolean ok = saved && fetched != null
					&& !fetched.isLessonLocked()
					&& fetched.isLessonTitleLocked()
					&& !fetched.isExperienceDescriptionLocked()
					&& fetched.isHoursSpentLocked()
					&& !fetched.isEffortUsedLocked();
			record("testPersistLessonLocks", ok);
		} catch (Exception e) {
			record("testPersistLessonLocks", false);
		} finally {
			cleanupLesson(id);
		}
	}

	// Unlocks all fields before deleting so the H2 test row is cleanly removed
	private static void cleanupLesson(int id) {
		if (id <= 0) return;
		try {
			LessonsLearned l = LessonsLearnedHelper.getLesson(id);
			if (l != null) {
				ControllerCuratorLock.saveLocks(l, false, false, false, false, false);
				LessonsLearnedHelper.deleteLesson(id);
			}
		} catch (Exception ignored) { }
	}

	private static void record(String name, boolean ok) {
		System.out.println(name + ": " + (ok ? "PASSED" : "FAILED"));
		if (ok) passed++; else failed++;
	}
}