package guiListUsers;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import entityClasses.User;

/*******
 * <p> Title: ViewListUsers Class. </p>
 * 
 * <p> Description: The List Users Screen. This screen allows an Admin 
 * to view all user accounts in the system. It uses a singleton pattern so the 
 * application only creates this window once to save memory.
 *  
 */

public class ViewListUsers {

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	protected static ListView<String> list_Users = new ListView<>();
	private static Line line_Separator2 = new Line(20, 525, width-20, 525);

	protected static Button button_Return = new Button("Return to Admin Home");

	private static ViewListUsers theView;
	protected static Stage theStage;
	private static Pane theRootPane;
	protected static User theUser;
	private static Scene theListUsersScene;

	public static void displayListUsers(Stage ps, User user) {
		theStage = ps;
		theUser = user;
		
		if (theView == null) {
			theView = new ViewListUsers();
		}
		
		label_UserDetails.setText("Admin: " + theUser.getUserName());
		ControllerListUsers.populateTable();
				
		theStage.setTitle("CSE 360 Foundation Code: List Users Page");
		theStage.setScene(theListUsersScene);
		theStage.show();
	}
	
	private ViewListUsers() {
		theRootPane = new Pane();
		theListUsersScene = new Scene(theRootPane, width, height);
	
		label_PageTitle.setText("System User Accounts");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: ");
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		list_Users.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
		list_Users.setLayoutX(20);
		list_Users.setLayoutY(110);
		list_Users.setPrefWidth(width - 40);
		list_Users.setPrefHeight(400);

		setupButtonUI(button_Return, "Dialog", 18, 250, Pos.CENTER, (width/2) - 125, 540);
		button_Return.setOnAction((_) -> { ControllerListUsers.performBack(); });

		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, line_Separator1,
			list_Users, line_Separator2, button_Return
		);
	}

	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f)); l.setMinWidth(w); l.setAlignment(p); l.setLayoutX(x); l.setLayoutY(y);		
	}
	
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f)); b.setMinWidth(w); b.setAlignment(p); b.setLayoutX(x); b.setLayoutY(y);		
	}
}