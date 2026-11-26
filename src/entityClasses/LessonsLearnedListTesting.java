package entityClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/*******
 * <p>
 * Title: LessonsLearnedListTesting Class.
 * </p>
 * 
 * <p>
 * Description: Boundary value and coverage tests for LessonsLearnedList, the
 * class behind every lesson list and keyword search a contributor sees. The
 * tests follow TP2 Test Designs.pdf: tests L1 to L10 check the boundaries of
 * list size and keyword input, and tests L11 to L21 reach the remaining
 * branches, including lessons with missing fields.
 * </p>
 * 
 * <p>
 * The tests focus on two weaknesses: Software or Data Integrity Failures (OWASP
 * A08), by checking that a search result never shares storage with the
 * original list, and Mishandling of Exceptional Conditions (OWASP A10), by
 * checking that missing values never cause a crash.
 * </p>
 * 
 * <p>
 * LessonsLearnedList holds lessons only in memory, so these tests need no
 * database and no screens.
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
public class LessonsLearnedListTesting {

	static int numPassed = 0; // Counter of the number of passed tests
	static int numFailed = 0; // Counter of the number of failed tests
	static ArrayList<String> failedTests = new ArrayList<String>(); // Reported at the end as defects

	/*******
	 * <p>
	 * Method: main(String[] args)
	 * </p>
	 * 
	 * <p>
	 * Description: Runs every test case in order, then prints the totals and the
	 * failed tests.
	 * </p>
	 * 
	 * @param args are not used
	 * 
	 */
	public static void main(String[] args) {
		System.out.println("______________________________________");
		System.out.println("\nLessonsLearnedList Boundary Value and Coverage Testing");

		/************** Start of the test cases **************/

		// Boundary value tests
		performTestCase("L1", "An empty list searched for \"git\" returns an empty list",
				LessonsLearnedListTesting::testL1);
		performTestCase("L2", "A one-lesson list returns the lesson only when it matches",
				LessonsLearnedListTesting::testL2);
		performTestCase("L3", "A missing keyword returns every lesson", () -> blankKeywordReturnsAll(null));
		performTestCase("L4", "An empty keyword returns every lesson", () -> blankKeywordReturnsAll(""));
		performTestCase("L5", "A keyword of only spaces returns every lesson", () -> blankKeywordReturnsAll("   "));
		performTestCase("L6", "A one-character keyword returns every lesson containing it",
				() -> searchFinds(sampleList(), "q", 3));
		performTestCase("L7", "A keyword equal to an entire title returns that lesson",
				() -> searchFinds(sampleList(), "Import fails because the project already exists", 3));
		performTestCase("L8", "An uppercase keyword matches lowercase text", () -> searchFinds(sampleList(), "GIT", 1, 2));
		performTestCase("L9", "Spaces around a keyword are ignored", () -> searchFinds(sampleList(), " git ", 1, 2));
		performTestCase("L10", "A list of 1,000 lessons returns the correct number of matches",
				LessonsLearnedListTesting::testL10);

		// Coverage tests
		performTestCase("L11", "A lesson with no title but a matching description is found",
				() -> searchFinds(listOf(lesson(1, null, "Cloned from git")), "git", 1));
		performTestCase("L12", "A lesson with no description but a matching title is found",
				() -> searchFinds(listOf(lesson(1, "Git push rejected", null)), "git", 1));
		performTestCase("L13", "A lesson with no title and no description is skipped without a crash",
				() -> searchFinds(listOf(lesson(1, null, null)), "git"));
		performTestCase("L14", "Title-only, description-only, and both matches are each returned once",
				LessonsLearnedListTesting::testL14);
		performTestCase("L15", "removeLesson removes a lesson in the list and ignores one that is not",
				LessonsLearnedListTesting::testL15);
		performTestCase("L16", "A list built from an existing collection holds those lessons",
				LessonsLearnedListTesting::testL16);
		performTestCase("L17", "Changing a blank-keyword search result leaves the original list unchanged",
				() -> resultIsIndependent(""));
		performTestCase("L18", "Changing a keyword search result leaves the original list unchanged",
				() -> resultIsIndependent("git"));
		performTestCase("L19", "Changing the list returned by getLessonsList leaves the original unchanged",
				LessonsLearnedListTesting::testL19);
		performTestCase("L20", "Adding a missing lesson and then searching does not crash",
				LessonsLearnedListTesting::testL20);
		performTestCase("L21", "Building from a missing starting list and then adding does not crash",
				LessonsLearnedListTesting::testL21);

		/************** End of the test cases **************/

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
	 * Tests that need more than one step
	 * 
	 */

	// L1 (L-R1, L-R5): the smallest list, size 0
	private static boolean testL1() {
		LessonsLearnedList empty = new LessonsLearnedList();
		System.out.println("New list size: " + empty.getSize() + " (expected 0)");
		return empty.getSize() == 0 && searchFinds(empty, "git");
	}

	// L2 (L-R5): a list of exactly one lesson, matching and not matching
	private static boolean testL2() {
		LessonsLearnedList one = listOf(sampleList().getLessonsList().get(0));
		return searchFinds(one, "git", 1) && searchFinds(one, "eclipse");
	}

	// L10 (L-R2, L-R5): a large list; every fourth lesson mentions git
	private static boolean testL10() {
		LessonsLearnedList big = new LessonsLearnedList();
		for (int i = 0; i < 1000; i++) {
			String title = (i % 4 == 0) ? "Git lesson " + i : "Eclipse lesson " + i;
			big.addLesson(lesson(i, title, "A lesson learned in CSE 360"));
		}
		int found = big.searchByKeyword("git").getSize();
		System.out.println("List size: " + big.getSize() + ", matches: " + found + " (expected 250)");
		return big.getSize() == 1000 && found == 250;
	}

	// L14 (L-R5): each side of the title-or-description check, and both together
	private static boolean testL14() {
		LessonsLearnedList list = listOf(lesson(1, "Git push rejected", "Pulled first"),
				lesson(2, "Copied projects lose settings", "Cloned from git"),
				lesson(3, "Git branch lost libraries", "After a git checkout"),
				lesson(4, "Import fails", "Folder already in the workspace"));
		return searchFinds(list, "git", 1, 2, 3);
	}

	// L15 (L-R3): removing a lesson that is present, then one that is not
	private static boolean testL15() {
		LessonsLearnedList list = sampleList();
		LessonsLearned first = list.getLessonsList().get(0);
		list.removeLesson(first);
		System.out.println("Size after removing a lesson in the list: " + list.getSize() + " (expected 2)");
		list.removeLesson(lesson(99, "Not in the list", "None"));
		System.out.println("Size after removing a lesson not in the list: " + list.getSize() + " (expected 2)");
		return list.getSize() == 2;
	}

	// L16 (L-R2): the constructor that takes an existing collection
	private static boolean testL16() {
		List<LessonsLearned> existing = new ArrayList<LessonsLearned>(sampleList().getLessonsList());
		LessonsLearnedList list = new LessonsLearnedList(existing);
		System.out.println("Size: " + list.getSize() + " (expected 3)");
		return list.getSize() == 3 && list.getLessonsList().containsAll(existing);
	}

	// L19 (L-R8, A08): the internal list must not be changeable from outside
	private static boolean testL19() {
		LessonsLearnedList original = sampleList();
		original.getLessonsList().add(lesson(99, "Added from outside", "Not through addLesson"));
		System.out.println("Original size after adding through getLessonsList: " + original.getSize()
				+ " (expected 3)");
		return original.getSize() == 3;
	}

	// L20 (L-R9, A10): a null lesson must be rejected or skipped, not crash later
	private static boolean testL20() {
		LessonsLearnedList list = sampleList();
		list.addLesson(null);
		System.out.println("Added a null lesson; now searching for \"git\"...");
		return searchFinds(list, "git", 1, 2);
	}

	// L21 (L-R9, A10): a null starting list must be rejected or handled
	private static boolean testL21() {
		LessonsLearnedList list = new LessonsLearnedList(null);
		System.out.println("Built a list from null; now adding a lesson...");
		list.addLesson(lesson(1, "Git push rejected", "Pulled first"));
		System.out.println("Size: " + list.getSize() + " (expected 1)");
		return list.getSize() == 1;
	}

	/*-********************************************************************************************
	 * 
	 * Helper methods shared by the tests
	 * 
	 */

	// Shared check for L3 to L5: a blank keyword returns all three sample lessons
	private static boolean blankKeywordReturnsAll(String keyword) {
		System.out.println("Keyword: " + (keyword == null ? "null" : "\"" + keyword + "\""));
		return searchFinds(sampleList(), keyword, 1, 2, 3);
	}

	// Shared check for L17 and L18 (L-R8, A08): adding a lesson to a search result
	// must not add it to the original list
	private static boolean resultIsIndependent(String keyword) {
		LessonsLearnedList original = sampleList();
		LessonsLearnedList result = original.searchByKeyword(keyword);
		result.addLesson(lesson(99, "Added to the result", "Only the result should grow"));
		System.out.println("Keyword: \"" + keyword + "\"; original size after changing the result: "
				+ original.getSize() + " (expected 3)");
		return original.getSize() == 3;
	}

	/*
	 * Searches a list and passes only when exactly the expected lesson IDs come
	 * back, each once. Passing no IDs means the result must be empty.
	 */
	private static boolean searchFinds(LessonsLearnedList list, String keyword, int... expectedIDs) {
		List<LessonsLearned> found = list.searchByKeyword(keyword).getLessonsList();
		ArrayList<Integer> foundIDs = new ArrayList<Integer>();
		for (LessonsLearned lesson : found)
			foundIDs.add(lesson.getId());
		ArrayList<Integer> expected = new ArrayList<Integer>();
		for (int id : expectedIDs)
			expected.add(id);
		System.out.println("Search \"" + keyword + "\" returned IDs " + foundIDs + " (expected " + expected + ")");
		return foundIDs.equals(expected);
	}

	/*
	 * Three realistic sample lessons. Lesson 1 mentions git in its title, lesson
	 * 2 only in its description, and lesson 3 not at all. Only lesson 3 contains
	 * the letter q, which L6 relies on.
	 */
	private static LessonsLearnedList sampleList() {
		return listOf(
				lesson(1, "Git push rejected after a teammate pushed first",
						"The push was rejected as non-fast-forward until I pulled their changes."),
				lesson(2, "Copied projects lose their run configuration",
						"After cloning the project from git, the JavaFX VM arguments were missing."),
				lesson(3, "Import fails because the project already exists",
						"Eclipse refused to import a folder already in the workspace, so I reopened it quickly instead."));
	}

	private static LessonsLearnedList listOf(LessonsLearned... lessons) {
		LessonsLearnedList list = new LessonsLearnedList();
		for (LessonsLearned lesson : lessons)
			list.addLesson(lesson);
		return list;
	}

	private static LessonsLearned lesson(int id, String title, String description) {
		LessonsLearned lesson = new LessonsLearned("testContributor", title);
		lesson.setId(id);
		lesson.setExperienceDescription(description);
		return lesson;
	}
}