package database;

import entityClasses.LessonsLearned;

/*******
 * <p> Title: LessonsLearnedTestingAutomation Class. </p>
 * 
 * <p> Description: A set of semi-automated tests for the delete operation of the lessons
 * learned database helper.  Each test case states what is being tested, performs the test,
 * and reports whether the result matched what was expected.</p>
 * 
 * @author Beck
 * 
 * @version 1.00	2026-10-18	Initial set of delete operation tests
 * 
 */
public class LessonsLearnedTestingAutomation {

	/** Counter of the number of passed tests */
	static int numPassed = 0;

	/** Counter of the number of failed tests */
	static int numFailed = 0;

	/*******
	 * <p> Method: void main(String[] args) </p>
	 * 
	 * <p> Description: Displays a header, runs each delete test case, and displays a footer
	 * summarizing how many tests passed and failed.</p>
	 * 
	 * @param args is the array of command line parameters.  These are not used.
	 * 
	 */
	public static void main(String[] args) {
		System.out.println("____________________________________________________________");
		System.out.println("\nLessons Learned Delete Operation Testing Automation");

		// Ensure the table exists before any test case runs
		LessonsLearnedHelper.initializeTable();

		/************** Positive test case: an unlocked lesson is deleted *****************/
		performDeleteTest(1, "An unlocked lesson is deleted", false, true);

		/************** Negative test case: a locked lesson is refused ********************/
		performDeleteTest(2, "A locked lesson is not deleted", true, false);

		/************** Negative test case: a lesson that does not exist ******************/
		System.out.println("____________________________________________________________\n");
		System.out.println("Test case: 3\nTesting: A lesson id that does not exist is refused");
		if (!LessonsLearnedHelper.deleteLesson(-1)) {
			System.out.println("***Success*** No lesson was deleted, as expected.\n");
			numPassed++;
		} else {
			System.out.println("***Failure*** A lesson was reported deleted.\n");
			numFailed++;
		}

		/************** Testing automation report footer *********************************/
		System.out.println("____________________________________________________________");
		System.out.println("\nNumber of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	/*******
	 * <p> Method: void performDeleteTest(int testCase, String description, boolean locked,
	 *		boolean expectedToDelete) </p>
	 * 
	 * <p> Description: Creates a lesson, optionally locking one of its fields, attempts to
	 * delete it, and reports whether the outcome matched what the test case expected.  The
	 * number of lessons in the database is checked before and after so the result can be seen
	 * on the console.</p>
	 * 
	 * @param testCase			is the number of this test case
	 * 
	 * @param description		states what this test case is testing
	 * 
	 * @param locked			specifies whether a field of the lesson is locked
	 * 
	 * @param expectedToDelete	specifies whether the deletion is expected to succeed
	 * 
	 */
	private static void performDeleteTest(int testCase, String description, boolean locked,
			boolean expectedToDelete) {
		System.out.println("____________________________________________________________\n");
		System.out.println("Test case: " + testCase + "\nTesting: " + description);

		// Create a lesson for this test so the test does not depend on existing data
		LessonsLearned lesson = new LessonsLearned("testContributor",
				"Test lesson for case " + testCase);
		lesson.setExperienceDescription("Created by the delete operation tests.");
		lesson.setHoursSpent(1.0);
		lesson.setEffortUsed("One tester");
		lesson.setLessonTitleLocked(locked);
		LessonsLearnedHelper.createLesson(lesson);

		int countBefore = LessonsLearnedHelper.getLessonCount();
		System.out.println("Number of lessons before the delete: " + countBefore);

		// The lesson just created has the highest id in the table
		int id = LessonsLearnedHelper.getHighestLessonId();
		boolean wasDeleted = LessonsLearnedHelper.deleteLesson(id);

		int countAfter = LessonsLearnedHelper.getLessonCount();
		System.out.println("Number of lessons after the delete:  " + countAfter);

		if (wasDeleted == expectedToDelete && countAfter == (expectedToDelete ?
				countBefore - 1 : countBefore)) {
			System.out.println("\n***Success*** The result was what the test case expected.\n");
			numPassed++;
		} else {
			System.out.println("\n***Failure*** The result was not what the test case expected.\n");
			numFailed++;
		}
	}
}