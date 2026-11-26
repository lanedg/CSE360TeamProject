package guiCuratorHome;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewCuratorHome Class. </p>
 *
 * <p> Description: The Java/FX-based Curator Home Page.  This is the page a curator reaches after
 * logging in, and it is the single place from which every curator task is started: locking a
 * lesson or one of its fields, merging the time and effort data of several lessons into
 * statistics, building the list of AI-assisted search terms for a lesson, and using an AI tool to
 * refine a lesson's wording.
 *
 * The page follows the pattern of the Role1 and Viewer home pages, so a curator finds the account
 * update, logout and quit controls in the places they occupy on every other home page.</p>
 *
 * <p> Copyright: Beck 2026 </p>
 *
 * @author Beck
 *
 * @version 1.00		2026-11-26 Initial version
 *
 */

public class ViewCuratorHome {

	/*-*******************************************************************************************

	Attributes

	*/

	// These are the application values required by the user interface

	/** The width of the application window, taken from the main application class */
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;

	/** The height of the application window, taken from the main application class */
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// GUI Area 1: It informs the user about the purpose of this page, whose account is being
	// used, and a button to allow this user to update the account settings

	/** The title shown at the top of the page */
	protected static Label label_PageTitle = new Label();

	/** The line that states which user is currently logged in */
	protected static Label label_UserDetails = new Label();

	/** The button that takes this user to the account update page */
	protected static Button button_UpdateThisUser = new Button("Account Update");

	/** The separator that partitions the title area from the rest of the page */
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: One button for each of the four curator tasks

	/** Opens the page on which a lesson or one of its fields is locked */
	protected static Button button_LockLessons = new Button("Lock Lessons and Fields");

	/** Opens the page on which the time and effort data of several lessons is merged */
	protected static Button button_MergeData = new Button("Merge Time and Effort Data");

	/** Opens the page on which the AI suggests search terms for a lesson */
	protected static Button button_SearchTerms = new Button("Manage Search Terms");

	/** Opens the page on which the AI suggests clearer wording for a lesson */
	protected static Button button_EditLesson = new Button("Refine a Lesson");

	/** The separator that partitions the page from the buttons at the bottom */
	protected static Line line_Separator4 = new Line(20, 525, width-20, 525);

	// GUI Area 3: This is the last of the GUI areas.  It is used for logging out and for quitting

	/** The button that logs this user out and returns to the login page */
	protected static Button button_Logout = new Button("Logout");

	/** The button that terminates the application */
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page

	/** The singleton instance of this class, used to determine if instantiation is needed */
	private static ViewCuratorHome theView;

	/** Reference for the in-memory database so this package has access */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The Stage that JavaFX has established for us */
	protected static Stage theStage;

	/** The Pane that holds all the GUI widgets */
	protected static Pane theRootPane;

	/** The current logged in User */
	protected static User theUser;

	/** The shared Scene each invocation populates */
	private static Scene theViewCuratorHomeScene;

	/** Admin: 1; Role1: 2; Role2: 3; Viewer: 4; Curator: 5 */
	protected static final int theRole = 5;


	/*-*******************************************************************************************

	Constructors

	*/

	/**********
	 * <p> Method: displayCuratorHome(Stage ps, User user) </p>
	 *
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Curator Home page to be displayed.
	 *
	 * It first sets up the shared attributes so we do not have to pass parameters.
	 *
	 * It then checks to see if the page has been setup.  If not, it instantiates the class and
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 *
	 * After the instantiation, the code populates the elements that change based on the user and
	 * the system's current state, sets the Scene onto the stage, and makes it visible.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 *
	 * @param user specifies the User for this GUI and its methods
	 *
	 * <p> Validated by: Manual Tests.pdf (MT-C1) </p>
	 *
	 */
	public static void displayCuratorHome(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewCuratorHome();

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		label_UserDetails.setText("User: " + theUser.getUserName());

		// Set the title for the window, display the page, and wait for the curator to act
		theStage.setTitle("CSE 360 Foundations: Curator Home Page");
		theStage.setScene(theViewCuratorHomeScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewCuratorHome() </p>
	 *
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, colour, and change and event handlers for
	 * each GUI object.
	 *
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayCuratorHome method.</p>
	 *
	 */
	private ViewCuratorHome() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewCuratorHomeScene = new Scene(theRootPane, width, height);

		// Populate the window with the title and other common widgets and set their static state

		// GUI Area 1
		label_PageTitle.setText("Curator Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {ControllerCuratorHome.performUpdate(); });

		// GUI Area 2
		setupButtonUI(button_LockLessons, "Dialog", 18, 300, Pos.CENTER, 20, 140);
		button_LockLessons.setOnAction((_) -> {ControllerCuratorHome.performLockLessons(); });

		setupButtonUI(button_MergeData, "Dialog", 18, 300, Pos.CENTER, 20, 220);
		button_MergeData.setOnAction((_) -> {ControllerCuratorHome.performMergeData(); });

		setupButtonUI(button_SearchTerms, "Dialog", 18, 300, Pos.CENTER, 20, 300);
		button_SearchTerms.setOnAction((_) -> {ControllerCuratorHome.performSearchTerms(); });

		setupButtonUI(button_EditLesson, "Dialog", 18, 300, Pos.CENTER, 20, 380);
		button_EditLesson.setOnAction((_) -> {ControllerCuratorHome.performEditLesson(); });

		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> {ControllerCuratorHome.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> {ControllerCuratorHome.performQuit(); });

		// This is the end of the GUI initialization code

		// Place all of the widget items into the Root Pane's list of children
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
				button_LockLessons, button_MergeData, button_SearchTerms, button_EditLesson,
				line_Separator4, button_Logout, button_Quit);
	}


	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above

	*/

	/**********
	 * Private local method to initialize the standard fields for a label
	 *
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}


	/**********
	 * Private local method to initialize the standard fields for a button
	 *
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}