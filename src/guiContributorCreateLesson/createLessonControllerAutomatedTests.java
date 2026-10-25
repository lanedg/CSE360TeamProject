package guiContributorCreateLesson;

import javafx.application.Platform;

import guiContributorCreateLesson.ControllerContributorCreateLesson;
import guiContributorCreateLesson.ViewContributorCreateLesson;
import database.LessonsLearnedHelper;
import entityClasses.User;

/*******
 * <p> Title: createLessonControllerAutomatedTests Class. </p>
 * 
 * <p> Description: Automated command-line tests for the contributor create lesson 
 * controller and view validation logic. Simulates user input in JavaFX fields, 
 * triggers controller methods, and validates UI error messages and database updates.</p>
 * 
 * @author Lane Gebbie
 * 
 * @version 1.00	2026-10-24	Initial controller/view automation tests
 * 
 */
public class createLessonControllerAutomatedTests {

	// Counter of the number of passed tests 
	static int numPassed = 0;

	// Counter of the number of failed tests
	static int numFailed = 0;

	/*******
	 * <p> Method: void main(String[] args) </p>
	 * 
	 * <p> Description: Initializes the JavaFX runtime toolkit, sets up a mock user session,
	 * runs each test case, and prints a summary report.</p>
	 * 
	 * @param args command line parameters (unused)
	 */
	public static void main(String[] args) {
		System.out.println("____________________________________________________________");
		System.out.println("\nCreate Lesson Controller & View Validation Testing Automation");

		// Initialize the JavaFX runtime toolkit so UI components can be manipulated headless
		try {
			Platform.startup(() -> {});
		} catch (IllegalStateException e) {
			// Toolkit may already be running if invoked multiple times
		}

		// Ensure the database table exists
		LessonsLearnedHelper.initializeTable();

		// Set up a mock logged-in user session required by the controller, null values are used for all unnecessary values 
		ViewContributorCreateLesson.theUser = new User("testContributor", null, null, null, null, null, null, false, true, false);

		// Test Case 1: Empty title triggers GUI validation error 
		performControllerTest(
			1, 
			"Empty lesson title is caught by controller validation", 
			"", 
			"Some description", 
			"2.0", 
			"Medium", 
			"Error: Lesson Title cannot be empty.", 
			false
		);

		// Test Case 2: Invalid hours format triggers GUI validation error 
		performControllerTest(
			2, 
			"Non-numeric hours spent is caught by controller validation", 
			"Valid Title", 
			"Some description", 
			"invalid-hours", 
			"Medium", 
			"Error: Hours Spent must be a valid number.", 
			false
		);

		//Test Case 3: Valid inputs successfully save through controller 
		performControllerTest(
			3, 
			"Valid inputs successfully pass validation and save to database", 
			"Controller Tested Lesson", 
			"Learned how controller validation works.", 
			"3.5", 
			"High", 
			"Lesson successfully created!", 
			true
		);

		// Testing automation report footer
		System.out.println("____________________________________________________________");
		System.out.println("\nNumber of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
		
		System.exit(0);
	}

	/*******
	 * <p> Method: void performControllerTest(...) </p>
	 * 
	 * <p> Description: Populates the View text fields, runs ControllerContributorCreateLesson.performSaveLesson(),
	 * and checks if both the expected message text and database count changes match expectations.</p>
	 */
	private static void performControllerTest(int testCase, String description, String title,
			String desc, String hours, String effort, String expectedMessage, boolean expectedDbChange) {
		
		System.out.println("____________________________________________________________\n");
		System.out.println("Test case: " + testCase + "\nTesting: " + description);

		// Populate View fields directly (simulating user typing)
		ViewContributorCreateLesson.textfield_LessonTitle.setText(title);
		ViewContributorCreateLesson.textarea_ExperienceDesc.setText(desc);
		ViewContributorCreateLesson.textfield_HoursSpent.setText(hours);
		ViewContributorCreateLesson.textfield_EffortUsed.setText(effort);
		ViewContributorCreateLesson.label_Message.setText("");

		int countBefore = LessonsLearnedHelper.getLessonCount();

		// Invoke the controller method directly
		ControllerContributorCreateLesson.performSaveLesson();

		int countAfter = LessonsLearnedHelper.getLessonCount();
		String actualMessage = ViewContributorCreateLesson.label_Message.getText();

		System.out.println("Expected Message: " + expectedMessage);
		System.out.println("Actual Message:   " + actualMessage);
		System.out.println("Database count change: " + countBefore + " -> " + countAfter);

		boolean messageMatches = actualMessage.equals(expectedMessage);
		boolean dbMatches = expectedDbChange ? (countAfter == countBefore + 1) : (countAfter == countBefore);

		if (messageMatches && dbMatches) {
			System.out.println("\n***Success*** The result was what the test case expected.\n");
			numPassed++;
		} else {
			System.out.println("\n***Failure*** The result was not what the test case expected.\n");
			numFailed++;
		}
	}
}