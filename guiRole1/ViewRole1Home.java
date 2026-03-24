package guiRole1;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import database.Database;
import entityClasses.User;
import entityClasses.post;
import entityClasses.reply;
import entityClasses.DiscussionInputRecognizer;


/*******
 * <p> Title: GUIReviewerHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Role1 Home Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 *  
 */

public class ViewRole1Home {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	public static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI ARea 2: This is a stub, so there are no widgets here.  For an actual role page, this are
	// would contain the widgets needed for the user to play the assigned role.
	
	
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	//
	protected static Alert alertDeleteConfirm = new Alert(AlertType.CONFIRMATION);
	//GUI AREA 6: Post section
	VBox postArea = new VBox(10);
	TextField text_searchBar = new TextField();
	Button button_CreatePost = new Button("Create New Post");
	// Scrollable Feed
	VBox postFeed = new VBox(10);
	ScrollPane scrollFeed = new ScrollPane(postFeed);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewRole1Home theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	
	protected static ComboBox<String> threadChoice;


	private static Scene theViewRole1HomeScene;	// The shared Scene each invocation populates
	protected static final int theRole = 2;		// Admin: 1; Role1: 2; Role2: 3

	/*-*******************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayRole1Home(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Role1 Home page to be displayed.
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
	public static void displayRole1Home(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewRole1Home();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());
		theView.refreshPostFeed(theView.postFeed, "", "");
				
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Student Home Page");
		theStage.setScene(theViewRole1HomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewRole1Home() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayRole2Home method.</p>
	 * 
	 */
	private ViewRole1Home() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewRole1HomeScene = new Scene(theRootPane, width, height);	// Create the scene
		theViewRole1HomeScene.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
		
		DialogPane alertDeleteConfirmPane = alertDeleteConfirm.getDialogPane();
	    alertDeleteConfirmPane.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    alertDeleteConfirmPane.getStyleClass().add("alertStyle");
	    alertDeleteConfirm.setTitle("Confirm user deletion");
	    alertDeleteConfirm.setContentText("This action cannot be undone.");
		alertDeleteConfirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Student Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 50);
		button_UpdateThisUser.setOnAction((_) -> {ControllerRole1Home.performUpdate(); });
		
		// GUI Area 2
		
			// This is a stub, so this area is empty
		
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((_) -> {ControllerRole1Home.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 200, Pos.CENTER, width-200-20, 540);
        button_Quit.setOnAction((_) -> {ControllerRole1Home.performQuit(); });
        
     	// GUI area 4 : Posts
     	setupTextUI(text_searchBar, "Arial", 16, 280, Pos.BASELINE_LEFT, 310, 50, true);
     	text_searchBar.setPromptText("Search posts by title or content");

     	setupButtonUI(button_CreatePost, "Dialog", 18, 250, Pos.CENTER, 300, 540);
     	button_CreatePost.setOnAction((_) -> showCreatePostDialog());
     	button_CreatePost.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());

     	threadChoice = new ComboBox<>();
		threadChoice.setLayoutX(150);
		threadChoice.setLayoutY(55);
		threadChoice.getSelectionModel().select(0);
		List<String> list = new ArrayList<String>();
		list.add("General");
		list.add("My Posts");
		list.add("Quizzes");
		list.add("Homework Help");
		threadChoice.setItems(FXCollections.observableArrayList(list));
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
		
		// TODO: Get list of threads from the database. Possibly move to update display
		//threadChoice.setItems(FXCollections.observableArrayList(list));

     	
     	scrollFeed.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
     	scrollFeed.setLayoutX(15);
     	scrollFeed.setLayoutY(100);
     	scrollFeed.setPrefSize(760, 420);
     	scrollFeed.setFitToWidth(true);

     	refreshPostFeed(postFeed, "", ""); 

     	// listener for search so it can not input over 500
     	text_searchBar.textProperty().addListener((_, _, newVal) -> {
     		String query = newVal;
     		if (newVal.length() > 500) {
     			query = newVal.substring(0, 500);
     			text_searchBar.setText(query);
     		}
     		refreshPostFeed(postFeed, newVal, threadChoice.getValue()); 
     	});

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
         theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
	        line_Separator4, text_searchBar, threadChoice, button_CreatePost, scrollFeed, button_Logout, button_Quit);
}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

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
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text input field
	 * 
	 * @param b		The TextField object to be initialized
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
	 * <p> Description: Refreshes the post feed of the user. This is called when something is done 
	 * to the post list like deleting or adding posts. Each post displays its read/unread
	 * status for the current user, the total number of replies, and the number of unread replies.</p>
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
	        
	        // Get read status and reply counts
	        boolean isRead = theDatabase.isPostRead(theUser.getUserName(), p.getPostID());
	        int totalReplies = theDatabase.getTotalReplyCount(p.getPostID());
	        int unreadReplies = theDatabase.getUnreadReplyCount(theUser.getUserName(), p.getPostID());
	        
	        // Build the read/unread label
	        String readStatus = "";
	        if(!p.getDeleted()) {
	        	readStatus = isRead ? "(Read)" : "(Unread)";
	        }
	        
	        // Build the reply info string
	        String replyInfo;
	        if(totalReplies > 0) {
	        	replyInfo = " | Replies: " + totalReplies + " (" + unreadReplies + " unread)";
	        }
	        else {
	        	replyInfo = " | No replies";
	        }
	        
	        // Combine into button label
	        String buttonLabel = displayHeader + " " + readStatus + "\nBy: " + p.getAuthor() + replyInfo;
	        
	        Button button_post = new Button(buttonLabel);
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
	    
	    ComboBox<String> pickThread = new ComboBox<>();
		pickThread.setLayoutX(width/2 - 50);
		pickThread.setLayoutY(55);
		List<String> dlist = new ArrayList<String>();	
		dlist.add("General");
		dlist.add("Quizzes");
		dlist.add("Homework Help");
		pickThread.setItems(FXCollections.observableArrayList(dlist));
		pickThread.getSelectionModel().select(0);

	    
	    
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

	    layout.getChildren().addAll(new Label("New Discussion"), postTitle, titleCount, postContent, pickThread, contentCount, submit);
	    dialog.setScene(new Scene(layout));
	    dialog.show();
	}
	
	/**********
	 * <p> Method: void buildReplyList(VBox replyList, List<reply> replies, 
	 *     List<Integer> unreadReplyIds, post p, Stage stage) </p>
	 * 
	 * <p> Description: Builds the reply display list for a post. Each reply shows its
	 * timestamp, author, and content. Replies that were unread are marked with a (New) 
	 * indicator. Can be filtered to show only unread replies. Also marks all displayed
	 * replies as read for the current user.</p>
	 * 
	 * @param replyList the VBox container to add reply elements to
	 * @param replies the list of replies to display
	 * @param unreadReplyIds list of reply IDs that were unread before viewing
	 * @param p the parent post
	 * @param stage the current dialog stage
	 */
	private void buildReplyList(VBox replyList, List<reply> replies, List<Integer> unreadReplyIds, post p, Stage stage) {
		replyList.getChildren().clear();
	    for (reply r : replies) {
	    	boolean wasUnread = unreadReplyIds.contains(r.getReplyID());
	    	theDatabase.markReplyAsRead(r.getReplyID(), theUser.getUserName());
	    	// Limits displayed time to the minute
	    	String replyRawTime = r.getTimestamp();
	        String replyTrimmedTime = replyRawTime.length() > 16 ? replyRawTime.substring(0, 16) : replyRawTime;
	        String newIndicator = wasUnread ? "(New) " : "";
	        String replyText = String.format("%s[%s] %s: %s", 
	        					newIndicator,
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
	            //editReplyBtn.setStyle("-fx-font-size: 15px; -fx-padding: 2 6 2 6;");

	            editReplyBtn.setOnAction(e -> {

	                TextInputDialog dialog = new TextInputDialog(r.getContent());
	                DialogPane dPane = dialog.getDialogPane();
	                dPane.getStylesheets().add(
		                    getClass().getResource("/applicationMain/application.css").toExternalForm());
		            dPane.getStyleClass().add("alertStyle");
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
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: void showFullPost(post p)</p>
	 * 
	 * <p> Description: This method displays a new panel for a user to read a post and all of its replys.
	 * This is where the user can reply to posts and delete posts. </p>
	 * 
	 * @param post p: the post that the panel is showing
	 */
	protected void showFullPost(post p) {
		theDatabase.markPostAsRead(p.getPostID(), theUser.getUserName());
		Stage stage = new Stage();
	    stage.initModality(Modality.APPLICATION_MODAL);
	    stage.setTitle(p.getTitle());
	    
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
	            replyField.textProperty().addListener((_, _, newVal) -> {
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
	    
	    if (theUser.getUserName().equals(p.getAuthor())) {

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

	    layout.getChildren().addAll(postContent, postTime, buttons);
	    VBox replyList = new VBox(5);
	    List<reply> allReplies = theDatabase.getRepliesForPost(p.getPostID());
	    List<Integer> unreadReplyIds = new ArrayList<>();
	    
	    Button toggleUnread = new Button("Show Unread Only");
	    final boolean[] showUnreadOnly = {false};
	    
	    toggleUnread.setOnAction(e -> {
	    	showUnreadOnly[0] = !showUnreadOnly[0];
	    	toggleUnread.setText(showUnreadOnly[0] ? "Show All Replies" : "Show Unread Only");
	    	
	    	if(showUnreadOnly[0]) {
	    		List<reply> unreadOnly = new ArrayList<>();
	    		for(reply r : allReplies) {
	    			if(unreadReplyIds.contains(r.getReplyID())) {
	    				unreadOnly.add(r);
	    			}
	    		}
	    		buildReplyList(replyList, unreadOnly, unreadReplyIds, p, stage);
	    	}
	    	else {
	    		buildReplyList(replyList, allReplies, unreadReplyIds, p, stage);
	    	}
	   		
	    	
	    });
	    
	    
	   
	    
	    for(reply r : allReplies) {
	    	if(!theDatabase.isReplyRead(theUser.getUserName(),r.getReplyID())) {
	    		unreadReplyIds.add(r.getReplyID());
	    	}	    	
	    }
	    //Build the initial reply list
	    buildReplyList(replyList, allReplies, unreadReplyIds, p, stage);
	    

	    layout.getChildren().add(toggleUnread);
	    ScrollPane scroller = new ScrollPane(replyList);
	    layout.getChildren().add(scroller);
	    scroller.setPrefHeight(300);
	    
	    layout.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());
	    postContent.getStylesheets().add(getClass().getResource("/applicationMain/application.css").toExternalForm());


	    stage.setScene(new Scene(layout, 400, 500));
	    stage.setOnHidden(e -> refreshPostFeed(postFeed, "", ""));
	    stage.show();
	}
}
