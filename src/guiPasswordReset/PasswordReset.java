package guiPasswordReset;

import java.security.SecureRandom;
import database.Database;
import entityClasses.User;
import inputValidation.ValidateInputLength;
import inputValidation.ValidatePasswordInput;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import applicationMain.FoundationsMain;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class PasswordReset {
	private static final SecureRandom randomchars = new SecureRandom();
	public static String generateOTP() {
		StringBuilder otp = new StringBuilder();
		for (byte b = 0; b <8; b++) {
			otp.append(randomchars.nextInt(10));
		}
		return otp.toString();
	}
	private static void showMessage(Stage stage, AlertType type, String message) {
		Alert alert = new Alert(type);
		alert.initOwner(stage);
		alert.setContentText(message);
		alert.setTitle("Password Reset");
		alert.setHeaderText(null);
		alert.showAndWait();
	}
	public static void adminDialog(Stage stage, User admin) {
		if(admin == null || !admin.getAdminRole()) {
			showMessage(stage, AlertType.ERROR, "Only admins can reset passwords"); return;
		}
		Database db = FoundationsMain.database;
		List<String> users = db.getUserList();
		if(users==null) {
			showMessage(stage, AlertType.ERROR, "Can't load users"); return;
		}
		users.set(0, "<Select>");
		ChoiceDialog<String> userSelection = new ChoiceDialog<>("<Select>", users);
		userSelection.initOwner(stage);
		userSelection.setHeaderText("Select the user to receive the OTP");
		userSelection.setContentText("Username: ");
		userSelection.setTitle("Password Reset");
		String username = userSelection.showAndWait().orElse(null);
		if (username==null) return;
		if(username.equals("<Select>")) { showMessage(stage, AlertType.ERROR, "Please select a user"); return; }
		String otp = generateOTP();
		try {
			if(db.setOTP(username ,otp)) { showMessage(stage,AlertType.INFORMATION, "Username: " + username + " \nOTP: " + otp); }
			else { showMessage(stage, AlertType.ERROR, "That user doesn't exist"); }
		} catch(SQLException ex) { showMessage(stage, AlertType.ERROR, "Couldn't save the OTP, try again"); }	
	}
	public static boolean resetLogin(Stage stage, String username, String password) {
		Database db = FoundationsMain.database;
		try {
			if(!db.passwordResetNeeded(username)) { return false; }
			if(!db.useOTP(username, password)) { showMessage(stage, AlertType.ERROR, "Password reset ... Enter a valid OTP or check User ID"); return true; }
			
			PasswordField updatedPassword = new PasswordField();
			PasswordField confirmation = new PasswordField();
			Label error = new Label();
			error.setWrapText(true);
			error.setMinHeight(Region.USE_PREF_SIZE);
			Dialog<ButtonType> dialog = new Dialog<>();
			dialog.initOwner(stage);
			dialog.setHeaderText("OTP used. You will need a new OTP if you cancel");
			dialog.setTitle("Choose a new password");
			VBox fields = new VBox(9, new Label("New password:"), updatedPassword, new Label("Confirm password:"), confirmation, error);
			fields.setPrefWidth(350);
			dialog.getDialogPane().setContent(fields);
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
			dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event -> {
				String newPassword = updatedPassword.getText();
				String repeatedPassword = confirmation.getText();
				String issue = ValidateInputLength.checkInputLength(newPassword);
				if(issue.isEmpty()) { issue = ValidateInputLength.checkInputLength(repeatedPassword); }
				if(issue.isEmpty()) { issue = ValidatePasswordInput.evaluatePassword(newPassword); }
				if (issue.isEmpty() && !newPassword.equals(repeatedPassword)) { issue = "Passwords don't match"; event.consume(); }
				if(!issue.isEmpty()) { error.setText(issue); dialog.getDialogPane().getScene().getWindow().sizeToScene(); event.consume(); return; }
				try { if(!db.finalizePasswordReset(username, repeatedPassword)) { error.setText("Reset can't be completed, get a new OTP"); dialog.getDialogPane().getScene().getWindow().sizeToScene(); } }
				catch (SQLException ex) { error.setText("Couldn't save your password, try again"); dialog.getDialogPane().getScene().getWindow().sizeToScene(); }
				});
			ButtonType result = dialog.showAndWait().orElse(ButtonType.CANCEL);
			if(result==ButtonType.OK) { showMessage(stage, AlertType.INFORMATION, "The password has been changed, you can log in"); }
			guiUserLogin.ViewUserLogin.displayUserLogin(stage);
	} 
		catch(SQLException ex) { showMessage(stage, AlertType.ERROR, "Can't check the password reset"); }
		return true;
}
}