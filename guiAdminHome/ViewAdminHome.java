package guiAdminHome;

import java.sql.SQLException;
import java.sql.Timestamp; 
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import database.Database;
import entityClasses.DiscussionInputRecognizer;
import entityClasses.InvitationCode;
import entityClasses.User;
import entityClasses.post;
import entityClasses.reply;
import guiUserUpdate.ViewUserUpdate;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the JavaFX GUI widgets
 * that enable an admin to perform admin functions.  This page contains a number of buttons that
 * have not yet been implemented.  What has been implemented may not work the way the final product
 * requires and there maybe defects in this code.
 * 
 * The class has been written using a singleton design pattern and is the View portion of the 
 * Model, View, Controller pattern.  The pattern is designed that the all accesses to this page and
 * its functions starts by invoking the static method displayAdminHome.  No other method should 
 * attempt to instantiate this class as that is controlled by displayAdminHome.  It ensure that
 * only one instance of class is instantiated and that one is properly configured for each use.  
 * 
 * Please note that this implementation is not appropriate for concurrent systems with multiple
 * users. This Baeldung article provides insight into the issues: 
 *           https://www.baeldung.com/java-singleton</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ViewAdminHome {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 5 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	public static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: This area is used to provide status of the system.  This basic foundational code
	// does not have much current status information to display.
	public static Label label_NumberOfInvitations = 
			new Label("Number of Oustanding Invitations: x");
	public static Label label_NumberOfUsers = new Label("Number of Users: x");
	
	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator2 = new Line(20, 165, width-20, 165);
	
	// GUI Area 3: This is the first of two areas provided the admin with a set of action buttons
	// that can be used to perform the tasks allocated to the admin role.  This part is about
	// inviting potential new users to establish an account and what role that user will have.
	protected static Label label_Invitations = new Label("Send An Invitation");
	protected static Label label_InvitationEmailAddress = new Label("Email Address");
	protected static TextField text_InvitationEmailAddress = new TextField();
	protected static ComboBox <String> combobox_SelectRole = new ComboBox <String>();
	protected static String [] roles = {"Admin", "Role1", "Role2"};
	protected static Button button_SendInvitation = new Button("Send Invitation");
	protected static Alert alertEmailError = new Alert(AlertType.INFORMATION);
	protected static Alert alertEmailSent = new Alert(AlertType.INFORMATION);
	
	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator3 = new Line(20, 255, width-20, 255);
	
	// GUI Area 4: This is the second of the two action item areas.  This provides a set of other
	// admin buttons to use to perform other roles.  Many of these buttons are just stubs and an
	// alert pops up to inform the admin of this fact.
	protected static Button button_ManageInvitations = new Button("Manage Invitations");
	protected static ChoiceDialog<String> manageInvitationsDialog;
	protected static Button button_SetOnetimePassword = new Button("Set a One-Time Password");
	protected static Button button_DeleteUser = new Button("Delete a User");
	protected static ChoiceDialog<String> deleteChoiceDialog;
	protected static Alert alertDeleteConfirm = new Alert(AlertType.CONFIRMATION);
	protected static Button button_ListUsers = new Button("List All Users");
	protected static Button button_AddRemoveRoles = new Button("Add/Remove Roles");
	

	// This is a separator and it is used to partition the GUI for various tasks
	private static Line line_Separator4 = new Line(20, 525, width-20,525);

	// GUI Area 5: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the 
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	
	public static TextInputDialog Dialog_OneTimePass = new TextInputDialog("");
	public static ChoiceDialog<String> ChoiceDialog_UserOTP;
	
	//GUI AREA 6: Post section
	VBox postArea = new VBox(10);
	TextField text_searchBar = new TextField();
	Button button_CreatePost = new Button("Create New Post");
	// Scrollable Feed
	VBox postFeed = new VBox(10);
	ScrollPane scrollFeed = new ScrollPane(postFeed);
	
	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewAdminHome theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	public static Stage theStage;			// The Stage that JavaFX has established for us
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current logged in User

	private static Scene theAdminHomeScene;		// The shared Scene each invocation populates
	private static final int theRole = 1;		// Admin: 1; Role1: 2; Role2: 3

	protected static Scene userListScene;
	protected static Stage userListStage;
	protected static Button button_BackButton = null;
	protected static VBox userListPane = null;
	
	protected static Alert deleteAlert;
	protected static Alert inviteCodeAlert;
	protected static Alert deleteInviteConfirm;
	
	protected static ComboBox<String> threadChoice;

	
	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayAdminHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Admin Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayAdminHome(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewAdminHome();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());		// Fetch this user's data
		applicationMain.FoundationsMain.activeHomePage = theRole;	// Set this as the active Home																	// UserUpdate page

		// Set the role for potential users to the default (No role selected)
		combobox_SelectRole.getSelectionModel().select(0);
				
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundation Code: Admin Home Page");
		theStage.setScene(theAdminHomeScene);						// Set this page onto the stage
		theStage.show();											// Display it to the user
	}
	
	/**********
	 * <p> Method: GUIAdminHomePage() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayAdminHome method.</p>
	 * 
	 */
	private ViewAdminHome() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theAdminHomeScene = new Scene(theRootPane, width, height);
		theAdminHomeScene.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		
		userListPane = new VBox(16);
		userListPane.setStyle("-fx-padding: 15px");
		userListScene = new Scene(userListPane);
		userListScene.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		
		userListStage = new Stage();
		userListStage.initModality(Modality.APPLICATION_MODAL);
		userListStage.setTitle("User List");
		
		DialogPane alertEmailErrorPane = alertEmailError.getDialogPane();
		alertEmailErrorPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		alertEmailErrorPane.getStyleClass().add("alertStyle-error");
		
		DialogPane alertEmailSentPane = alertEmailSent.getDialogPane();
		alertEmailSentPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		alertEmailSentPane.getStyleClass().add("alertStyle");
		

		// choose user for otp
		ChoiceDialog_UserOTP = new ChoiceDialog<>();
		ChoiceDialog_UserOTP.setTitle("Select User");
		ChoiceDialog_UserOTP.setHeaderText("Select user for OTP");
		
		DialogPane oneTimePassUserPane = ChoiceDialog_UserOTP.getDialogPane();
		oneTimePassUserPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		oneTimePassUserPane.getStyleClass().add("alertStyle");

		// set otp
		
		DialogPane oneTimePassPane = Dialog_OneTimePass.getDialogPane();
		oneTimePassPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		oneTimePassPane.getStyleClass().add("alertStyle");
		
		
		Dialog_OneTimePass.setTitle("One Time Password");
		Dialog_OneTimePass.setHeaderText("Set New OTP");

		//back button setup
		button_BackButton = new Button("Close");
		button_BackButton.setOnAction((_)-> {
			userListStage.close();
		});
		button_BackButton.setFont(Font.font("Dialog", 16));
		button_BackButton.setMinWidth(100);
		userListStage.setScene(userListScene);
		userListStage.sizeToScene();
		userListStage.setWidth(350);
		
		deleteChoiceDialog = new ChoiceDialog<>();
		DialogPane deleteChoiceDilalogPane = deleteChoiceDialog.getDialogPane();
		deleteChoiceDilalogPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		deleteChoiceDilalogPane.getStyleClass().add("alertStyle");
		deleteChoiceDialog.setTitle("Delete User");
		
		manageInvitationsDialog = new ChoiceDialog<>();
		DialogPane invitationsChoiceDialogPane = manageInvitationsDialog.getDialogPane();
		invitationsChoiceDialogPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		invitationsChoiceDialogPane.getStyleClass().add("alertStyle");
		manageInvitationsDialog.setTitle("Manage Invitations");
		
	    deleteAlert = new Alert(AlertType.WARNING);
	    DialogPane alertDialogPane = deleteAlert.getDialogPane();
	    alertDialogPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    alertDialogPane.getStyleClass().add("alertStyle-error");
	    deleteAlert.setTitle("Delete Error.");
	    deleteAlert.setHeaderText("Delete Header");
	    deleteAlert.setContentText("Delete Context");
	    

	    DialogPane alertDeleteConfirmPane = alertDeleteConfirm.getDialogPane();
	    alertDeleteConfirmPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    alertDeleteConfirmPane.getStyleClass().add("alertStyle");
	    alertDeleteConfirm.setTitle("Confirm user deletion");
	    alertDeleteConfirm.setContentText("This action cannot be undone.");
		alertDeleteConfirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

	    
     	threadChoice = new ComboBox<>();
		threadChoice.setLayoutX(285);
		threadChoice.setLayoutY(275);
		threadChoice.getSelectionModel().select(0);
		List<String> tlist = new ArrayList<String>();
		tlist.add("General");
		tlist.add("My Posts");
		tlist.add("Quizzes");
		tlist.add("Homework Help");
		threadChoice.setItems(FXCollections.observableArrayList(tlist));
		threadChoice.getSelectionModel().select(0);
		
		// Updates display to show posts from the chosen thread (the default is general)
		threadChoice.setOnAction((_) -> {
			String selectedItem = threadChoice.getValue();
			if (selectedItem != null && selectedItem.equals("General")) {
	     		refreshPostFeed(postFeed, "", ""); 

				
			} else if (selectedItem != null && selectedItem.equals("Quizzes")) {
				
	     		refreshPostFeed(postFeed, "", "Quizzes"); 

			} else if (selectedItem != null && selectedItem.equals("Homework Help")) {
	     		refreshPostFeed(postFeed, "", "Homework Help"); 

				
			} else if (selectedItem != null && selectedItem.equals("My Posts")) {
	     		refreshPostFeed(postFeed, "", ""); 

				
			}
		});
		
	    
	    inviteCodeAlert = new Alert(AlertType.WARNING);
	    DialogPane inviteCodeDialogPane = inviteCodeAlert.getDialogPane();
	    inviteCodeDialogPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    inviteCodeDialogPane.getStyleClass().add("alertStyle-error");
	    inviteCodeAlert.setTitle("Cannot Remove InviteCodes");
	    inviteCodeAlert.setHeaderText("Error: No invite codes found.");
	    inviteCodeAlert.setContentText("All invite codes have expired.");
	
	
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Admin Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> 
				{ViewUserUpdate.displayUserUpdate(theStage, theUser);});
			
		// GUI Area 2
		setupLabelUI(label_NumberOfInvitations, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 105);
		label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	
		setupLabelUI(label_NumberOfUsers, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 135);
		label_NumberOfUsers.setText("Number of users: " + 
				theDatabase.getNumberOfUsers());
	
		// GUI Area 3
		setupLabelUI(label_Invitations, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 175);
	
		setupLabelUI(label_InvitationEmailAddress, "Arial", 16, width, Pos.BASELINE_LEFT,
		20, 210);
	
		setupTextUI(text_InvitationEmailAddress, "Arial", 16, 360, Pos.BASELINE_LEFT,
		130, 205, true);
	
		setupComboBoxUI(combobox_SelectRole, "Dialog", 16, 90, 500, 205);
	
		List<String> list = new ArrayList<String>();	// Create a new list empty list of the
		for (int i = 0; i < roles.length; i++) {		// roles this code currently supports
			list.add(roles[i]);
		}
		combobox_SelectRole.setItems(FXCollections.observableArrayList(list));
		combobox_SelectRole.getSelectionModel().select(0);
		alertEmailSent.setTitle("Invitation");
		alertEmailSent.setHeaderText("Invitation was sent");

		setupButtonUI(button_SendInvitation, "Dialog", 16, 150, Pos.CENTER, 630, 205);
		button_SendInvitation.setOnAction((_) -> {ControllerAdminHome.performInvitation(); });
	
		// GUI Area 4
		setupButtonUI(button_ManageInvitations, "Dialog", 16, 250, Pos.CENTER, 20, 270);
		button_ManageInvitations.setOnAction((_) -> 
			{ControllerAdminHome.manageInvitations(); });
	
		setupButtonUI(button_SetOnetimePassword, "Dialog", 16, 250, Pos.CENTER, 20, 320);
		button_SetOnetimePassword.setOnAction((_) -> 
			{ControllerAdminHome.setOnetimePassword(); });

		setupButtonUI(button_DeleteUser, "Dialog", 16, 250, Pos.CENTER, 20, 370);
		button_DeleteUser.setOnAction((_) -> {ControllerAdminHome.deleteUser(); });

		setupButtonUI(button_ListUsers, "Dialog", 16, 250, Pos.CENTER, 20, 420);
		button_ListUsers.setOnAction((_) -> {ControllerAdminHome.listUsers(); });

		setupButtonUI(button_AddRemoveRoles, "Dialog", 16, 250, Pos.CENTER, 20, 470);
		button_AddRemoveRoles.setOnAction((_) -> {ControllerAdminHome.addRemoveRoles(); });
		
		// GUI Area 5
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> {ControllerAdminHome.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> {ControllerAdminHome.performQuit(); });
		
		// GUI Area 6
		setupTextUI(text_searchBar, "Arial", 16, 300, Pos.BASELINE_LEFT, 450, 270, true);
		text_searchBar.setPromptText("Search posts by title or content");

		setupButtonUI(button_CreatePost, "Dialog", 18, 200, Pos.CENTER, width - 20 - 200, 540);
		button_CreatePost.setOnAction((_) -> showCreatePostDialog());
		button_CreatePost.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());

		scrollFeed.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		scrollFeed.setLayoutX(280);
		scrollFeed.setLayoutY(315);
		scrollFeed.setPrefSize(475, 200);
		scrollFeed.setFitToWidth(true);

		refreshPostFeed(postFeed, "", ""); 

		// listener for search so it can not input over 500
		text_searchBar.textProperty().addListener((obs, oldVal, newVal) -> {
			String query = newVal;
		    if (newVal.length() > 500) {
		        query = newVal.substring(0, 500);
		        text_searchBar.setText(query);
		    }
		    refreshPostFeed(postFeed, newVal, ""); 
		});
		
		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
    		label_NumberOfInvitations, label_NumberOfUsers,
    		line_Separator2,
    		label_Invitations, 
    		label_InvitationEmailAddress, text_InvitationEmailAddress,
    		combobox_SelectRole, button_SendInvitation, line_Separator3,
    		button_ManageInvitations,
    		button_SetOnetimePassword,
    		button_DeleteUser,
    		button_ListUsers,
    		button_AddRemoveRoles,
    		threadChoice,
    		line_Separator4, 
    		text_searchBar,
    		scrollFeed,
    		button_CreatePost,
    		button_Logout,
    		button_Quit
    		);
		
		// With theRootPane set up with the common widgets, it is up to displayAdminHome to show
		// that Pane to the user after the dynamic elements of the widgets have been updated.
	}

	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
	*/

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
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
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	
	/**********
	 * Private local method to initialize the standard fields for a text input field
	 * 
	 * @param t		The TextField object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 * @param e		Is this TextField user editable?
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	

	
	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w, double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
	
	
	/**********
	 * <p> Method: displayUserList() </p>
	 * 
	 * <p> Description: Clears the contents and then re-populates the userListPane in ViewAdminHome. When
	 * finished it shows the userListStage </p>
	 * 
	 */	
	protected static void displayUserList() {	
		//center the list
		userListPane.getChildren().clear();
		for(String user : theDatabase.getAllUsers()) {
			Label label_User = new Label(user);    
			label_User.setFont(Font.font("Dialog", 16));
			userListPane.getChildren().add(label_User);
		}
		userListPane.getChildren().add(button_BackButton);

		userListStage.show();
	}

	/**********
	 * <p> Method: showDeleteAlert() </p>
	 * 
	 * <p> Description: Helper method to display alerts during delete operations.
	 * Centralizes alert display for consistency.</p>
	 * 
	 * 
	 *
	 * 
	 */
	public static void showDeleteAlert() {
	    deleteAlert.showAndWait();
	    System.out.println("deleteallertShowandwait called");
	}
	
	/**********
	 * <p> Method: showInviteCodeAlert() </p>
	 * 
	 * <p> Description: Helper method to display alerts during ManageInvite operations.</p>
	 */
	public static void showInviteCodeAlert() {
		inviteCodeAlert.showAndWait();
	}


	/**********
	 * <p> Method: showDeleteChoiceDialog() </p>
	 * 
	 * <p> Description: Clears the ChoiceDialog contents of deleteChoiceDialog and adds
	 * them back to account for any changes in database. </p>
	 * 
	 * @return String with formatted dialog box output.
	 */
	public static Optional<String>  showChoiceDialog_UserOTP() {
		ChoiceDialog_UserOTP.getItems().clear();
		
		for(String user : theDatabase.getAllUsers()) {   
			String sub = user.substring(user.indexOf(" "), user.indexOf("("));
			ChoiceDialog_UserOTP.getItems().add(sub);
		}

		Optional<String> result = ChoiceDialog_UserOTP.showAndWait();
		return result;
	}

	/**********
	 * <p> Method: showManageInviteCodesDialog() </p>
	 * 
	 * <p> Description: Creates the strings that are displayed in the drop down menu for ManageInvites.</p>
	 * 
	 * @return String with formatted dialog box output.
	 */
	public static Optional<String>  showDeleteChoiceDialog() {

		deleteChoiceDialog.getItems().clear();
		
		for(String user : theDatabase.getAllUsers()) { 
			String sub = user.substring(user.indexOf(" "), user.indexOf("("));
			deleteChoiceDialog.getItems().add(sub);
		}
		

		Optional<String> result = deleteChoiceDialog.showAndWait();
		return result;
	}
	
	/**********
	 * <p> Method: showManageInviteCodesDialog() </p>
	 * 
	 * <p> Description: Creates the strings that are displayed in the drop down menu for ManageInvites.</p>
	 * 
	 * @return String with formatted dialog box output.
	 */
	public static Optional<String>  showManageInviteCodesDialog() {

		manageInvitationsDialog.getItems().clear();
		Map<String, InvitationCode> inviteCodes = theDatabase.getAllInvitationCodes();
		
		DateTimeFormatter formatter =
		        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
		                         .withZone(ZoneId.systemDefault());
		
		for(String code : inviteCodes.keySet()) { 
			Timestamp expiresAt = inviteCodes.get(code).getExpiresAt();
			String email = inviteCodes.get(code).getEmail();
			String formattedTime = formatter.format(expiresAt.toInstant());
			
			manageInvitationsDialog.getItems().add("InviteCode: [" + code + "]\nExpires: " + formattedTime + "\nEmailed to: " + email);
		}
		

		Optional<String> result = manageInvitationsDialog.showAndWait();
		return result;
	}
	
	/**********
	 * <p> Method: updateOutstandingInvitationsCount() </p>
	 * 
	 * <p> Description: Helper method to update the "Outstanding Invites" count at the top of the main page..</p>
	 */
	public static void updateOutstandingInvitationsCount() {
		label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
		theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> Method: void showValidationError(String message)</p>
	 * 
	 * <p> Description: This method displays an error message to the user when their post input is not valid.
	 *		This happens when the input is null, title is > 32 characters or content is > 500 characters
	 * </p>
	 */
	private void showValidationError(String message) {
	    Alert alert = new Alert(AlertType.ERROR);
	    alert.setTitle("Validation Error");
	    alert.setHeaderText("Invalid Input");
	    alert.setContentText(message);
	    
	    // Apply your application.css for consistency
	    DialogPane pane = alert.getDialogPane();
	    pane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    pane.getStyleClass().add("alertStyle-error"); // Use your error style
	    
	    alert.showAndWait();
	}
	
	/**********
	 * <p> Method: void refreshPostFeed(VBox container, String keyword) </p>
	 * 
	 * <p> Description: Refreshes the post feed of the user. This is called when something is done to the post list like deleting or adding posts.</p>
	 */
	private void refreshPostFeed(VBox container, String keyword, String thread) {
	    container.getChildren().clear();
	    List<post> results;
	    if (threadChoice.getValue() == "My Posts") {
	    	results = theDatabase.searchPost(theUser.getUserName());
	    } else {
	    	
	    	results = theDatabase.searchPost(keyword, thread);
	    }
	    
	    for (post p : results) {
	        String displayHeader = p.getDeleted() ? "(this post has been deleted)" : p.getTitle();
	        Button button_post = new Button(displayHeader + "\nBy: " + p.getAuthor());
	        button_post.setMaxWidth(Double.MAX_VALUE);
	        button_post.getStyleClass().add("button");
	        
	        button_post.setOnAction((_) -> showFullPost(p));
	        
	        container.getChildren().add(button_post);
	    }
	}
	
	
	/**********
	 * <p> Method: void showCreatePostDialog() </p>
	 * 
	 * <p> Description: Prompts the user for a title and contents. If the input is valid, creates the new post and refreshes the feed</p>
	 */
	private void showCreatePostDialog() {
	    Stage dialog = new Stage();
	    dialog.initModality(Modality.APPLICATION_MODAL);
	    VBox layout = new VBox(10);
	    layout.setStyle("-fx-padding: 20");
	    layout.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());

	    TextField postTitle = new TextField();
	    postTitle.setPromptText("Title (Max 32)");
	    
	    postTitle.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    Label titleCount = new Label("0/32");
	    titleCount.setStyle("-fx-text-fill: gray;");
	    
	    TextField postContent = new TextField();
	    postContent.setPromptText("Write your post (Max 500):");
	    Label contentCount = new Label("0/500");
	    contentCount.setStyle("-fx-text-fill: gray;");
	    postContent.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    
	    ComboBox<String> pickThread = new ComboBox<>();
		pickThread.setLayoutX(width/2 - 50);
		pickThread.setLayoutY(55);
		List<String> list = new ArrayList<String>();	
		list.add("My Posts");
		list.add("General");
		pickThread.setItems(FXCollections.observableArrayList(list));
		pickThread.getSelectionModel().select(0);
	    
	    // keeps the input of content <= 32
	    postTitle.textProperty().addListener((_, _, newValue) -> {
	        if (newValue.length() > 32) {postTitle.setText(newValue.substring(0, 32));}
	        titleCount.setText(postTitle.getText().length() + "/32");
	    });

	    // keeps the input of content <= 500
	    postContent.textProperty().addListener((_, _, newValue) -> {
	        if (newValue.length() > 500) {postContent.setText(newValue.substring(0, 500));}
	        contentCount.setText(postContent.getText().length() + "/500");
	    });
	    
	    Button submit = new Button("Post");
	    submit.setOnAction((_) -> {
	        String title = postTitle.getText().trim();
	        String content = postContent.getText().trim();

	        //input validation for new post
	        if (title.isEmpty()) {
	        	showValidationError("Title cannot be empty.");
	        } else if (title.length() > 32) {
	        	showValidationError("Title is too long! (Max 32 characters)");
	        } else if (content.isEmpty()) {
	        	showValidationError("Post content cannot be empty.");
	        } else if (content.length() > 500) {
	        	showValidationError("Post is too long! (Max 500 characters)");
	        } else {
	        	theDatabase.createPost(theUser.getUserName(), postTitle.getText(), pickThread.getValue(), postContent.getText());
	        	refreshPostFeed(postFeed, "", "");
	        	dialog.close();
	        }
	    });
	    submit.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());

	    layout.getChildren().addAll(new Label("New Discussion"), postTitle, titleCount, postContent, contentCount, submit);
	    dialog.setScene(new Scene(layout));
	    dialog.show();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: void showFullPost(post p)</p>
	 * 
	 * <p> Description: This method displays a new panel for a user to read a post and all of its replys.
	 * This is where the user can reply to posts and delete posts. </p>
	 * 
	 * @param p: the post that the panel is showing
	 */
	protected void showFullPost(post p) {
		Stage stage = new Stage();
	    stage.initModality(Modality.APPLICATION_MODAL);
	    stage.setTitle("Post");

	    VBox layout = new VBox(15);
	    layout.setStyle("-fx-padding: 20;");

	    // change the name of the post if deleted (should be redundant but is here in case)
	    String contentText = p.getDeleted() ? "(this post has been deleted)" : p.getContent();
	    Label postContent = new Label(contentText);
	    postContent.setWrapText(true);
	    postContent.setPrefWidth(350); 
	    postContent.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
	    
	    // limit the time to show until minutes
	    String Time = p.getTimestamp();
	    String trimmedTime = Time.length() > 16 ? Time.substring(0, 16) : Time;
	    Label postTime = new Label("Posted on: " + trimmedTime);
	    postTime.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
	    
	    // Action Buttons
	    HBox buttons = new HBox(10);
	    
	    // Delete button only shows for the author and admins
	    if (theUser.getUserName().equals(p.getAuthor()) || theUser.getAdminRole()) {
	        Button button_Delete = new Button("Delete");
	        button_Delete.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	        button_Delete.setOnAction((_) -> {
	            // "Are you sure?" Alert 
	        	DialogPane alertDeleteConfirmPane = alertDeleteConfirm.getDialogPane();
	    	    alertDeleteConfirmPane.getStyleClass().add("alertStyle");
	    	    alertDeleteConfirm.setTitle("Are you sure?");
	    	    alertDeleteConfirm.setContentText("This action cannot be undone.");
	    		alertDeleteConfirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
	    		Optional<ButtonType> result = alertDeleteConfirm.showAndWait();
	    		
	            if (!result.isEmpty() && result.get() == ButtonType.YES) {
	                theDatabase.deletePost(p.getPostID());
	                //set the objects value to deleted as well 
	                p.setDeleted(true);
	                stage.close();
	                refreshPostFeed(postFeed, "", "");
	            }
	        });
	        buttons.getChildren().add(button_Delete);
	    }
	       if(theUser.getUserName().equals(p.getAuthor()) || theUser.getAdminRole()) {
	        Button button_update = new Button("Edit");

	        button_update.setOnAction(e -> {

	            // update title
	            TextInputDialog titleDialog = new TextInputDialog(p.getTitle());
	            titleDialog.setTitle("Edit your post");
	            titleDialog.setHeaderText("Edit Title:");
	            titleDialog.setContentText("Title:");

	            DialogPane titlePane = titleDialog.getDialogPane();
	            titlePane.getStylesheets().add(
	                    getClass().getResource("/applicationMain/application.css").toExternalForm());
	            titlePane.getStyleClass().add("alertStyle");

	            Optional<String> titleResult = titleDialog.showAndWait();

	            if (titleResult.isEmpty()) {
	                return; // user cancelled
	            }

	            String newTitle = titleResult.get().trim();
	            String titleError = DiscussionInputRecognizer.checkPostTitle(newTitle);

	            if (!titleError.isEmpty()) {
	                showValidationError(titleError);
	                return;
	            }

	            // update content
	            TextInputDialog contentDialog = new TextInputDialog(p.getContent());
	            contentDialog.setTitle("Edit your post");
	            contentDialog.setHeaderText("Edit Content:");
	            contentDialog.setContentText("Content:");

	            DialogPane contentPane = contentDialog.getDialogPane();
	            contentPane.getStylesheets().add(
	                    getClass().getResource("/applicationMain/application.css").toExternalForm());
	            contentPane.getStyleClass().add("alertStyle");

	            TextField contentField = contentDialog.getEditor();
	            contentField.textProperty().addListener((obs, oldVal, newVal) -> {
	                if (newVal.length() > 500) {
	                    contentField.setText(newVal.substring(0, 500));
	                }
	            });

	            Optional<String> contentResult = contentDialog.showAndWait();

	            if (contentResult.isEmpty()) {
	                return; // user cancelled
	            }

	            String newContent = contentResult.get().trim();
	            String contentError = DiscussionInputRecognizer.checkPostContent(newContent);

	            if (!contentError.isEmpty()) {
	                showValidationError(contentError);
	                return;
	            }

	            // update database
	            try {
					theDatabase.updatePost(p.getPostID(), newTitle, newContent);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
	            
	            stage.close();
	            refreshPostFeed(postFeed, "", "");
	        });

	        buttons.getChildren().add(button_update);

	    }
	    //Show Reply button only if the post is not deleted
	    if (!p.getDeleted()) {
	        Button button_Reply = new Button("Reply");
	        button_Reply.setOnAction((_) -> {
	            TextInputDialog Dialog_reply = new TextInputDialog();
	            Dialog_reply.setTitle("Add a Reply");
	            Dialog_reply.setHeaderText("Reply to " + p.getAuthor());
	            Dialog_reply.setContentText("Enter your reply:");
	            DialogPane pane = Dialog_reply.getDialogPane();
	            pane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	            pane.getStyleClass().add("alertStyle");      
	            
	            TextField replyField = Dialog_reply.getEditor();
	            //listener to track length of the input
	            replyField.textProperty().addListener((obs, oldVal, newVal) -> {
	                if (newVal.length() > 500) {
	                    replyField.setText(newVal.substring(0, 500));
	                }
	            });
	            
	            Optional<String> result = Dialog_reply.showAndWait();
	            
	            //trim and validate the reply is <= 500
	            result.ifPresent(content -> {
	            	String trimmed = content.trim();
	                if (trimmed.isEmpty()) {
	                    showValidationError("Reply cannot be empty.");
	                } else if (trimmed.length() > 500) {
	                    showValidationError("Reply is too long! (Max 500 characters)");
	                } else {
	                    theDatabase.createReply(p.getPostID(), theUser.getUserName(), trimmed);
	                    stage.close();
	                    showFullPost(p); 
	                }
	            });
	        });
	        buttons.getChildren().add(button_Reply);
	    }

	    layout.getChildren().addAll(postContent, postTime, buttons);
	    
	    VBox replyList = new VBox(5);
	    for (reply r : theDatabase.getRepliesForPost(p.getPostID())) {
	    	// Limits displayed time to the minute
	    	String replyRawTime = r.getTimestamp();
	        String replyTrimmedTime = replyRawTime.length() > 16 ? replyRawTime.substring(0, 16) : replyRawTime;
	        String replyText = String.format("[%s] %s: %s", 
	                            replyTrimmedTime, 
	                            r.getAuthor(), 
	                            r.getContent());
	        
	        Label rLabel = new Label(replyText);
	        rLabel.setWrapText(true);
	        rLabel.setPrefWidth(240);
	        
	        HBox replyBox = new HBox(10);
	        replyBox.getChildren().add(rLabel);
	        
	        
	        if (theUser.getUserName().equals(r.getAuthor())) {

	            Button editReplyBtn = new Button("Edit");
	            editReplyBtn.setStyle("-fx-font-size: 9px; -fx-padding: 2 6 2 6;");

	            editReplyBtn.setOnAction(e -> {

	                TextInputDialog dialog = new TextInputDialog(r.getContent());
	                dialog.setTitle("Edit Reply");
	                dialog.setHeaderText("Edit your reply:");
	                dialog.setContentText("Content:");

	                Optional<String> result = dialog.showAndWait();

	                if (result.isEmpty()) return;

	                String newContent = result.get().trim();
	                String error = DiscussionInputRecognizer.checkPostContent(newContent);

	                if (!error.isEmpty()) {
	                    showValidationError(error);
	                    return;
	                }

	                try {
	                    theDatabase.updateReply(r.getReplyID(), newContent);
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }

	                // Refresh just this post view
	                stage.close();
	                showFullPost(p);
	            });

	            replyBox.getChildren().add(editReplyBtn);
	        
	        }
	        replyList.getChildren().add(replyBox);
	    }

	    ScrollPane scroller = new ScrollPane(replyList);
	    layout.getChildren().add(scroller);
	    scroller.setPrefHeight(300);
	    
	    layout.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    postContent.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());


	    stage.setScene(new Scene(layout, 400, 500));
	    stage.show();
	}
}
