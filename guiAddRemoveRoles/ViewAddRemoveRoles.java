package guiAddRemoveRoles;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 *
 * <p> Title: GUIAddRemoveRolesPage Class. </p>
 *
 * <p> Description: The Java/FX-based page for changing the assigned roles to users. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 * @author Amairani Caballero
 *
 * @version 1.00 2025-08-20 Initial version
 * @version 1.01 2026-02-08 Add debug output and fix scene display
 *
 */
public class ViewAddRemoveRoles {

	/*-*******************************************************************************************
	 *
	 * Attributes
	 *
	 */

	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: Page title, current user details, and account update button
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// Separator
	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	// Area 2a: select a user to update
	protected static Label label_SelectUser = new Label("Select a user to be updated:");
	protected static ComboBox<String> combobox_SelectUser = new ComboBox<String>();

	// Area 2b: add/remove roles for selected user
	protected static List<String> addList = new ArrayList<String>();
	protected static Button button_AddRole = new Button("Add This Role");

	protected static List<String> removeList = new ArrayList<String>();
	protected static Button button_RemoveRole = new Button("Remove This Role");

	protected static Label label_CurrentRoles = new Label("This user's current roles:");
	protected static Label label_SelectRoleToBeAdded = new Label("Select a role to be added:");
	protected static ComboBox<String> combobox_SelectRoleToAdd = new ComboBox<String>();

	protected static Label label_SelectRoleToBeRemoved = new Label("Select a role to be removed:");
	protected static ComboBox<String> combobox_SelectRoleToRemove = new ComboBox<String>();

	// Separator
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

	// GUI Area 3: navigation buttons
	protected static Button button_Return = new Button("Return");
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// Singleton and shared references
	private static ViewAddRemoveRoles theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	public static Scene theAddRemoveRolesScene = null;

	protected static String theSelectedUser = "";
	protected static String theAddRole = "";
	protected static String theRemoveRole = "";

	protected static Alert baseAlert;

	/*-*******************************************************************************************
	 *
	 * Constructors
	 *
	 */

	/**********
	 *
	 * <p> Method: displayAddRemoveRoles(Stage ps, User user) </p>
	 *
	 * <p> Description: Single entry point to display the Add/Remove Roles page. It sets shared
	 * references (stage/user), creates the singleton view if needed, populates dynamic UI state,
	 * then sets and shows the scene. </p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 * @param user specifies the User who is currently logged in (admin managing roles)
	 */
	public static void displayAddRemoveRoles(Stage ps, User user) {

		System.out.println("\n*** DEBUG ***: displayAddRemoveRoles() ENTERED");
		System.out.println("*** DEBUG ***: ps = " + ps);
		System.out.println("*** DEBUG ***: user = " + (user != null ? user.getUserName() : "null"));

		// Establish references to the GUI and the current user
		theStage = ps;
		theUser = user;

		System.out.println("*** DEBUG ***: theView before check = " + theView);

		// Create the singleton instance of this class if needed
		if (theView == null) {
			System.out.println("*** DEBUG ***: Creating new ViewAddRemoveRoles instance");
			theView = new ViewAddRemoveRoles();
		}

		System.out.println("*** DEBUG ***: About to select first user in combobox");

		// Default to no user selected
		combobox_SelectUser.getSelectionModel().select(0);

		System.out.println("*** DEBUG ***: Calling ControllerAddRemoveRoles.repaintTheWindow()");

		// Populate the dynamic aspects of the GUI (two-mode page)
		ControllerAddRemoveRoles.repaintTheWindow();

		System.out.println("*** DEBUG ***: Calling ControllerAddRemoveRoles.doSelectUser()");
		ControllerAddRemoveRoles.doSelectUser();

		// CRITICAL: Set the scene and show the stage
		System.out.println("*** DEBUG ***: Setting scene on stage");

		theStage.setTitle("CSE 360 Foundation Code: Add/Remove Roles");
		theStage.setScene(theAddRemoveRolesScene);
		theStage.show();

		System.out.println("*** DEBUG ***: displayAddRemoveRoles() COMPLETED SUCCESSFULLY");
	}

	/**********
	 *
	 * <p> Method: ViewAddRemoveRoles() </p>
	 *
	 * <p> Description: Initializes the static aspects of the GUI widgets (layout, styling, and
	 * event handlers). This constructor is called only once because this class is a singleton. </p>
	 */
	public ViewAddRemoveRoles() {

		System.out.println("*** DEBUG ***: ViewAddRemoveRoles CONSTRUCTOR CALLED");

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();

		theAddRemoveRolesScene = new Scene(theRootPane, width, height);
		theAddRemoveRolesScene.getStylesheets()
				.add(getClass().getResource("/applicationMain/application.css").toExternalForm());

		baseAlert = new Alert(AlertType.ERROR);
		DialogPane baseAlertPane = baseAlert.getDialogPane();
		baseAlertPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		baseAlertPane.getStyleClass().add("alertStyle-error");

		System.out.println("*** DEBUG ***: Scene created with width=" + width + ", height=" + height);

		// GUI Area 1
		label_PageTitle.setText("Add/Remove Roles Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		// GUI Area 2a
		setupLabelUI(label_SelectUser, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
		setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 280, 125);

		List<String> userList = theDatabase.getUserList();
		System.out.println("*** DEBUG ***: User list from database: " + userList);

		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUser.getSelectionModel().select(0);

		combobox_SelectUser.getSelectionModel().selectedItemProperty().addListener(
				(@SuppressWarnings("unused") ObservableValue<? extends String> observable,
				 @SuppressWarnings("unused") String oldvalue,
				 @SuppressWarnings("unused") String newValue) -> {
					System.out.println("*** DEBUG ***: User selected from combobox");
					ControllerAddRemoveRoles.doSelectUser();
				});

		// GUI Area 2b
		setupLabelUI(label_CurrentRoles, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 170);
		setupLabelUI(label_SelectRoleToBeAdded, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 210);

		setupComboBoxUI(combobox_SelectRoleToAdd, "Dialog", 16, 150, 280, 205);
		setupButtonUI(button_AddRole, "Dialog", 16, 150, Pos.CENTER, 460, 205);

		ViewAddRemoveRoles.button_AddRole.setOnAction((_) -> {
			System.out.println("*** DEBUG ***: Add Role button clicked");
			ControllerAddRemoveRoles.performAddRole();
		});

		setupButtonUI(button_RemoveRole, "Dialog", 16, 150, Pos.CENTER, 460, 275);

		ViewAddRemoveRoles.button_RemoveRole.setOnAction((_) -> {
			System.out.println("*** DEBUG ***: Remove Role button clicked");
			ControllerAddRemoveRoles.performRemoveRole();
		});

		setupLabelUI(label_SelectRoleToBeRemoved, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 280);
		setupComboBoxUI(combobox_SelectRoleToRemove, "Dialog", 16, 150, 280, 275);

		// GUI Area 3
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {
			System.out.println("*** DEBUG ***: Return button clicked");
			ControllerAddRemoveRoles.performReturn();
		});

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {
			System.out.println("*** DEBUG ***: Logout button clicked");
			ControllerAddRemoveRoles.performLogout();
		});

		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {
			System.out.println("*** DEBUG ***: Quit button clicked");
			ControllerAddRemoveRoles.performQuit();
		});

		System.out.println("*** DEBUG ***: ViewAddRemoveRoles CONSTRUCTOR COMPLETED");

		// Setting widgets into the Root Pane is delegated to controller methods due to dynamic layout
	}

	/*-*******************************************************************************************
	 *
	 * Helper methods used to minimize the number of lines of code needed above
	 *
	 */

	/**********
	 *
	 * Initializes standard UI fields for a label.
	 *
	 * @param l  the Label object to be initialized
	 * @param ff the font family to be used
	 * @param f  the font size
	 * @param w  the minimum width of the label
	 * @param p  the alignment (e.g. left, centered, or right)
	 * @param x  the x position (from left edge)
	 * @param y  the y position (from top edge)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 *
	 * Initializes standard UI fields for a button.
	 *
	 * @param b  the Button object to be initialized
	 * @param ff the font family to be used
	 * @param f  the font size
	 * @param w  the minimum width of the button
	 * @param p  the alignment (e.g. left, centered, or right)
	 * @param x  the x position (from left edge)
	 * @param y  the y position (from top edge)
	 */
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/**********
	 *
	 * Initializes standard UI fields for a ComboBox.
	 *
	 * @param c  the ComboBox object to be initialized
	 * @param ff the font family to be used
	 * @param f  the font size
	 * @param w  the minimum width of the ComboBox
	 * @param x  the x position (from left edge)
	 * @param y  the y position (from top edge)
	 */
	protected static void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y) {
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}

	public static void showcombobox_SelectRoleToAdd() {
		System.out.println("\nshowComboBox_sselkjsl\n");
		combobox_SelectRoleToAdd.getItems().clear();

		for (String user : theDatabase.getAllUsers()) {
			String sub = user.substring(user.indexOf(" "), user.indexOf("("));
			combobox_SelectRoleToAdd.getItems().add(sub);
		}

		combobox_SelectRoleToAdd.show();
	}
}