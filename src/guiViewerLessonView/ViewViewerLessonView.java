package guiViewerLessonView;

import database.Database;
import entityClasses.LessonsLearned;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewViewerLessonView Class. </p>
 * 
 * <p> Description: The Java/FX-based page for viewing an individual lesson learned.
 * Displays title, experience description, hours spent, and effort used in read-only
 * fields. </p>
 * 
 * @author Lane Gebbie
 * 
 * @version 1.00		2026-11-25 Initial version
 *  
 */

public class ViewViewerLessonView {

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

	// GUI Area 2: Individual lesson fields
	protected static Label label_LessonTitle = new Label("Title:");
	protected static TextField text_LessonTitle = new TextField();

	protected static Label label_HoursSpent = new Label("Hours Spent:");
	protected static TextField text_HoursSpent = new TextField();

	protected static Label label_EffortUsed = new Label("Effort Used:");
	protected static TextField text_EffortUsed = new TextField();

	protected static Label label_ExperienceDescription = new Label("Experience Description:");
	protected static TextArea text_ExperienceDescription = new TextArea();

	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	// GUI Area 3: Return, Logout, and Quit buttons
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	private static ViewViewerLessonView theView;

	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;
	protected static LessonsLearned theLesson;

	private static Scene theViewerLessonViewScene;
	protected static final int theRole = 4;

	/*-*******************************************************************************************

	Constructors

	 */

	/**********
	 * <p> Method: displayViewerLessonView(Stage ps, User user, LessonsLearned lesson) </p>
	 * 
	 * <p> Description: Single entry point from outside this package to display the Lesson
	 * Details page. Initializes the singleton view if needed, refreshes user account details,
	 * populates the lesson fields, and shows the scene on the stage. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 * @param user specifies the User interacting with this GUI and its methods
	 * @param lesson specifies the LessonsLearned record to display
	 */
	public static void displayViewerLessonView(Stage ps, User user, LessonsLearned lesson) {

		theStage = ps;
		theUser = user;
		theLesson = lesson;

		if (theView == null) {
			theView = new ViewViewerLessonView();
		}

		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;

		label_UserDetails.setText("User: " + theUser.getUserName());
		ControllerViewerLessonView.populateLessonFields(theLesson);

		theStage.setTitle("CSE 360 Foundations: Lesson Details");
		theStage.setScene(theViewerLessonViewScene);
		theStage.show();
	}

	/**********
	 * <p> Constructor: ViewViewerLessonView() </p>
	 * 
	 * <p> Description: Initializes all GUI widgets, layouts, read-only field settings, and
	 * button actions for the Lesson Details page. Implemented as a singleton. </p>
	 */
	private ViewViewerLessonView() {

		theRootPane = new Pane();
		theViewerLessonViewScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Lesson Learned Details");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> { ControllerViewerLessonView.performUpdate(); });

		// GUI Area 2
		setupLabelUI(label_LessonTitle, "Arial", 16, 180, Pos.BASELINE_LEFT, 20, 115);
		setupTextUI(text_LessonTitle, "Arial", 16, width - 220, Pos.BASELINE_LEFT, 200, 110, false);

		setupLabelUI(label_HoursSpent, "Arial", 16, 180, Pos.BASELINE_LEFT, 20, 160);
		setupTextUI(text_HoursSpent, "Arial", 16, 200, Pos.BASELINE_LEFT, 200, 155, false);

		setupLabelUI(label_EffortUsed, "Arial", 16, 180, Pos.BASELINE_LEFT, 20, 205);
		setupTextUI(text_EffortUsed, "Arial", 16, width - 220, Pos.BASELINE_LEFT, 200, 200, false);

		setupLabelUI(label_ExperienceDescription, "Arial", 16, 220, Pos.BASELINE_LEFT, 20, 250);
		text_ExperienceDescription.setFont(Font.font("Arial", 15));
		text_ExperienceDescription.setLayoutX(20);
		text_ExperienceDescription.setLayoutY(280);
		text_ExperienceDescription.setPrefWidth(width - 40);
		text_ExperienceDescription.setPrefHeight(225);
		text_ExperienceDescription.setEditable(false);
		text_ExperienceDescription.setWrapText(true);

		// GUI Area 3
		setupButtonUI(button_Return, "Dialog", 18, 220, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> { ControllerViewerLessonView.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 220, Pos.CENTER, 270, 540);
		button_Logout.setOnAction((_) -> { ControllerViewerLessonView.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 220, Pos.CENTER, 520, 540);
		button_Quit.setOnAction((_) -> { ControllerViewerLessonView.performQuit(); });

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
				label_LessonTitle, text_LessonTitle,
				label_HoursSpent, text_HoursSpent,
				label_EffortUsed, text_EffortUsed,
				label_ExperienceDescription, text_ExperienceDescription,
				line_Separator4, button_Return, button_Logout, button_Quit);
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