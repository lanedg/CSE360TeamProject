package guiListUsers;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import javafx.collections.FXCollections;
import java.util.ArrayList;

/*******
 * <p> Title: ControllerListUsers Class. </p>
 * 
 * <p> Description: The List Users Controller. This grabs user data from the database, 
 * formats it into clean text, and sends it to the View to be displayed. </p>
 * 
 */

public class ControllerListUsers {
	
	private static Database theDatabase = FoundationsMain.database;

	protected static void performBack() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewListUsers.theStage, ViewListUsers.theUser);
	}
	
	protected static void populateTable() {
		ArrayList<User> allUsersList = theDatabase.getAllUsers();
		ArrayList<String> displayStrings = new ArrayList<>();
		
		for (User u : allUsersList) {
			String name = u.getFirstName() != null ? u.getFirstName() : "";
			if (u.getMiddleName() != null && !u.getMiddleName().isEmpty()) {
				name += " " + u.getMiddleName();
			}
			if (u.getLastName() != null && !u.getLastName().isEmpty()) {
				name += " " + u.getLastName();
			}
			if (name.trim().isEmpty()) name = "Not Provided";
			
			String roles = "";
			if (u.getAdminRole()) roles += "Admin, ";
			if (u.getNewRole1()) roles += "Role 1, ";
			if (u.getNewRole2()) roles += "Role 2, ";
			
			if (roles.isEmpty()) {
				roles = "None";
			} else {
				roles = roles.substring(0, roles.length() - 2); 
			}
			
			String row = String.format("Username: %s   |   Name: %s   |   Email: %s   |   Roles: %s", 
					u.getUserName(), name.trim(), u.getEmailAddress(), roles);
			
			displayStrings.add(row);
		}
		
		ViewListUsers.list_Users.setItems(FXCollections.observableArrayList(displayStrings));
	}
}