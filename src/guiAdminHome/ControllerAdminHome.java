package guiAdminHome;

import database.Database;

import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

/*******
 * <p>
 * Title: GUIAdminHomePage Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based Admin Home Page. This class provides the
 * controller actions basic on the user's use of the JavaFX GUI widgets defined
 * by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.
 * WHen those buttons are pressed, an alert pops up to tell the user that the
 * function associated with the button has not been implemented. Also, be aware
 * that What has been implemented may not work the way the final product
 * requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only
 * class methods that can invoke these methods. This is why each has been
 * declared at "protected". Do not change any of these methods to public.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00 2025-08-17 Initial version
 * @version 1.01 2025-09-16 Update Javadoc documentation *
 */

public class ControllerAdminHome {

	/*-*******************************************************************************************
	
	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerAdminHome() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p>
	 * 
	 * Title: performInvitation () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method to send an email inviting a potential user to
	 * establish an account and a specific role.
	 * </p>
	 */
	protected static void performInvitation() {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}

		// Check to ensure that we are not sending a second message with a new
		// invitation code to
		// the same email address.
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText("An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}

		// Inform the user that the invitation has been sent and display the invitation
		// code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress, theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + " was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();

		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations
				.setText("Number of outstanding invitations: " + theDatabase.getNumberOfInvitations());
	}

	/**********
	 * <p>
	 * 
	 * Title: manageInvitations () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that is currently a stub informing the user
	 * that this function has not yet been implemented.
	 * </p>
	 */
	protected static void manageInvitations() {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}

	/**********
	 * <p>
	 * 
	 * Title: setOnetimePassword () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that is currently a stub informing the user
	 * that this function has not yet been implemented.
	 * </p>
	 */
	protected static void setOnetimePassword() {
		System.out.println("\n*** WARNING ***: One-Time Password Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("One-Time Password Issue");
		ViewAdminHome.alertNotImplemented.setContentText("One-Time Password Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}

	/**********
	 * <p>
	 * 
	 * Title: deleteUser () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that allows an Admin to remove a user account
	 * so deleted users can no longer operate the system. An Admin cannot remove
	 * their own Admin access, so no account can be removed if doing so would leave
	 * the system without an admin.
	 * </p>
	 * 
	 */
	protected static void deleteUser() {
		// Fetch list of users. First entry is the "<Select a User>" placeholder.
		List<String> userList = theDatabase.getUserList();
		if (userList == null || userList.size() <= 1) {
			showAlert("Delete User", "There are no user accounts to delete.");
			return;
		}

		// Let the admin choose who to remove. Choice dialog should be used instead of
		// text so that the admin can only name an account that exists.
		ChoiceDialog<String> selectUser = new ChoiceDialog<>(userList.get(0), userList);
		selectUser.setTitle("Delete User");
		selectUser.setHeaderText("Select the user account to remove.");
		selectUser.setContentText("User:");

		Optional<String> selection = selectUser.showAndWait();
		if (selection.isEmpty())
			return;

		String theUserName = selection.get();
		if (theUserName.equals(userList.get(0))) {
			showAlert("Delete User", "No User was selected");
			return;
		}

		// An Admin is not allowed to remove their own access
		String currentAdmin = ViewAdminHome.theUser.getUserName();
		if (theUserName.equals(currentAdmin)) {
			showAlert("Delete User",
					"An admin may not remove their own access.\n" + "Another admin must perform this action.");
			return;
		}

		// At least one admin must remain available.
		if (theDatabase.isUserAdmin(theUserName) && theDatabase.getNumberOfAdmins() <= 1) {
			showAlert("Delete User", "This is the only remaining Admin.\n"
					+ "Removing this account would leave nobody to be able to use admin functions.");
			return;
		}

		// Require an explicit "Yes" before account is removed
		TextInputDialog areYouSure = new TextInputDialog();
		areYouSure.setTitle("Are you sure?");
		areYouSure.setHeaderText("Remove the account for \"" + theUserName + "\"?");
		areYouSure.setContentText("Type 'Yes' to confirm: ");

		Optional<String> confirmation = areYouSure.showAndWait();
		if (confirmation.isEmpty())
			return; // Admin canceled dialog

		// Check the size of this input before it's used.
		String answer = confirmation.get();
		String lengthError = inputValidation.ValidateInputLength.checkInputLength(answer);
		if (lengthError.length() != 0) {
			showAlert("Delete User", lengthError);
			return;
		}

		// Anything other than "Yes" leaves the account in place
		if (!answer.trim().equalsIgnoreCase("Yes")) {
			showAlert("Delete User", "The account was not removed.");
			return;
		}

		// Perform the removal and report outcome
		if (theDatabase.deleteUser(theUserName)) {
			System.out.println("User account removed: " + theUserName);
			showAlert("Delete User", "The account for \"" + theUserName + "\" was removed.");
		} else {
			showAlert("Delete User", "The account could not be removed. Please try again.");
		}
	}

	/*****
	 * <p>
	 * 
	 * Title: showAlert () Method.
	 * <p>
	 * 
	 * <p>
	 * Description: Private helper that displays an informational message to the
	 * admin,
	 * </p>
	 * 
	 * @param header  the heading text for the alert
	 * 
	 * @param message the body for the alert
	 * 
	 */
	private static void showAlert(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Admin");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**********
	 * <p>
	 * 
	 * Title: listUsers () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that is currently a stub informing the user
	 * that this function has not yet been implemented.
	 * </p>
	 */
	protected static void listUsers() {
		System.out.println("\n*** WARNING ***: List Users Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("List User Issue");
		ViewAdminHome.alertNotImplemented.setContentText("List Users Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}

	/**********
	 * <p>
	 * 
	 * Title: addRemoveRoles () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that allows an admin to add and remove roles
	 * for any of the users currently in the system. This is done by invoking the
	 * AddRemoveRoles Page. There is no need to specify the home page for the return
	 * as this can only be initiated by and Admin.
	 * </p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}

	/**********
	 * <p>
	 * 
	 * Title: invalidEmailAddress () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that is intended to check an email address
	 * before it is used to reduce errors. The code currently only checks to see
	 * that the email address is not empty. In the future, a syntactic check must be
	 * performed and maybe there is a way to check if a properly email address is
	 * active.
	 * </p>
	 * 
	 * @param emailAddress This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		String emailAdressValid = inputValidation.ValidateEmailInput.checkEmailAddress(emailAddress);
		if (emailAdressValid.length() != 0) {
			ViewAdminHome.alertEmailError.setContentText(emailAdressValid);
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}

	/**********
	 * <p>
	 * 
	 * Title: performLogout () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that logs this user out of the system and
	 * returns to the login page for future use.
	 * </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}

	/**********
	 * <p>
	 * 
	 * Title: performQuit () Method.
	 * </p>
	 * 
	 * <p>
	 * Description: Protected method that gracefully terminates the execution of the
	 * program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
