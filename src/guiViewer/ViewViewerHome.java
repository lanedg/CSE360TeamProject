package guiViewer;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.LessonsLearnedList;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewViewerHome Class. </p>
 * 
 * <p> Description: The ViewerHome Page view. Allows a Viewer to browse all
 * lessons learned, filter by keyword on Enter or button click, select a lesson, and open
 * it in a dedicated view. </p>
 * 
 * @author Lane Gebbie
 * 
 * @version 1.00		2026-11-25 Initial version
 *  
 */

public class ViewViewerHome {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: Page title, user details, and account update button
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	// GUI Area 2: Keyword search bar and lessons list
	protected static Label label_Search = new Label("Search:");
	protected static TextField text_Search = new TextField();
	protected static Button button_Search = new Button("Search");
	protected static ListView<String> list_Lessons = new ListView<>();

	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	// GUI Area 3: View Lesson, Logout, and Quit buttons
	protected static Button button_ViewLesson = new Button("View Lesson");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewViewerHome theView;

	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	protected static LessonsLearnedList allLessons = new LessonsLearnedList();
	protected static LessonsLearned selectedLesson = null;

	private static Scene theViewViewerHomeScene;
	protected static final int theRole = 4;

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayViewerHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: Single entry point from outside this package to display the Viewer Home
	 * page. Initializes the singleton view if needed, refreshes user and lesson data, resets
	 * search and selection state, and shows the scene on the stage. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 * @param user specifies the User interacting with this GUI and its methods
	 */
	public static void displayViewerHome(Stage ps, User user) {

		theStage = ps;
		theUser = user;

		if (theView == null) {
			theView = new ViewViewerHome();
		}

		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		label_UserDetails.setText("User: " + theUser.getUserName());
		text_Search.setText("");
		selectedLesson = null;
		button_ViewLesson.setDisable(true);
		ControllerViewerHome.refreshLessonList();

		theStage.setTitle("CSE 360 Foundations: Viewer Home Page");
		theStage.setScene(theViewViewerHomeScene);
		theStage.show();
	}

	/**********
	 * <p> Constructor: ViewViewerHome() </p>
	 * 
	 * <p> Description: Initializes all GUI widgets, layouts, selection listeners, and button
	 * actions for the Viewer Home page. Implemented as a singleton. </p>
	 */
	private ViewViewerHome() {

		theRootPane = new Pane();
		theViewViewerHomeScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Viewer Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> { ControllerViewerHome.performUpdate(); });

		// GUI Area 2
		setupLabelUI(label_Search, "Arial", 18, 80, Pos.BASELINE_LEFT, 20, 112);

		setupTextUI(text_Search, "Arial", 16, 460, Pos.BASELINE_LEFT, 100, 108, true);
		text_Search.setPromptText("Enter keyword in title or description...");
		text_Search.setOnAction((_) -> { ControllerViewerHome.performSearch(); });

		setupButtonUI(button_Search, "Dialog", 16, 130, Pos.CENTER, 580, 106);
		button_Search.setOnAction((_) -> { ControllerViewerHome.performSearch(); });

		list_Lessons.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
		list_Lessons.setLayoutX(20);
		list_Lessons.setLayoutY(150);
		list_Lessons.setPrefWidth(width - 40);
		list_Lessons.setPrefHeight(360);
		list_Lessons.getSelectionModel().selectedIndexProperty().addListener((_, _, _) -> {
			ControllerViewerHome.performSelectLesson();
		});

		// GUI Area 3
		setupButtonUI(button_ViewLesson, "Dialog", 18, 220, Pos.CENTER, 20, 540);
		button_ViewLesson.setDisable(true);
		button_ViewLesson.setOnAction((_) -> { ControllerViewerHome.performViewLesson(); });

		setupButtonUI(button_Logout, "Dialog", 18, 220, Pos.CENTER, 270, 540);
		button_Logout.setOnAction((_) -> { ControllerViewerHome.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 220, Pos.CENTER, 520, 540);
		button_Quit.setOnAction((_) -> { ControllerViewerHome.performQuit(); });

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
				label_Search, text_Search, button_Search, list_Lessons,
				line_Separator4, button_ViewLesson, button_Logout, button_Quit);
	}

	/*-*******************************************************************************************

	Helper methods to reduce code length

	 */

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The minimum width of the Label
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
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
	 * @param w		The minimum width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 * 
	 * @param t		The TextField object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the TextField
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param e		True if the TextField is editable, false otherwise
	 */
	private static void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x,
			double y, boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
}