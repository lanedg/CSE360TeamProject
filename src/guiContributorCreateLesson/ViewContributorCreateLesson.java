package guiContributorCreateLesson;

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
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewContributorCreateLesson Class. </p>
 * 
 * <p> Description: The Java/FX-based view class for creating a new lesson as a Contributor. 
 * Provides input fields for lesson title, experience description, hours spent, and effort used, 
 * along with navigation and action buttons.</p>
 * 
 * @author Lane Gebbie
 * 
 * @version 1.00		2026-10-21 Initial version
 *  
 */
public class ViewContributorCreateLesson {
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	
	//GUI Area 1: Header and User Info
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
    protected static Button button_UpdateThisUser = new Button("Account Update");
    protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
    
    // GUI Area 2: Lesson Input Fields (mapping to LessonsLearned attributes)
    protected static Label label_LessonTitle = new Label("Lesson Title:");
    protected static TextField textfield_LessonTitle = new TextField();
    
    protected static Label label_ExperienceDesc = new Label("Experience Description:");
    protected static TextArea textarea_ExperienceDesc = new TextArea();
    
    protected static Label label_HoursSpent = new Label("Hours Spent:");
    protected static TextField textfield_HoursSpent = new TextField();
    
    protected static Label label_EffortUsed = new Label("Effort Used:");
    protected static TextField textfield_EffortUsed = new TextField();
    
    protected static Button button_SaveLesson = new Button("Save Lesson");
    protected static Label label_Message = new Label();

    // GUI Area 3: Bottom Navigation Separator and Buttons
    protected static Line line_Separator4 = new Line(20, 525, width-20, 525);
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    // Configuration Attributes
    private static ViewContributorCreateLesson theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;		
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    public static Scene theCreateLessonScene = null;
    
    /**********
     * <p> Method: displayCreateLesson(Stage ps, User user) </p>
     * 
     * <p> Description: Single entry point from outside this package to display the 
     * Create Lesson page. Initializes the stage and user references, clears previous inputs, 
     * and repaints the window.</p>
     * 
     * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
     * @param user specifies the User interacting with this GUI and its methods
     */
    public static void displayCreateLesson(Stage ps, User user) {
        theStage = ps;
        theUser = user;
        
        if (theView == null) theView = new ViewContributorCreateLesson();
        
        // Reset input fields upon opening
        textfield_LessonTitle.clear();
        textarea_ExperienceDesc.clear();
        textfield_HoursSpent.clear();
        textfield_EffortUsed.clear();
        label_Message.setText("");
        
        label_UserDetails.setText("User: " + theUser.getUserName());
        
        ControllerContributorCreateLesson.repaintTheWindow();
    }
    
    
    /**********
     * <p> Constructor: ViewContributorCreateLesson() </p>
     * 
     * <p> Description: Initializes all elements of the graphical user interface for the 
     * create lesson page, including labels, text fields, text areas, buttons, and layouts. 
     * Implemented as a singleton.</p>
     */
    public ViewContributorCreateLesson() {
        theRootPane = new Pane();
        theCreateLessonScene = new Scene(theRootPane, width, height);
        
        // GUI Area 1 setup
        label_PageTitle.setText("Create New Lesson Page");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        label_UserDetails.setText("User: ");
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
        
        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((_) -> 
            { guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
        
        // GUI Area 2 setup (Inputs for LessonsLearned)
        setupLabelUI(label_LessonTitle, "Arial", 16, 180, Pos.BASELINE_LEFT, 20, 120);
        setupTextFieldUI(textfield_LessonTitle, "Dialog", 14, 450, 220, 115);
        
        setupLabelUI(label_ExperienceDesc, "Arial", 16, 180, Pos.BASELINE_LEFT, 20, 165);
        textarea_ExperienceDesc.setLayoutX(220);
        textarea_ExperienceDesc.setLayoutY(165);
        textarea_ExperienceDesc.setPrefWidth(450);
        textarea_ExperienceDesc.setPrefHeight(100);
        
        setupLabelUI(label_HoursSpent, "Arial", 16, 180, Pos.BASELINE_LEFT, 20, 285);
        setupTextFieldUI(textfield_HoursSpent, "Dialog", 14, 450, 220, 280);
        
        setupLabelUI(label_EffortUsed, "Arial", 16, 180, Pos.BASELINE_LEFT, 20, 330);
        setupTextFieldUI(textfield_EffortUsed, "Dialog", 14, 450, 220, 325);
        
        setupButtonUI(button_SaveLesson, "Dialog", 16, 200, Pos.CENTER, 220, 380);
        button_SaveLesson.setOnAction((_) -> { ControllerContributorCreateLesson.performSaveLesson(); });
        
        setupLabelUI(label_Message, "Arial", 14, 500, Pos.BASELINE_LEFT, 220, 420);
        
        // GUI Area 3 setup
        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
        button_Return.setOnAction((_) -> { ControllerContributorCreateLesson.performReturn(); });

        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
        button_Logout.setOnAction((_) -> { ControllerContributorCreateLesson.performLogout(); });
    
        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
        button_Quit.setOnAction((_) -> { ControllerContributorCreateLesson.performQuit(); });
    }

    /**********
     * Protected local method to initialize the standard fields for a label
     * 
     * @param l		The Label object to be initialized
     * @param ff	The font to be used
     * @param f		The size of the font to be used
     * @param w		The minimum width of the Label
     * @param p		The alignment (e.g. left, centered, or right)
     * @param x		The location from the left edge (x axis)
     * @param y		The location from the top (y axis)
     */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);        
    }
    
    /**********
     * Protected local method to initialize the standard fields for a button
     * 
     * @param b		The Button object to be initialized
     * @param ff	The font to be used
     * @param f		The size of the font to be used
     * @param w		The minimum width of the Button
     * @param p		The alignment (e.g. left, centered, or right)
     * @param x		The location from the left edge (x axis)
     * @param y		The location from the top (y axis)
     */
    protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);        
    }

    /**********
     * Protected local method to initialize the standard fields for a text field
     * 
     * @param tf	The TextField object to be initialized
     * @param ff	The font to be used
     * @param f		The size of the font to be used
     * @param w		The minimum width of the TextField
     * @param x		The location from the left edge (x axis)
     * @param y		The location from the top (y axis)
     */
    protected static void setupTextFieldUI(TextField tf, String ff, double f, double w, double x, double y) {
        tf.setStyle("-fx-font: " + f + " " + ff + ";");
        tf.setMinWidth(w);
        tf.setLayoutX(x);
        tf.setLayoutY(y);
    }
    
}
