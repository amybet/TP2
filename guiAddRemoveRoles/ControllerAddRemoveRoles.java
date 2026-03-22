package guiAddRemoveRoles;

import database.Database;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import java.util.List;

/*******
 * <p> Title: ControllerAddRemoveRoles Class. </p>
 *
 * <p> Description: The Java/FX-based Add Remove Roles Page. This class provides the controller
 * actions based on the user's use of the JavaFX GUI widgets defined by the View class.
 *
 * This page has one of the more complex Controller Classes due to the fact that changing the
 * values of widgets changes the layout of the page. It is up to the Controller to determine what
 * to do and it involves the proper elements from the View Class for this GUI page.
 *
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods. This is why each has been declared as "protected". Do not change any
 * of these methods to public.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 * @author Amairani Caballero
 *
 * @version 1.00 2025-08-17 Initial version
 * @version 1.01 2025-09-16 Update Javadoc documentation
 * @version 1.02 2026-02-08 Add permission checks and admin constraints
 */
public class ControllerAddRemoveRoles {

	/*-*******************************************************************************************
	 *
	 * User Interface Actions for this page
	 *
	 * This controller is not a class that gets instantiated. Rather, it is a collection of protected
	 * static methods that can be called by the View (which is a singleton instantiated object) and
	 * the Model is often just a stub, or will be a singleton instantiated object.
	 *
	 */

	/** Default constructor is not used. */
	public ControllerAddRemoveRoles() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> Method: doSelectUser() </p>
	 *
	 * <p> Description: This method uses the ComboBox widget, fetches which item in the ComboBox
	 * was selected (a user in this case), and establishes that user and the current user, setting
	 * easily accessible values without needing to do a query. </p>
	 */
	protected static void doSelectUser() {
		ViewAddRemoveRoles.theSelectedUser = (String) ViewAddRemoveRoles.combobox_SelectUser.getValue();
		theDatabase.getUserAccountDetails(ViewAddRemoveRoles.theSelectedUser);
		setupSelectedUser();
	}

	/**********
	 * <p> Method: repaintTheWindow() </p>
	 *
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewAddRemoveRoles.theRootPane.getChildren().clear();

		// Determine which of the two views to show to the user
		if (ViewAddRemoveRoles.theSelectedUser.compareTo("<Select a User>") == 0) {
			// Only show the request to select a user to be updated and the ComboBox
			ViewAddRemoveRoles.theRootPane.getChildren().addAll(
					ViewAddRemoveRoles.label_PageTitle,
					ViewAddRemoveRoles.label_UserDetails,
					ViewAddRemoveRoles.button_UpdateThisUser,
					ViewAddRemoveRoles.line_Separator1,
					ViewAddRemoveRoles.label_SelectUser,
					ViewAddRemoveRoles.combobox_SelectUser);
		} else {
			// Show all the widgets because a user has been selected
			ViewAddRemoveRoles.theRootPane.getChildren().addAll(
					ViewAddRemoveRoles.label_PageTitle,
					ViewAddRemoveRoles.label_UserDetails,
					ViewAddRemoveRoles.button_UpdateThisUser,
					ViewAddRemoveRoles.line_Separator1,
					ViewAddRemoveRoles.label_SelectUser,
					ViewAddRemoveRoles.combobox_SelectUser,
					ViewAddRemoveRoles.label_CurrentRoles,
					ViewAddRemoveRoles.label_SelectRoleToBeAdded,
					ViewAddRemoveRoles.combobox_SelectRoleToAdd,
					ViewAddRemoveRoles.button_AddRole,
					ViewAddRemoveRoles.label_SelectRoleToBeRemoved,
					ViewAddRemoveRoles.combobox_SelectRoleToRemove,
					ViewAddRemoveRoles.button_RemoveRole);
		}

		// Add navigation buttons
		ViewAddRemoveRoles.theRootPane.getChildren().addAll(
				ViewAddRemoveRoles.line_Separator4,
				ViewAddRemoveRoles.button_Return,
				ViewAddRemoveRoles.button_Logout,
				ViewAddRemoveRoles.button_Quit);
	}

	/**********
	 * <p> Method: setupSelectedUser() </p>
	 *
	 * <p> Description: This method populates the lists of roles that can be added and removed
	 * for the selected user. It updates the ComboBox selections based on the user's current roles.</p>
	 */
	protected static void setupSelectedUser() {
		// The database was already updated with the selected user's details in doSelectUser()
		// Now fetch those values from the database

		ViewAddRemoveRoles.addList.clear();
		ViewAddRemoveRoles.addList.add("<Select a role>");

		ViewAddRemoveRoles.removeList.clear();
		ViewAddRemoveRoles.removeList.add("<Select a role>");

		// Build the list of roles to add based on current database state
		if (!theDatabase.getCurrentAdminRole()) {
			ViewAddRemoveRoles.addList.add("Admin");
		}
		if (!theDatabase.getCurrentNewRole1()) {
			ViewAddRemoveRoles.addList.add("Role1");
		}
		if (!theDatabase.getCurrentNewRole2()) {
			ViewAddRemoveRoles.addList.add("Role2");
		}

		// Build the list of roles to remove based on current database state
		if (theDatabase.getCurrentAdminRole()) {
			ViewAddRemoveRoles.removeList.add("Admin");
		}
		if (theDatabase.getCurrentNewRole1()) {
			ViewAddRemoveRoles.removeList.add("Role1");
		}
		if (theDatabase.getCurrentNewRole2()) {
			ViewAddRemoveRoles.removeList.add("Role2");
		}

		// Update the current roles label
		StringBuilder currentRoles = new StringBuilder("This user's current roles: ");
		int roleCount = 0;

		if (theDatabase.getCurrentAdminRole()) {
			currentRoles.append("Admin ");
			roleCount++;
		}
		if (theDatabase.getCurrentNewRole1()) {
			currentRoles.append("Role1 ");
			roleCount++;
		}
		if (theDatabase.getCurrentNewRole2()) {
			currentRoles.append("Role2");
			roleCount++;
		}
		if (roleCount == 0) {
			currentRoles.append("(None)");
		}

		ViewAddRemoveRoles.label_CurrentRoles.setText(currentRoles.toString());

		// Update the ComboBoxes
		ViewAddRemoveRoles.setupComboBoxUI(ViewAddRemoveRoles.combobox_SelectRoleToAdd, "Dialog", 16, 150, 280, 205);
		ViewAddRemoveRoles.combobox_SelectRoleToAdd.setItems(FXCollections.observableArrayList(ViewAddRemoveRoles.addList));
		ViewAddRemoveRoles.combobox_SelectRoleToAdd.getSelectionModel().clearAndSelect(0);

		ViewAddRemoveRoles.setupComboBoxUI(ViewAddRemoveRoles.combobox_SelectRoleToRemove, "Dialog", 16, 150, 280, 275);
		ViewAddRemoveRoles.combobox_SelectRoleToRemove.setItems(FXCollections.observableArrayList(ViewAddRemoveRoles.removeList));
		ViewAddRemoveRoles.combobox_SelectRoleToRemove.getSelectionModel().select(0);

		// Repaint the window showing these new values
		repaintTheWindow();
	}

	/**********
	 * <p> Method: performAddRole() </p>
	 *
	 * <p> Description: This method adds a new role to the selected user. It updates the database
	 * entry for that user with the new role. </p>
	 */
	protected static void performAddRole() {
		// Determine which item in the ComboBox list was selected
		ViewAddRemoveRoles.theAddRole = (String) ViewAddRemoveRoles.combobox_SelectRoleToAdd.getValue();

		// If the selection is the list header (e.g., "<Select a role>") don't do anything
		if (ViewAddRemoveRoles.theAddRole.compareTo("<Select a role>") != 0) {
			// If an actual role was selected, update the database entry for that user for the role
			if (theDatabase.updateUserRole(ViewAddRemoveRoles.theSelectedUser, ViewAddRemoveRoles.theAddRole, "true")) {
				ViewAddRemoveRoles.combobox_SelectRoleToAdd = new ComboBox<String>();
				setupSelectedUser();
			}
		}
	}

	/**********
	 * <p> Method: performRemoveRole() </p>
	 *
	 * <p> Description: This method removes an existing role from the selected user. It performs
	 * validation to ensure:
	 * <ul>
	 *   <li>An admin cannot remove their own admin role</li>
	 *   <li>At least one admin remains in the system</li>
	 *   <li>Users retain at least one role (if required by policy)</li>
	 * </ul>
	 * </p>
	 */
	protected static void performRemoveRole() {
		// Determine which item in the ComboBox list was selected
		ViewAddRemoveRoles.theRemoveRole =
				(String) ViewAddRemoveRoles.combobox_SelectRoleToRemove.getValue();

		// If the selection is the list header (e.g., "<Select a role>") don't do anything
		if (ViewAddRemoveRoles.theRemoveRole.compareTo("<Select a role>") != 0) {

			// Validation 1: Admin cannot remove their own admin role
			if (isAdminRemovingOwnAdminRole(ViewAddRemoveRoles.theSelectedUser, ViewAddRemoveRoles.theRemoveRole)) {
				showAdminSelfRemovalAlert();
				return;
			}

			// Validation 2: Ensure at least one admin remains in the system
			if (ViewAddRemoveRoles.theRemoveRole.equals("Admin")
					&& !isAnotherAdminExists(ViewAddRemoveRoles.theSelectedUser)) {
				showLastAdminAlert();
				return;
			}

			// Validation 3: Check if removing this role would leave user with no roles
			if (wouldRemoveAllRoles()) {
				showRemoveAllRolesAlert();
				return;
			}

			// All validations passed, proceed with removal
			if (theDatabase.updateUserRole(ViewAddRemoveRoles.theSelectedUser, ViewAddRemoveRoles.theRemoveRole, "false")) {
				ViewAddRemoveRoles.combobox_SelectRoleToRemove = new ComboBox<String>();
				setupSelectedUser();
			}
		}
	}

	/**********
	 * <p> Method: isAdminRemovingOwnAdminRole() </p>
	 *
	 * <p> Description: Checks whether the currently logged-in admin is attempting to remove
	 * their own Admin role. </p>
	 *
	 * @param selectedUsername The username of the user being modified
	 * @param roleToRemove The role being removed
	 * @return true if an admin is trying to remove their own admin role; false otherwise
	 */
	private static boolean isAdminRemovingOwnAdminRole(String selectedUsername, String roleToRemove) {
		// Check if the role being removed is "Admin"
		if (!roleToRemove.equals("Admin")) {
			return false;
		}

		// Check if the selected user is the current admin user
		if (selectedUsername.equals(ViewAddRemoveRoles.theUser.getUserName())) {
			// Check if this user has the admin role
			if (ViewAddRemoveRoles.theUser.getAdminRole()) {
				return true;
			}
		}

		return false;
	}

	/**********
	 * <p> Method: isAnotherAdminExists() </p>
	 *
	 * <p> Description: Checks whether there is at least one other admin in the system besides
	 * the user being modified. </p>
	 *
	 * @param selectedUsername The username of the user whose admin role is being removed
	 * @return true if another admin exists; false if this would be the last admin
	 */
	private static boolean isAnotherAdminExists(String selectedUsername) {
		List<String> allUsers = theDatabase.getUserList();

		for (String username : allUsers) {
			// Skip the user being modified
			if (username.equals(selectedUsername)) {
				continue;
			}

			// Get the user details and check if they have admin role
			theDatabase.getUserAccountDetails(username);

			if (theDatabase.getCurrentAdminRole()) {
				return true; // Found another admin
			}
		}

		return false; // No other admin exists
	}

	/**********
	 * <p> Method: wouldRemoveAllRoles() </p>
	 *
	 * <p> Description: Checks whether removing the selected role would leave the selected user
	 * with no roles at all. Based on system policy, users must retain at least one role. </p>
	 *
	 * @return true if removing this role would result in zero roles; false otherwise
	 */
	private static boolean wouldRemoveAllRoles() {
		// Count current roles from the database state
		int currentRoleCount = 0;

		if (theDatabase.getCurrentAdminRole()) {
			currentRoleCount++;
		}
		if (theDatabase.getCurrentNewRole1()) {
			currentRoleCount++;
		}
		if (theDatabase.getCurrentNewRole2()) {
			currentRoleCount++;
		}

		// If removing one role would leave 0 roles, return true
		return currentRoleCount == 1;
	}

	/**********
	 * <p> Method: showAdminSelfRemovalAlert() </p>
	 *
	 * <p> Description: Displays an alert when an admin tries to remove their own admin role. </p>
	 */
	private static void showAdminSelfRemovalAlert() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Cannot Remove Own Admin Role");
		alert.setHeaderText("Self-Removal Not Allowed");
		alert.setContentText("You cannot remove your own Admin role. Another admin must remove it for you.");
		alert.showAndWait();
	}

	/**********
	 * <p> Method: showLastAdminAlert() </p>
	 *
	 * <p> Description: Displays an alert when an attempt is made to remove the last admin
	 * from the system. </p>
	 */
	private static void showLastAdminAlert() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Cannot Remove Last Admin");
		alert.setHeaderText("System Integrity Check");
		alert.setContentText("The system must have at least one Admin user. You cannot remove the last Admin role from the system.");
		alert.showAndWait();
	}

	/**********
	 * <p> Method: showRemoveAllRolesAlert() </p>
	 *
	 * <p> Description: Displays an alert when an attempt is made to remove a user's last role. </p>
	 */
	private static void showRemoveAllRolesAlert() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Cannot Remove Last Role");
		alert.setHeaderText("Minimum Role Requirement");
		alert.setContentText("Each user must have at least one role. You cannot remove this user's last role.");
		alert.showAndWait();
	}

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the user and returns to the login page. </p>
	 */
	protected static void performLogout() {
		System.out.println("\n*** INFO ***: User logging out");
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAddRemoveRoles.theStage);
	}

	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns to the previous page (Admin Home). </p>
	 */
	protected static void performReturn() {
		System.out.println("\n*** INFO ***: Returning to previous page");
		// Return to Admin Home since only admins can access this page
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewAddRemoveRoles.theStage, ViewAddRemoveRoles.theUser);
	}

	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Quits the application. </p>
	 */
	protected static void performQuit() {
		System.out.println("\n*** INFO ***: Application ending");
		System.exit(0);
	}
}