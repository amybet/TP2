package guiAdminHome;

import java.util.Optional;

import database.Database;
import entityClasses.InputRecognizer;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.List;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
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
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setHeaderText("Invite Error");
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Manages currently active invitations, the Admin can withdraw invite codes from certain users if they wish.
	 * Only Admins have access to this function.</p>
	 */
	protected static void manageInvitations () {
		System.out.println("*** manageInvitations() function called");

		theDatabase.clearInvalidInviteCodes();

		// Get number of invites from database
		int num = theDatabase.getNumberOfInvitations();
		

		

		if (num <= 0) {
			System.out.println("Failed to find InviteCodes");
			showInviteCodeAlert("No InviteCodes found", "There are no InviteCodes to delete.",
					AlertType.INFORMATION);
			ViewAdminHome.showInviteCodeAlert();
			ViewAdminHome.updateOutstandingInvitationsCount();
			return;
		}

		

		// Show dialog to select an invite to delete
		ViewAdminHome.manageInvitationsDialog.setHeaderText("Delete Invitations:");
		Optional<String> result = ViewAdminHome.showManageInviteCodesDialog();
		

		if (!result.isPresent()) {
			return;
		}
		
		String code = result.get();
		code = code.substring(
				code.indexOf('[') + 1,
				code.indexOf(']')
		);


		Optional<ButtonType> confirmation = ViewAdminHome.alertDeleteConfirm.showAndWait();

		// Delete user if confirmation is Yes

		if (confirmation.isPresent() && confirmation.get() == ButtonType.YES) {

			

		// Proceed with deletion
		theDatabase.removeInvitationAfterUse(code);
		System.out.println("Invitation successfully removed");
		}
		ViewAdminHome.updateOutstandingInvitationsCount();

	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Prompts the user for a user and password. 
	 * 	   Set's the selected users password to the new temp password given by the user.
	 * 
	 *  </p>
	 */
	protected static void setOnetimePassword () {
		Optional<String> tmp_passUser = ViewAdminHome.showChoiceDialog_UserOTP();
		String isValidUser = "temp";
		try {
			isValidUser = InputRecognizer.checkUsername(tmp_passUser.get());			
			
			
			String user = tmp_passUser.toString();
			String username = user.replace("Optional[", "").replace("]", "").trim();
			
			
			
			Optional<String> tmp_pass = ViewAdminHome.Dialog_OneTimePass.showAndWait();
			String isValidPass = "temp";
			isValidPass = InputRecognizer.checkPassword(tmp_pass.get());

			
			if(isValidPass.isEmpty()) {
				String otp = theDatabase.generateOTP(tmp_pass.get(), username);
				//ViewAdminHome.label_testArea.setText("Temporary password for: [" + theDatabase.getCurrentUsername() + "] is: " + otp);
				System.out.print("Temporary password for: [" + username + "] is: " + otp);
			} else{
				System.out.print("Username Error: " + isValidUser);
				System.out.print("Password Error: " + isValidPass);
			}
		} catch(Exception e) {
			
			System.out.println("value not given");
		}

	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
		System.out.println("*** deleteUser() function called");

		// Get list of users from database
		List<String> userList = theDatabase.getUserList();
		

		// Remove the "<Select a User>" placeholder
		if (userList != null && userList.size() > 1) {
			userList.remove(0);
		}

		if (userList == null || userList.isEmpty()) {
			showDeleteAlert("No users found", "There are no users to delete",
					AlertType.INFORMATION);
			return;
		}

		

		// Show dialog to select a user to delete
		ViewAdminHome.deleteChoiceDialog.setHeaderText("Select a user:");
		Optional<String> result = ViewAdminHome.showDeleteChoiceDialog();
		

		if (!result.isPresent()) {
			return;
		}

		String selectedUsername = result.get().strip();
		
		// Validation: admin cannot delete themselves
		if (selectedUsername.equals(ViewAdminHome.theUser.getUserName())) {
			showDeleteAlert("You cannot delete yourself.", "Choose another user that is not yourself.", AlertType.INFORMATION);
			return;
		}

		

		// Show confirmation dialog 
		ViewAdminHome.alertDeleteConfirm.setHeaderText("Are you sure you want to permanently delete " + 

				selectedUsername + "?");
		

		Optional<ButtonType> confirmation = ViewAdminHome.alertDeleteConfirm.showAndWait();


		// Delete user if confirmation is Yes

		if (confirmation.isPresent() && confirmation.get() == ButtonType.YES) {
		
			// Proceed with deletion
			if (theDatabase.deleteUserByUsername(selectedUsername)) {
				System.out.println("*** INFO ***: User " + selectedUsername + " successfully deleted");

				// Show success message
				showDeleteAlert("User Deleted Successfully", 
						"The user " + selectedUsername + " has been removed from the system.", 
						AlertType.INFORMATION);

				// Update the user count
				ViewAdminHome.label_NumberOfUsers.setText("Number of Users: " + 
						(theDatabase.getUserList().size() - 1));

			} else {

				System.out.println("*** ERROR ***: Failed to delete user " + selectedUsername);
				showDeleteAlert("Deletion Failed", 
					"An error occurred while deleting user: " + selectedUsername, 
					AlertType.ERROR);
			}
		} else {
			// User clicked "No" or cancelled
			System.out.println("*** INFO ***: User deletion cancelled");

		}

				
	}

	/**********
	 * <p> Method: showDeleteAlert() </p>
	 * 
	 * <p> Description: Populates the deleteAlert in ViewAdminHome with the given parameters </p>
	 * 
	 * @param title String value for Title
	 * @param contextText String value for ContextText
	 * @param error The AlertType
	 */
	private static void showDeleteAlert(String title, String contextText, AlertType error) {
	    ViewAdminHome.deleteAlert.setTitle(title);
	    ViewAdminHome.deleteAlert.setHeaderText(title);
	    ViewAdminHome.deleteAlert.setContentText(contextText);
		ViewAdminHome.showDeleteAlert();
	}
	
	private static void showInviteCodeAlert(String string, String string2, AlertType error) {
		// TODO Auto-generated method stub
		
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}	

	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that calls displayUserList. </p>
	 */
	protected static void listUsers() {
		
		ViewAdminHome.displayUserList();
		
		System.out.print(theDatabase.getAllUsers());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.showcombobox_SelectRoleToAdd();
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		String errorLine = InputRecognizer.checkEmailAddress(emailAddress);
		if(errorLine != "") {
			ViewAdminHome.alertEmailError.setContentText(errorLine);
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
