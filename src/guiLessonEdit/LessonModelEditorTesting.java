package guiLessonEdit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;

import applicationMain.FoundationsMain;
import database.Database;
import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;
import entityClasses.User;

/*******
 * <p>
 * Title: LessonModelEditorTesting Class.
 * </p>
 * 
 * <p>
 * Description: Boundary value and coverage tests for LessonModelEditor, the
 * class that saves a contributor's edits to a lesson while enforcing ownership
 * and curator locks. The tests follow TP2 Test Designs.pdf: tests E1 to E17
 * check each input limit at, just inside, and just outside its boundary, and
 * tests C1 to C12 reach the remaining branches of loadLesson and saveLesson.
 * </p>
 * 
 * <p>
 * The tests focus on two weaknesses: Mishandling of Exceptional Conditions
 * (OWASP A10), by checking that every bad input produces a readable message,
 * and Software or Data Integrity Failures (OWASP A08), by checking that locked
 * fields and other contributors' lessons are never changed.
 * </p>
 * 
 * <p>
 * The tests run against the real H2 database. They use test users whose names
 * are unique to each run, and they delete every user and lesson they create.
 * This class is in package guiLessonEdit because loadLesson and saveLesson are
 * package-private.
 * </p>
 * 
 * <p>
 * Copyright: Beckham Orticelli © 2026
 * </p>
 * 
 * @author Beckham Orticelli
 * 
 * @version 1.00 2026-11-11 Initial version for HW3 Task 2.4
 */
public class LessonModelEditorTesting {

	static int numPassed = 0; // Counter of the number of passed tests
	static int numFailed = 0; // Counter of the number of failed tests
	static ArrayList<String> failedTests = new ArrayList<String>(); // Reported at the end as defects

	static Database database = FoundationsMain.database;

	// The values every fresh test lesson starts with, so a test can tell whether
	// a field was changed
	static final String ORIGINAL_TITLE = "Push rejected after a teammate pushed first";
	static final String ORIGINAL_EXPERIENCE = "My push was rejected as non-fast-forward until I pulled my teammate's changes.";
	static final double ORIGINAL_HOURS = 1.0;
	static final String ORIGINAL_EFFORT = "Two team members";

	// Test users. The time suffix makes the names unique to each run, so a run
	// never collides with real accounts or with leftovers from an earlier run.
	static final String RUN = Long.toString(System.currentTimeMillis() % 1000000);
	static final String PASSWORD = "Test123!";
	static User contributorA; // Owns the test lessons
	static User contributorB; // A different contributor
	static User userC; // Has no Contributor role

	// Every lesson the tests create, so cleanup can remove them all
	static ArrayList<Integer> createdLessonIDs = new ArrayList<Integer>();

	/*
	 * A test step that may throw. saveLesson and loadLesson throw the checked
	 * SQLException, so a plain Runnable cannot hold them.
	 */
	interface TestAction {
		void run() throws Exception;
	}

	/*******
	 * <p>
	 * Method: main(String[] args)
	 * </p>
	 * 
	 * <p>
	 * Description: Connects to the database, creates the test users, runs every
	 * test case in order, removes everything the tests created, and prints the
	 * totals and the failed tests.
	 * </p>
	 * 
	 * @param args are not used
	 * 
	 */
	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nLessonModelEditor Boundary Value and Coverage Testing");

		try {
			database.connectToDatabase();
			LessonsLearnedHelper.initializeTable();
			contributorA = new User("editorTestA" + RUN, PASSWORD, "Test", "", "Owner", "Test",
					"a" + RUN + "@test.com", false, true, false);
			contributorB = new User("editorTestB" + RUN, PASSWORD, "Test", "", "Other", "Test",
					"b" + RUN + "@test.com", false, true, false);
			userC = new User("editorTestC" + RUN, PASSWORD, "Test", "", "Reviewer", "Test",
					"c" + RUN + "@test.com", false, false, true);
			database.register(contributorA);
			database.register(contributorB);
			database.register(userC);
		} catch (SQLException e) {
			System.out.println("*** Error *** Could not set up the test users: " + e.getMessage());
			System.out.println("Close any running copy of the application and try again.");
			return;
		}

		/************** Start of the test cases **************/

		// Boundary value tests
		performTestCase("E1", "A 255-character title is saved", LessonModelEditorTesting::testE1);
		performTestCase("E2", "A 256-character title is rejected", LessonModelEditorTesting::testE2);
		performTestCase("E3", "An empty title is rejected", LessonModelEditorTesting::testE3);
		performTestCase("E4", "A title of only spaces is rejected", LessonModelEditorTesting::testE4);
		performTestCase("E5", "A 255-character title with a space on each end is saved, trimmed",
				LessonModelEditorTesting::testE5);
		performTestCase("E6", "A 2000-character experience is saved", LessonModelEditorTesting::testE6);
		performTestCase("E7", "A 2001-character experience is rejected", LessonModelEditorTesting::testE7);
		performTestCase("E8", "Hours of 0 and 0.01 are saved", LessonModelEditorTesting::testE8);
		performTestCase("E9", "Hours of -0.01 are rejected", LessonModelEditorTesting::testE9);
		performTestCase("E10", "Blank hours are saved as 0", LessonModelEditorTesting::testE10);
		performTestCase("E11", "Hours of NaN are rejected", () -> rejectHours("NaN"));
		performTestCase("E12", "Hours of Infinity are rejected", () -> rejectHours("Infinity"));
		performTestCase("E13", "Hours of abc are rejected", () -> rejectHours("abc"));
		performTestCase("E14", "Hours of 1e309 (too large, becomes Infinity) are rejected",
				() -> rejectHours("1e309"));
		performTestCase("E15", "A 255-character effort is saved", LessonModelEditorTesting::testE15);
		performTestCase("E16", "A 256-character effort is rejected", LessonModelEditorTesting::testE16);
		performTestCase("E17", "Two invalid fields give both messages and save nothing",
				LessonModelEditorTesting::testE17);

		// Coverage tests
		performTestCase("C1", "A missing user cannot open a lesson", LessonModelEditorTesting::testC1);
		performTestCase("C2", "A user without the Contributor role cannot open a lesson",
				LessonModelEditorTesting::testC2);
		performTestCase("C3", "A contributor with the wrong password cannot open a lesson",
				LessonModelEditorTesting::testC3);
		performTestCase("C4", "A lesson ID that does not exist is reported", LessonModelEditorTesting::testC4);
		performTestCase("C5", "A contributor cannot open another contributor's lesson",
				LessonModelEditorTesting::testC5);
		performTestCase("C6", "Saving with no lesson open is refused", LessonModelEditorTesting::testC6);
		performTestCase("C7", "Saving with a different ID than the open lesson is refused",
				LessonModelEditorTesting::testC7);
		performTestCase("C8", "A lesson with all four fields locked cannot be saved",
				LessonModelEditorTesting::testC8);
		performTestCase("C9", "A locked title is never changed", LessonModelEditorTesting::testC9);
		performTestCase("C10", "Invalid hours are ignored, not rejected, when hours are locked",
				LessonModelEditorTesting::testC10);
		performTestCase("C11", "A lock added after the lesson was opened is still honored",
				LessonModelEditorTesting::testC11);
		performTestCase("C12", "A missing title or experience gives a readable message",
				LessonModelEditorTesting::testC12);

		/************** End of the test cases **************/

		cleanUp();
		database.closeConnection();

		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
		System.out.println("Failed tests (defects found): " + (failedTests.isEmpty() ? "none" : failedTests));
	}

	/*******
	 * <p>
	 * Method: void performTestCase(String testID, String description,
	 * BooleanSupplier test)
	 * </p>
	 * 
	 * <p>
	 * Description: Prints the test's ID and what it checks, runs it, and records
	 * whether it passed. An unexpected exception counts as a failure instead of
	 * stopping the remaining tests, because an unhandled exception is exactly the
	 * kind of defect these tests look for.
	 * </p>
	 * 
	 * @param testID      is the test's ID from TP2 Test Designs.pdf
	 * 
	 * @param description is what the test checks
	 * 
	 * @param test        is the method that runs the test and returns true if it
	 *                    passed
	 * 
	 */
	private static void performTestCase(String testID, String description, BooleanSupplier test) {
		System.out.println("____________________________________________________________________________"
				+ "\n\nTest case " + testID + ": " + description);
		boolean passed;
		try {
			passed = test.getAsBoolean();
		} catch (Exception e) {
			System.out.println("Unhandled exception: " + e);
			passed = false;
		}
		if (passed) {
			System.out.println("***Success*** Test case " + testID + " passed.");
			numPassed++;
		} else {
			System.out.println("***Failure*** Test case " + testID + " failed.");
			numFailed++;
			failedTests.add(testID);
		}
	}

	/*-********************************************************************************************
	 * 
	 * Boundary value tests
	 * 
	 */

	// E1 (R6): exactly at the 255-character title limit
	private static boolean testE1() {
		String title = titleOfLength(255);
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		return expectSaved(save(editor, id, title, ORIGINAL_EXPERIENCE, "1", ORIGINAL_EFFORT))
				&& storedTitleIs(id, title);
	}

	// E2 (R6): one character over the title limit
	private static boolean testE2() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		return expectError(save(editor, id, titleOfLength(256), ORIGINAL_EXPERIENCE,
				"1", ORIGINAL_EFFORT), "Title is too long");
	}

	// E3 (R6): the lower boundary, an empty title
	private static boolean testE3() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		return expectError(save(editor, id, "", ORIGINAL_EXPERIENCE, "1", ORIGINAL_EFFORT), "Title is required");
	}

	// E4 (R6): spaces only must count as empty, because the title is trimmed first
	private static boolean testE4() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		return expectError(save(editor, id, "   ", ORIGINAL_EXPERIENCE, "1", ORIGINAL_EFFORT),
				"Title is required");
	}

	// E5 (R6): 257 characters before trimming but 255 after, so the limit must be
	// applied to the trimmed title
	private static boolean testE5() {
		String title = titleOfLength(255);
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		return expectSaved(save(editor, id, " " + title + " ", ORIGINAL_EXPERIENCE, "1", ORIGINAL_EFFORT))
				&& storedTitleIs(id, title);
	}

	// E6 (R7): exactly at the 2000-character experience limit
	private static boolean testE6() {
		String experience = textOfLength(ORIGINAL_EXPERIENCE + " ", 2000);
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		if (!expectSaved(save(editor, id, ORIGINAL_TITLE, experience, "1", ORIGINAL_EFFORT)))
			return false;
		int storedLength = stored(id).getExperienceDescription().length();
		System.out.println("Stored experience length: " + storedLength + " (expected 2000)");
		return storedLength == 2000;
	}

	// E7 (R7): one character over the experience limit
	private static boolean testE7() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		return expectError(save(editor, id, ORIGINAL_TITLE, textOfLength(ORIGINAL_EXPERIENCE + " ", 2001), "1",
				ORIGINAL_EFFORT), "Experience is too long");
	}

	// E8 (R8): the lower boundary 0 and the smallest step inside it are both valid
	private static boolean testE8() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		boolean zeroSaved = expectSaved(save(editor, id, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, "0", ORIGINAL_EFFORT))
				&& storedHoursAre(id, 0.0);
		boolean smallSaved = expectSaved(
				save(editor, id, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, "0.01", ORIGINAL_EFFORT))
				&& storedHoursAre(id, 0.01);
		return zeroSaved && smallSaved;
	}

	// E9 (R8): the smallest step outside the lower boundary
	private static boolean testE9() {
		return rejectHours("-0.01");
	}

	// E10 (R8): blank hours mean 0
	private static boolean testE10() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		return expectSaved(save(editor, id, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, "", ORIGINAL_EFFORT))
				&& storedHoursAre(id, 0.0);
	}

	// E15 (R9): exactly at the 255-character effort limit
	private static boolean testE15() {
		String effort = textOfLength(ORIGINAL_EFFORT + ", ", 255);
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		if (!expectSaved(save(editor, id, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, "1", effort)))
			return false;
		boolean matches = stored(id).getEffortUsed().equals(effort);
		System.out.println("Stored effort matches the 255-character input: " + matches);
		return matches;
	}

	// E16 (R9): one character over the effort limit
	private static boolean testE16() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		return expectError(
				save(editor, id, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, "1", textOfLength(ORIGINAL_EFFORT + ", ", 256)),
				"Effort is too long");
	}

	// E17 (R10): several errors are reported together, and nothing is written
	private static boolean testE17() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		boolean bothReported = expectError(save(editor, id, "", ORIGINAL_EXPERIENCE, "-1", ORIGINAL_EFFORT),
				"Title is required\nHours must be a number 0+");
		return bothReported && storedTitleIs(id, ORIGINAL_TITLE) && storedHoursAre(id, ORIGINAL_HOURS);
	}

	/*-********************************************************************************************
	 * 
	 * Coverage tests
	 * 
	 */

	// C1 (R1): the user == null branch of loadLesson
	private static boolean testC1() {
		int id = newLessonForA();
		LessonModelEditor editor = new LessonModelEditor();
		return expectError(() -> editor.loadLesson(null, id), "Can't verify account");
	}

	// C2 (R1): the missing Contributor role branch of loadLesson
	private static boolean testC2() {
		int id = newLessonForA();
		LessonModelEditor editor = new LessonModelEditor();
		return expectError(() -> editor.loadLesson(userC, id), "Can't verify account");
	}

	// C3 (R1): the failed login branch. The user object claims the Contributor
	// role, so only the database login check can catch the wrong password.
	private static boolean testC3() {
		int id = newLessonForA();
		User wrongPassword = new User(contributorA.getUserName(), "WrongPass9!", "Test", "", "Owner", "Test",
				"a" + RUN + "@test.com", false, true, false);
		LessonModelEditor editor = new LessonModelEditor();
		return expectError(() -> editor.loadLesson(wrongPassword, id), "Can't verify account");
	}

	// C4 (R2): the lesson == null branch of loadLesson
	private static boolean testC4() {
		int missingID = LessonsLearnedHelper.getHighestLessonId() + 1000;
		LessonModelEditor editor = new LessonModelEditor();
		return expectError(() -> editor.loadLesson(contributorA, missingID), "Lesson not found");
	}

	// C5 (R3, A08): the ownership branch of loadLesson
	private static boolean testC5() {
		int id = newLessonForA();
		LessonModelEditor editor = new LessonModelEditor();
		return expectError(() -> editor.loadLesson(contributorB, id), "You can only edit your lessons");
	}

	// C6 (R4): the no-open-lesson branch of saveLesson
	private static boolean testC6() {
		int id = newLessonForA();
		LessonModelEditor editor = new LessonModelEditor();
		return expectError(save(editor, id, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, "1", ORIGINAL_EFFORT),
				"Reopen lesson");
	}

	// C7 (R4): the ID-mismatch branch of saveLesson
	private static boolean testC7() {
		int openedID = newLessonForA();
		int otherID = newLessonForA();
		LessonModelEditor editor = openAsA(openedID);
		return expectError(save(editor, otherID, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, "1", ORIGINAL_EFFORT),
				"Reopen lesson");
	}

	// C8 (R5): the all-fields-locked branch of saveLesson
	private static boolean testC8() {
		int id = newLessonForA();
		setLocks(id, true, true, true, true);
		LessonModelEditor editor = openAsA(id);
		return expectError(save(editor, id, "A new title", "New experience", "2", "New effort"),
				"All fields are locked");
	}

	// C9 (R11, A08): a locked title is skipped while the unlocked fields save
	private static boolean testC9() {
		int id = newLessonForA();
		setLocks(id, true, false, false, false);
		LessonModelEditor editor = openAsA(id);
		if (!expectSaved(save(editor, id, "A new title", "New experience", "2", "New effort")))
			return false;
		LessonsLearned lesson = stored(id);
		System.out.println("Stored experience: " + lesson.getExperienceDescription());
		return storedTitleIs(id, ORIGINAL_TITLE) && lesson.getExperienceDescription().equals("New experience");
	}

	// C10 (R11): when hours are locked, the hours branch is skipped entirely, so
	// even an invalid value is ignored rather than rejected
	private static boolean testC10() {
		int id = newLessonForA();
		setLocks(id, false, false, true, false);
		LessonModelEditor editor = openAsA(id);
		return expectSaved(save(editor, id, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, "abc", ORIGINAL_EFFORT))
				&& storedHoursAre(id, ORIGINAL_HOURS);
	}

	// C11 (R12, A08): saveLesson must reload the lesson, not trust the copy that
	// was on screen when the lesson was opened
	private static boolean testC11() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		System.out.println("Locking the title in the database after the lesson was opened...");
		setLocks(id, true, false, false, false);
		return expectSaved(save(editor, id, "A new title", ORIGINAL_EXPERIENCE, "1", ORIGINAL_EFFORT))
				&& storedTitleIs(id, ORIGINAL_TITLE);
	}

	// C12 (R13, A10): a missing value must produce a readable message, not crash
	private static boolean testC12() {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		System.out.println("Input title: null");
		boolean titleHandled = expectReadableError(save(editor, id, null, ORIGINAL_EXPERIENCE, "1", ORIGINAL_EFFORT));
		System.out.println("\nInput experience: null");
		boolean experienceHandled = expectReadableError(
				save(editor, id, ORIGINAL_TITLE, null, "1", ORIGINAL_EFFORT));
		return titleHandled && experienceHandled;
	}

	/*-********************************************************************************************
	 * 
	 * Helper methods shared by the tests
	 * 
	 */

	// Shared check for the four non-number hours inputs (E9, E11 to E14)
	private static boolean rejectHours(String hours) {
		int id = newLessonForA();
		LessonModelEditor editor = openAsA(id);
		System.out.println("Input hours: \"" + hours + "\"");
		return expectError(save(editor, id, ORIGINAL_TITLE, ORIGINAL_EXPERIENCE, hours, ORIGINAL_EFFORT),
				"Hours must be a number 0+") && storedHoursAre(id, ORIGINAL_HOURS);
	}

	// Creates a fresh, unlocked lesson owned by contributor A and returns its ID
	private static int newLessonForA() {
		LessonsLearned lesson = new LessonsLearned(contributorA.getUserName(), ORIGINAL_TITLE);
		lesson.setExperienceDescription(ORIGINAL_EXPERIENCE);
		lesson.setHoursSpent(ORIGINAL_HOURS);
		lesson.setEffortUsed(ORIGINAL_EFFORT);
		LessonsLearnedHelper.createLesson(lesson);
		// createLesson does not return the new ID, so read the highest one; the
		// tests run alone, so no other lesson can be created in between
		int id = LessonsLearnedHelper.getHighestLessonId();
		createdLessonIDs.add(id);
		return id;
	}

	// Opens a lesson as contributor A, the way the edit screen does before a save
	private static LessonModelEditor openAsA(int id) {
		LessonModelEditor editor = new LessonModelEditor();
		try {
			editor.lesson = editor.loadLesson(contributorA, id);
		} catch (SQLException e) {
			System.out.println("Could not open lesson " + id + ": " + e.getMessage());
		}
		return editor;
	}

	// Wraps one saveLesson call as contributor A so it can be passed to a check
	private static TestAction save(LessonModelEditor editor, int id, String title, String experience,
			String hours, String effort) {
		return () -> editor.saveLesson(contributorA, id, title, experience, hours, effort);
	}

	// Sets the four lock flags directly in the database. No curator screen exists
	// yet, so this uses the same helper method the existing LessonEditTest uses.
	private static void setLocks(int id, boolean title, boolean experience, boolean hours, boolean effort) {
		LessonsLearned lesson = stored(id);
		lesson.setLessonTitleLocked(title);
		lesson.setExperienceDescriptionLocked(experience);
		lesson.setHoursSpentLocked(hours);
		lesson.setEffortUsedLocked(effort);
		LessonsLearnedHelper.updateLesson(lesson);
	}

	// Passes when the action throws an IllegalArgumentException with exactly the
	// expected message
	private static boolean expectError(TestAction action, String expected) {
		System.out.println("Expected message: " + oneLine(expected));
		try {
			action.run();
			System.out.println("Actual: no error was reported");
			return false;
		} catch (IllegalArgumentException e) {
			System.out.println("Actual message:   " + oneLine(e.getMessage()));
			return expected.equals(e.getMessage());
		} catch (Exception e) {
			System.out.println("Actual: unhandled " + e.getClass().getSimpleName());
			return false;
		}
	}

	// Passes when the action throws an IllegalArgumentException with any message,
	// meaning the problem was reported to the user instead of crashing
	private static boolean expectReadableError(TestAction action) {
		System.out.println("Expected: a readable error message");
		try {
			action.run();
			System.out.println("Actual: no error was reported");
			return false;
		} catch (IllegalArgumentException e) {
			System.out.println("Actual message: " + oneLine(e.getMessage()));
			return true;
		} catch (Exception e) {
			System.out.println("Actual: unhandled " + e.getClass().getSimpleName());
			return false;
		}
	}

	// Passes when the action finishes without any exception
	private static boolean expectSaved(TestAction action) {
		try {
			action.run();
			System.out.println("Save result: saved");
			return true;
		} catch (Exception e) {
			System.out.println("Save result: refused with " + e.getClass().getSimpleName() + ": "
					+ oneLine(e.getMessage()));
			return false;
		}
	}

	// Reads a lesson back from the database, so checks see what was really stored
	private static LessonsLearned stored(int id) {
		try {
			return LessonsLearnedHelper.getLesson(id);
		} catch (SQLException e) {
			System.out.println("Could not read lesson " + id + ": " + e.getMessage());
			return null;
		}
	}

	private static boolean storedTitleIs(int id, String expected) {
		String actual = stored(id).getLessonTitle();
		System.out.println("Stored title is the expected title: " + actual.equals(expected));
		return actual.equals(expected);
	}

	private static boolean storedHoursAre(int id, double expected) {
		double actual = stored(id).getHoursSpent();
		System.out.println("Stored hours: " + actual + " (expected " + expected + ")");
		return actual == expected;
	}

	// Repeats a realistic phrase and cuts it to an exact length
	private static String textOfLength(String phrase, int length) {
		return phrase.repeat(length / phrase.length() + 1).substring(0, length);
	}

	// Builds a title of an exact length that does not start or end with a space,
	// so trimming cannot change its length
	private static String titleOfLength(int length) {
		String title = textOfLength(ORIGINAL_TITLE + " ", length);
		if (title.endsWith(" "))
			title = title.substring(0, length - 1) + ".";
		return title;
	}

	// Shows a multi-line message on one line so expected and actual line up
	private static String oneLine(String message) {
		return message == null ? "null" : message.replace("\n", " | ");
	}

	/*******
	 * <p>
	 * Method: void cleanUp()
	 * </p>
	 * 
	 * <p>
	 * Description: Unlocks and deletes every test lesson, then deletes the test
	 * users. Lessons are unlocked first because deleteLesson refuses to delete a
	 * locked lesson.
	 * </p>
	 * 
	 */
	private static void cleanUp() {
		for (int id : createdLessonIDs) {
			if (stored(id) != null) {
				setLocks(id, false, false, false, false);
				LessonsLearnedHelper.deleteLesson(id);
			}
		}
		database.deleteUser(contributorA.getUserName());
		database.deleteUser(contributorB.getUserName());
		database.deleteUser(userC.getUserName());
		System.out.println("____________________________________________________________________________");
		System.out.println("\nRemoved the " + createdLessonIDs.size() + " test lessons and 3 test users.");
	}
}