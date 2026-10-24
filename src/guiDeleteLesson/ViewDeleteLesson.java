package guiDeleteLesson;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.LessonsLearnedHelper;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import entityClasses.User;

/*******
 * <p> Title: ViewDeleteLesson Class. </p>
 * 
 * <p> Description: The Java/FX-based page that allows a contributor to delete a lesson learned
 * that they have added to the system.  The page lists only the lessons this contributor added,
 * as a contributor is not able to see the submissions of other contributors.  A lesson that has
 * been locked, or that has a locked field, may not be deleted, so the page reports the lock
 * state of the selected lesson and refuses the deletion when it is locked.</p>
 * 
 * <p> Copyright: Beck 2026 </p>
 * 
 * @author Beck
 * 
 * @version 1.00		2026-10-21 Initial version
 *  
 */

public class ViewDeleteLesson {

	/*-*******************************************************************************************

	Attributes
	
	*/

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI.  There are 3 areas for this GUI.

	// GUI Area 1: It informs the user about the purpose of this page, whose account is being
	// used, and a button to allow this user to update the account settings.
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// When no lesson has been selected, only Area 2a is shown.  If a lesson in the ComboBox in
	// Area 2a has been specified, then Area 2b is made visible.

	// Area 2a: This allows the contributor to select one of the lessons they have added as the
	// first step in deleting it.  The act of selecting a lesson causes the change in the GUI.
	// The contributor does not need to push a button to make this happen.
	protected static Label label_SelectLesson = new Label("Select a lesson to be deleted:");
	protected static ComboBox <String> combobox_SelectLesson = new ComboBox <String>();

	// Area 2b: When a lesson has been selected these widgets are shown and can be used
	protected static Label label_LessonStatus = new Label();
	protected static Button button_DeleteLesson = new Button("Delete This Lesson");

	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20, 525);

	// GUI Area 3: This is the last of the GUI areas.  It is used for quitting the application,
	// logging out, and returning to the page that invoked this one.
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.

	// These attributes are used to configure the page and populate it with this user's
	// information
	private static ViewDeleteLesson theView;	// Used to determine if instantiation of the class
												// is needed

	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current user of the application

	public static Scene theDeleteLessonScene = null;	// The Scene each invocation populates
	protected static String theSelectedLesson = "";		// The lesson that is to be deleted

	// The first entry of the ComboBox is a prompt rather than one of the contributor's lessons
	protected static final String SELECT_PROMPT = "<Select a Lesson>";


	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayDeleteLesson(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Delete Lesson page to be displayed.
	 * 
	 * It first sets up the shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class,
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it
	 * visible to the user.</p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 * 
	 * @param user specifies the User whose lessons may be deleted
	 *
	 */
	public static void displayDeleteLesson(Stage ps, User user) {

		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;

		// If not yet established, populate the static aspects of the GUI by creating the
		// singleton instance of this class
		if (theView == null) theView = new ViewDeleteLesson();

		// Repopulate the list of this contributor's lessons, as lessons may have been added or
		// removed since the last time this page was shown
		refreshLessonList();

		// Default to no lesson selected
		combobox_SelectLesson.getSelectionModel().select(0);

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page has two modes (1: lesson has not been selected, and
		// 2: lesson has been selected) so there are two lists of widgets to be displayed.  For
		// this reason, the following two controller methods deal with this dynamic aspect.
		ControllerDeleteLesson.repaintTheWindow();
		ControllerDeleteLesson.doSelectLesson();
	}


	/**********
	 * <p> Method: ViewDeleteLesson() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.
	 * 
	 * This is a singleton, so this is performed just once.  Subsequent uses fill in the
	 * changeable fields using the displayDeleteLesson method.</p>
	 * 
	 */
	public ViewDeleteLesson() {

		// This page is used by the contributor role, so the role is not specified here

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theDeleteLessonScene = new Scene(theRootPane, width, height);

		// Populate the window with the title and other common widgets and set their static state

		// GUI Area 1
		label_PageTitle.setText("Delete Lesson Learned Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) ->
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

		// GUI Area 2a
		setupLabelUI(label_SelectLesson, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);

		setupComboBoxUI(combobox_SelectLesson, "Dialog", 16, 400, 300, 125);
		refreshLessonList();
		combobox_SelectLesson.getSelectionModel().select(0);
		combobox_SelectLesson.getSelectionModel().selectedItemProperty()
			.addListener((@SuppressWarnings("unused") ObservableValue<? extends String> observable,
				@SuppressWarnings("unused") String oldValue,
				@SuppressWarnings("unused") String newValue)
					-> {ControllerDeleteLesson.doSelectLesson();});

		// GUI Area 2b
		setupLabelUI(label_LessonStatus, "Arial", 16, 700, Pos.BASELINE_LEFT, 50, 180);

		setupButtonUI(button_DeleteLesson, "Dialog", 16, 200, Pos.CENTER, 50, 230);
		button_DeleteLesson.setOnAction((_) -> {ControllerDeleteLesson.performDeleteLesson(); });

		// GUI Area 3
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {ControllerDeleteLesson.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {ControllerDeleteLesson.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {ControllerDeleteLesson.performQuit(); });

		// This is the end of the GUI Widgets for the page

		// Due to the dynamic nature of this page, setting the widgets into the Root Pane has been
		// delegated to the repaintTheWindow and doSelectLesson controller methods.
	}


	/*-*******************************************************************************************

	Helper methods used to minimize the number of lines of code needed above
	
	*/

	/**********
	 * <p> Method: void refreshLessonList() </p>
	 * 
	 * <p> Description: Fetch the list of lessons this contributor has added and place it into
	 * the ComboBox.  Only this contributor's lessons are listed, as the Contributor user story
	 * states that a contributor is unable to see submissions from other contributors.  The
	 * ComboBox holds Strings, so the display text for each lesson is built here, with the id
	 * first so the Controller can tell which lesson was selected.</p>
	 * 
	 */
	protected static void refreshLessonList() {
		List<String> lessonList = new ArrayList<String>();
		lessonList.add(SELECT_PROMPT);

		LessonsLearnedList theLessons =
				LessonsLearnedHelper.getLessonsForAuthor(theUser.getUserName());
		for (LessonsLearned lesson : theLessons.getLessonsList()) {
			lessonList.add(lesson.getId() + " - " + lesson.getLessonTitle());
		}

		combobox_SelectLesson.setItems(FXCollections.observableArrayList(lessonList));
	}


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
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}


	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w,
			double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}