package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.*;
import entityClasses.InvitationCode;
import entityClasses.User;
import entityClasses.post;
import entityClasses.reply;



/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 * @version 2.01		2025-12-17 Minor updates for Spring 2026
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create tmp_pass column for storage
		statement.execute("ALTER TABLE userDB ADD COLUMN IF NOT EXISTS tmp_password VARCHAR(255)");
		
	  	// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10),"
	    		+ "expiresAt TIMESTAMP, "
	            + "usedAt TIMESTAMP)";
	    statement.execute(invitationCodesTable);
	    
	    // Create the post table to store all user posts
	    String createPostsTable = "CREATE TABLE IF NOT EXISTS posts (" 
	    	    + "postID INT AUTO_INCREMENT PRIMARY KEY, " 
	    	    + "author VARCHAR(255), " 
	    	    + "title VARCHAR(255), " 
	    	    + "thread VARCHAR(255),"
	    	    + "content TEXT, "
	    	    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " 
	    	    + "isDeleted BOOLEAN DEFAULT FALSE)";
	    statement.execute(createPostsTable);

	    // Create the reply table to store all replys to a post
	    String createRepliesTable = "CREATE TABLE IF NOT EXISTS replies (" 
	    	    + "replyID INT AUTO_INCREMENT PRIMARY KEY, " 
	    	    + "parentPostID INT, " 
	    	    + "author VARCHAR(255), " 
	    	    + "content TEXT, " 
	    	    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " 
	    	    + "FOREIGN KEY (parentPostID) REFERENCES posts(postID))"; 
	    statement.execute(createRepliesTable);
	    
	    // Create the read_replies table to store which replies to a users post are read
	    String createReadRepliesTable =  "CREATE TABLE IF NOT EXISTS read_replies (" 
	    		+ "userName VARCHAR(255), " 
	    		+ "replyID INT, "
	    		+ "PRIMARY KEY (userName, replyID))";
	    statement.execute(createReadRepliesTable);

	    // Create the read_posts table to store which a users has read
	    String createReadPostsTable =  "CREATE TABLE IF NOT EXISTS read_posts (" 
	    		+ "userName VARCHAR(255), " 
	    		+ "postID INT, "
	    		+ "PRIMARY KEY (userName, postID))";
	    statement.execute(createReadPostsTable);
	    
	    String createFlagsTable = "CREATE TABLE IF NOT EXISTS flags (" 
	    	    + "flagID INT AUTO_INCREMENT PRIMARY KEY, "
	    	    + "postID INT, "
	    	    + "flaggedBy VARCHAR(255), "
	    	    + "postAuthor VARCHAR(255), "
	    	    + "category VARCHAR(255), "
	    	    + "description TEXT, "
	    	    + "suggestedAction TEXT, "
	    	    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	    	    + "FOREIGN KEY (postID) REFERENCES posts(postID))";

	    	statement.execute(createFlagsTable);
	}


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with <Select User> at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}

	/*******
	 *  <p> Method: List getAllUsers() </p>
	 *  
	 *  <P> Description: Generate an List of Strings, one for each user in the database,
	 *  used for displaying all users to an admin </p>
	 *  
	 *  @return a list of userNames and emailAddress found in the database.
	 */
	
	public List<String> getAllUsers(){
		List<String> userList = new ArrayList<>();
		String query = "SELECT userName, emailAddress, firstName, lastName, adminRole, newRole1, newRole2 FROM userDB";
		
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				List<String> activeRoles = new ArrayList<>();
				
				//add roles to list when true
				if(rs.getString("adminRole").equalsIgnoreCase("true")) {activeRoles.add("Admin");};
				if(rs.getString("newRole1").equalsIgnoreCase("true")) {activeRoles.add("Role1");};
				if(rs.getString("newRole2").equalsIgnoreCase("true")) {activeRoles.add("Role2");};
			    
			    String roles = String.join(", ", activeRoles);
			    
				String list = String.format("\nUser: %s (%s %s), Email: %s\nRoles: %s\n", 
					rs.getString("userName"),
					rs.getString("firstName"),
					rs.getString("lastName"),
					rs.getString("emailAddress"),
					roles);
				userList.add(list);
			}
		} catch (SQLException e) {
	        return null;
	    }
		//stage -> Scene -> Plane 
		
		
		return userList;
	}
	
	// TODO: Add javaDocs
	public void createFlag(int postID, String flaggedBy, String postAuthor,
            String category, String description, String suggestedAction) {

		String query = "INSERT INTO flags (postID, flaggedBy, postAuthor, category, description, suggestedAction) "
		       + "VALUES (?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		pstmt.setInt(1, postID);
		pstmt.setString(2, flaggedBy);
		pstmt.setString(3, postAuthor);
		pstmt.setString(4, category);
		pstmt.setString(5, description);
		pstmt.setString(6, suggestedAction);
		
		pstmt.executeUpdate();
		} catch (SQLException e) {
		e.printStackTrace();
		}
	}
	
	// TODO: Add javaDocs
	public List<String> getAllFlags() {
	    List<String> result = new ArrayList<>();

	    String query = "SELECT * FROM flags ORDER BY timestamp DESC";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            String flagInfo = String.format(
	                "Post ID: %d\nFlagged By: %s\nAuthor: %s\nCategory: %s\nSuggested Action: %s\nDescription: %s\nTime: %s",
	                rs.getInt("postID"),
	                rs.getString("flaggedBy"),
	                rs.getString("postAuthor"),
	                rs.getString("category"),
	                rs.getString("suggestedAction"),
	                rs.getString("description"),
	                rs.getString("timestamp")
	            );

	            result.add(flagInfo);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return result;
	}
	
	/*******
	 *  <p> Method: List createThread(String name) </p>
	 *  
	 *  <P> Description: creates a new thread by making a blank post. Blank posts contain the value
	 *  "THREADBLANK" for its title, author, and content. The given name is used for the thread.</p>
	 *  
	 *  @param name The name for the new thread.
	 */
	public void createThread(String name) {
		createPost("THREADBLANK", "THREADBLANK", name, "THREADBLANK");
	}
	
	/*******
	 *  <p> Method: List editThread(String oldName, String newName) </p>
	 *  
	 *  <P> Description: Edits all post and replies under the thread name to have the new name. </p>
	 *  
	 *  @param oldName String containing the old thread name
	 *  @param newName String containing the new thread name
	 */
	public void editThread(String oldName, String newName) {
		// ACCOUNTS FOR POSTS TABLE
		String query = "UPDATE posts SET thread = ? WHERE thread = ?";
	    try (PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setString(1, newName);
	        ps.setString(2, oldName);
	        ps.executeUpdate();
	    } catch(SQLException e) {
	    	System.out.println("ERROR IN EDIT POST THREAD");
	    	e.printStackTrace();
	    }
	    
	    // ACOUNT FOR REPLIES TABLE
	    // TODO: will error if replies table is empty
		query = "UPDATE replies SET thread = ? WHERE thread = ?";
	    try (PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setString(1, newName);
	        ps.setString(2, oldName);
	        ps.executeUpdate();
	    } catch(SQLException e) {
	    	System.out.println("ERROR IN EDIT REPLY THREAD");
	    	e.printStackTrace();
	    }
	}
	
	/*******
	 *  <p> Method: List deleteThread(String name) </p>
	 *  
	 *  <P> Description: deletes the THREADBLANK post from the database and then edits all posts
	 *  under the deleted thread to be under "General".</p>
	 *  
	 *  @param name The name for thread to be deleted.
	 */
	public void deleteThread(String name) {
		// delete THREADBLANK from post table
		String query = "DELETE FROM posts WHERE thread = ? AND title = ?";
	    try (PreparedStatement ps = connection.prepareStatement(query)) {
	        ps.setString(1, name);
	        ps.setString(2, "THREADBLANK");
	        ps.executeUpdate();
	    } catch(SQLException e) {
	    	System.out.println("ERROR IN DELETE POST THREAD");
	    	e.printStackTrace();
	    }
	    
	    // might need to check replies table?
	    
	    // move all threads to general
	    editThread(name, "General");
	}
	
	/*******
	 *  <p> Method: List getThreadBlanks(String name) </p>
	 *  
	 *  <P> Description: returns a list of all THREADBLANK posts in the database.</p>
	 *  
	 *  @return list of post objects
	 */
	public List<post> getThreadBlanks() {
		List<post> threadBlanks = new ArrayList<>();
		
		
		String query = "SELECT * FROM posts WHERE author = ? AND title = ? AND content = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, "THREADBLANK");
			pstmt.setString(2, "THREADBLANK");
			pstmt.setString(3, "THREADBLANK");

			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				threadBlanks.add(new post(
						rs.getInt("postID"),
						rs.getString("author"), 
						rs.getString("title"),
                       rs.getString("content"), 
                       rs.getString("thread"),
                       rs.getString("timestamp"), 
                       rs.getBoolean("isDeleted")));
			}
			pstmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return threadBlanks;
	}
	
	
/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// CHECK FOR INJECTION
//		if (!DatabaseInputRecognizer.checkInjection(user.getUserName()) ||
//				!DatabaseInputRecognizer.checkInjection(user.getPassword())) {
//			System.out.println("SQL INJECTION FOUND IN LOGIN ADMIN");
//			return false;
//		}
			
		
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			restorePassword(user.getUserName());
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// CHECK FOR INJECTION
//		if (!DatabaseInputRecognizer.checkInjection(user.getUserName()) ||
//				!DatabaseInputRecognizer.checkInjection(user.getPassword())) {
//			System.out.println("SQL INJECTION FOUND IN LOGIN ADMIN");
//			return false;
//		}
			
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			restorePassword(user.getUserName());
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
//		// CHECK FOR INJECTION
//		if (!DatabaseInputRecognizer.checkInjection(user.getUserName()) ||
//				!DatabaseInputRecognizer.checkInjection(user.getPassword())) {
//			System.out.println("SQL INJECTION FOUND IN LOGIN ADMIN");
//			return false;
//		}
			
		
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			restorePassword(user.getUserName());
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}	
	
	/*******
	 * <p> Method: void createPost(String author, String content) </p>
	 * 
	 * <p> Description: Take a posts author, title and contents and create a new post object.
	 * This object is then added to the posts database table.
	 * </p>
	 * 
	 * @param String author : the author of the new post
	 *
	 * @param String title : title of the new post, has to be <= 32 characters
	 * 
	 * @param String contents : actual text of the new post, has to be <= 500 characters
	 */
	public void createPost(String author, String title, String thread, String content) {
		String query = "INSERT INTO posts (author, title, thread, content) VALUES (?,?,?,?)";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, author);
			pstmt.setString(2, title);
			pstmt.setString(3, thread);
			pstmt.setString(4, content);
			pstmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// Amy updated so it can verify post ownership before deleting
	/**
	 * <p> Method: boolean deletePost(int postID, String currentUsername) </p>
	 * 
	 * <p> Description: Deletes a post by setting isDeleted flag to true.
	 * Only the post author can delete the post.
	 * Authorization check: Verifies current user is the post author.
	 * </p>
	 * 
	 * @param postID the ID of the post to delete
	 * @param currentUsername the username of the user requesting deletion
	 * @return true if deletion successful, false if unauthorized or error
	 */
	public boolean deletePost(int postID, String currentUsername) {
	    // Verify input
	    if (postID <= 0 || currentUsername == null || currentUsername.isEmpty()) {
	        System.err.println("ERROR: Invalid postID or username");
	        return false;
	    }
	    
	    // Get post author from database (authorization check)
	    String getAuthorQuery = "SELECT author FROM posts WHERE postID = ?";
	    try (PreparedStatement getAuthorStmt = connection.prepareStatement(getAuthorQuery)) {
	        getAuthorStmt.setInt(1, postID);
	        ResultSet rs = getAuthorStmt.executeQuery();
	        
	        // Check if post exists
	        if (!rs.next()) {
	            System.err.println("ERROR: Post not found");
	            return false;
	        }
	        
	        String postAuthor = rs.getString("author");
	        
	        // Verify current user is the original post author
	        if (!postAuthor.equals(currentUsername)) {
	            System.err.println("ERROR: CWE-863 Authorization Failure - User is not post author");
	            System.err.println("  Post author: " + postAuthor);
	            System.err.println("  Current user: " + currentUsername);
	            return false;  // Deny unauthorized deletion
	        }
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error retrieving post author: " + e.getMessage());
	        return false;
	    }
	    
	    // Authorization has been verified, proceed with deletion
	    String deleteQuery = "UPDATE posts SET isDeleted = TRUE WHERE postID = ?";
	    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
	        deleteStmt.setInt(1, postID);
	        deleteStmt.executeUpdate();
	        System.out.println("SUCCESS: Post " + postID + " deleted by " + currentUsername);
	        return true;
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error deleting post: " + e.getMessage());
	        return false;
	    }
	}

	// Amy updated so it can verify post ownership before deleting

	/**
	 * <p> Method: boolean updatePost(int postId, String newTitle, String newContent, String currentUsername) </p>
	 * 
	 * <p> Description: Updates title and content of a post.
	 * Only the post author can update the post.
	 * Authorization check: Verifies current user is the post author.
	 * </p>
	 * 
	 * @param postId the ID of the post to update
	 * @param newTitle the new title
	 * @param newContent the new content
	 * @param currentUsername the username of the user requesting update
	 * @return true if update successful, false if unauthorized or error
	 */
	public boolean updatePost(int postId, String newTitle, String newContent, String currentUsername) {
	    // STEP 1: Verify inputs
	    if (postId <= 0 || currentUsername == null || currentUsername.isEmpty()) {
	        System.err.println("ERROR: Invalid postID or username");
	        return false;
	    }
	    
	    if (newTitle == null || newTitle.isEmpty() || newContent == null || newContent.isEmpty()) {
	        System.err.println("ERROR: Title and content cannot be empty");
	        return false;
	    }
	    
	    // Get post author for authorization check
	    String getAuthorQuery = "SELECT author FROM posts WHERE postID = ?";
	    try (PreparedStatement getAuthorStmt = connection.prepareStatement(getAuthorQuery)) {
	        getAuthorStmt.setInt(1, postId);
	        ResultSet rs = getAuthorStmt.executeQuery();
	        
	        // Check if post exists
	        if (!rs.next()) {
	            System.err.println("ERROR: Post not found");
	            return false;
	        }
	        
	        String postAuthor = rs.getString("author");
	        
	        // Verify current user is the original post author
	        if (!postAuthor.equals(currentUsername)) {
	            System.err.println("ERROR: CWE-863 Authorization Failure - User is not post author");
	            System.err.println("  Post author: " + postAuthor);
	            System.err.println("  Current user: " + currentUsername);
	            return false;  // ← DENY unauthorized update
	        }
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error retrieving post author: " + e.getMessage());
	        return false;
	    }
	    
	    // Authorization has been verified, proceed with update
	    String updateQuery = "UPDATE posts SET title = ?, content = ? WHERE postID = ?";
	    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	        updateStmt.setString(1, newTitle);
	        updateStmt.setString(2, newContent);
	        updateStmt.setInt(3, postId);
	        updateStmt.executeUpdate();
	        System.out.println("SUCCESS: Post " + postId + " updated by " + currentUsername);
	        return true;
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error updating post: " + e.getMessage());
	        return false;
	    }
	}
	
	
	/*******
	 * <p> Method: List<post> searchPost(String search) </p>
	 * 
	 * <p> Description: This method is called whenever a new character is typed into the search bar,
	 * It scans through all posts titles and contents and compares it to the inputed string. It displays
	 * the most similar results in the list
	 * </p>
	 * 
	 * @param String search : the string that is being searched in titles and contents, updated every character input
	 *
	 * @return List<post> result : an arraylist of the most similar posts to the search input
	 */
	public List<post> searchPost(String search, String thread){
		List<post> result = new ArrayList<>();
		String query = "SELECT * FROM posts WHERE (title LIKE ? OR content LIKE ?) AND (thread LIKE ?) "
				+ "AND NOT (title = ?)";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, "%" + search + "%");
			pstmt.setString(2, "%" + search + "%");
			pstmt.setString(3, "%" + thread + "%");
			pstmt.setString(4, "THREADBLANK");

			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				result.add(new post(
						rs.getInt("postID"),
						rs.getString("author"), 
						rs.getString("title"),
                       rs.getString("content"), 
                       rs.getString("thread"),
                       rs.getString("timestamp"), 
                       rs.getBoolean("isDeleted")));
			}
			pstmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/*******
	 * <p> Method: List<post> searchPost(String search) </p>
	 * 
	 * <p> Description: This method is called whenever a new character is typed into the search bar,
	 * It scans through all posts titles and contents and compares it to the inputed string. It displays
	 * the most similar results in the list
	 * </p>
	 * 
	 * @param String search : the string that is being searched in titles and contents, updated every character input
	 *
	 * @return List<post> result : an arraylist of the most similar posts to the search input
	 */
	public List<post> searchPost(String author){
		List<post> result = new ArrayList<>();
		String query = "SELECT * FROM posts WHERE author LIKE ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, "%" + author + "%");
			

			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				result.add(new post(
						rs.getInt("postID"),
						rs.getString("author"), 
						rs.getString("title"),
                       rs.getString("content"), 
                       rs.getString("thread"),
                       rs.getString("timestamp"), 
                       rs.getBoolean("isDeleted")));
			}
			pstmt.execute();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/*******
	 * <p> Method: void createReply(int parentID, String author, String content) </p>
	 * 
	 * <p> Description: takes a parent post's id, child's author and the reply contents. It creates
	 * a query to add the reply to the replay table	in the database. This links them together so that 
	 * replys can be displayed under a post
	 * </p>
	 * 
	 * @param int parentID : the ID of the post the reply is for
	 *
	 * @param String author : the author of the new reply
	 * 
	 * @param String content : the contents of the new reply
	 */
	public void createReply(int parentID, String author, String content) {
	    String query = "INSERT INTO replies (parentPostID, author, content) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, parentID);
	        pstmt.setString(2, author);
	        pstmt.setString(3, content);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Amy updated so it can verify post ownership before deleting

	/**
	 * <p> Method: boolean updateReply(int replyID, String newContent, String currentUsername) </p>
	 * 
	 * <p> Description: Updates content of a reply.
	 * Only the reply author can update the reply.
	 * Authorization check: Verifies current user is the reply author.
	 * </p>
	 * 
	 * @param replyID the ID of the reply to update
	 * @param newContent the new content
	 * @param currentUsername the username of the user requesting update
	 * @return true if update successful, false if unauthorized or error
	 */
	public boolean updateReply(int replyID, String newContent, String currentUsername) throws SQLException {
	    // Verify input
	    if (replyID <= 0 || currentUsername == null || currentUsername.isEmpty()) {
	        System.err.println("ERROR: Invalid replyID or username");
	        return false;
	    }
	    
	    if (newContent == null || newContent.isEmpty()) {
	        System.err.println("ERROR: Content cannot be empty");
	        return false;
	    }
	    
	    // Get reply author for authorization check
	    String getAuthorQuery = "SELECT author FROM replies WHERE replyID = ?";
	    try (PreparedStatement getAuthorStmt = connection.prepareStatement(getAuthorQuery)) {
	        getAuthorStmt.setInt(1, replyID);
	        ResultSet rs = getAuthorStmt.executeQuery();
	        
	        // Check if reply exists
	        if (!rs.next()) {
	            System.err.println("ERROR: Reply not found");
	            return false;
	        }
	        
	        String replyAuthor = rs.getString("author");
	        
	        // Verify current user is the reply author
	        if (!replyAuthor.equals(currentUsername)) {
	            System.err.println("ERROR: CWE-863 Authorization Failure - User is not reply author");
	            System.err.println("  Reply author: " + replyAuthor);
	            System.err.println("  Current user: " + currentUsername);
	            return false;  // Deny unauthorized update
	        }
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error retrieving reply author: " + e.getMessage());
	        return false;
	    }
	    
	    // Authorization verified, proceed with update
	    String updateQuery = "UPDATE replies SET content = ? WHERE replyID = ?";
	    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	        updateStmt.setString(1, newContent);
	        updateStmt.setInt(2, replyID);
	        updateStmt.executeUpdate();
	        System.out.println("SUCCESS: Reply " + replyID + " updated by " + currentUsername);
	        return true;
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error updating reply: " + e.getMessage());
	        return false;
	    }
	}
	
	/*******
	 * <p> Method: void markReplyAsRead(int replyId, String userName) </p>
	 * 
	 * <p> Description: Marks a specific reply as read by the given user. Uses MERGE INTO
	 * to avoid duplicate entries if the reply has already been marked as read.</p>
	 * 
	 * @param replyId the ID of the reply being marked as read
	 * @param userName the username of the user who read the reply
	 */
	public void markReplyAsRead(int replyId, String userName ) {
		String query = "MERGE INTO read_replies (userName, replyID) VALUES (?, ?)";
	
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setInt(2, replyId);
			pstmt.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*******
	 * <p> Method: void markPostAsRead(int postId, String userName) </p>
	 * 
	 * <p> Description: Marks a specific post as read by the given user. Uses MERGE INTO
	 * to avoid duplicate entries if the post has already been marked as read.</p>
	 * 
	 * @param postId the ID of the post being marked as read
	 * @param userName the username of the user who read the post
	 */
	public void markPostAsRead(int postId, String userName ) {
		String query = "MERGE INTO read_posts (userName, postID) VALUES (?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setInt(2, postId);
			pstmt.executeUpdate();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*******
	 * <p> Method: boolean isReplyRead(String userName, int replyId) </p>
	 * 
	 * <p> Description: Checks whether a specific reply has been read by the given user
	 * by looking for a matching entry in the read_replies table.</p>
	 * 
	 * @param userName the username of the user to check
	 * @param replyId the ID of the reply to check
	 * @return true if the user has read the reply, false otherwise
	 */
	public boolean isReplyRead(String userName, int replyId) {
		
		String query = "SELECT COUNT(*) FROM read_replies WHERE userName = ? AND replyID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setInt(2, replyId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1) > 0;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*******
	 * <p> Method: boolean isPostRead(String userName, int postId) </p>
	 * 
	 * <p> Description: Checks whether a specific post has been read by the given user
	 * by looking for a matching entry in the read_posts table.</p>
	 * 
	 * @param userName the username of the user to check
	 * @param postId the ID of the post to check
	 * @return true if the user has read the post, false otherwise
	 */
	public boolean isPostRead(String userName, int postID) {
		String query = "SELECT COUNT(*) FROM read_posts WHERE userName = ? AND postID = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setInt(2, postID);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1) > 0;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*******
	 * <p> Method: int getTotalReplyCount(int postId) </p>
	 * 
	 * <p> Description: Returns the total number of replies for a given post
	 * by counting all entries in the replies table with the matching post ID.</p>
	 * 
	 * @param postId the ID of the post to count replies for
	 * @return the total number of replies, or 0 if none exist or an error occurs
	 */
	public int getTotalReplyCount(int postId) {
		String query = "SELECT COUNT(*) FROM replies WHERE parentPostID = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/*******
	 * <p> Method: int getUnreadReplyCount(String userName, int postId) </p>
	 * 
	 * <p> Description: Returns the number of replies for a given post that the specified
	 * user has not yet read. This is determined by counting replies whose IDs are not
	 * present in the read_replies table for that user.</p>
	 * 
	 * @param userName the username of the user to check unread replies for
	 * @param postId the ID of the post to count unread replies for
	 * @return the number of unread replies, or 0 if none exist or an error occurs
	 */
	public int getUnreadReplyCount(String userName,int postId) {	    
	    
		String query = "SELECT COUNT(*) FROM replies WHERE parentPostID = ? " 
				+ "AND replyID NOT IN (SELECT replyID FROM read_replies WHERE userName = ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			pstmt.setString(2, userName);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*******
	 * <p> Method: String generateOTP(String username) </p>
	 * 
	 * <p> Description: Generate one time password for a user.</p>
	 * 
	 * @param User for the user whos password will change.
	 *
	 * @param tmp_pass for password the selected user's password will change to.
	 * 
	 * @return the new password that the user's password was changed to
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateOTP(String tmp_pass, String User) {
	    String code = tmp_pass; 
	    String query = "UPDATE userDB SET tmp_password = password, password = ? WHERE username = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	pstmt.setString(1, code);
	    	pstmt.setString(2, User);
	    	pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}
	
	
	/*******
	 * <p> Method: void restorePassword(String username) </p>
	 * 
	 * <p> Description: Restore original password after OTP is used.</p>
	 * 
	 * @param username for the user whos password will change.
	 *
	 *
	 */
	// Generates a new invitation code and inserts it into the database.
	public void restorePassword(String username) {
	    String query = "UPDATE userDB SET password = tmp_password, tmp_password = NULL WHERE username = ? AND tmp_password IS NOT NULL";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	pstmt.setString(1, username);
	    	pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
		String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
		Instant expires = Instant.now().plus(Duration.ofMinutes(15));
		String query = "INSERT INTO InvitationCodes (code, emailAddress, role, expiresAt, usedAt) VALUES (?, ?, ?, ?, NULL)";
	
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setString(2, emailAddress);
			pstmt.setString(3, role);
			pstmt.setTimestamp(4, Timestamp.from(expires));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
	}
	

	/*******
	 * <p> Method: Map<String, InvitationCode> getAllInvitationCodes() </p>
	 * 
	 * <p> Description: Gives user all current invite codes within the database.</p>
	 * 
	 * @return Map of invite codes. Key is the code, value is the details about said code.
	 * 
	 */
	public Map<String, InvitationCode> getAllInvitationCodes() {
	    clearInvalidInviteCodes();
	    Map<String, InvitationCode> codes = new HashMap<>();

	    String query = "SELECT code, expiresAt, emailAddress FROM InvitationCodes";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            String code = rs.getString("code");
	            Timestamp expiresAt = rs.getTimestamp("expiresAt");
	            String email = rs.getString("emailAddress");

	            InvitationCode invite =
	                    new InvitationCode(code, expiresAt, email);

	            codes.put(code, invite);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return codes;
	}

	
	/*******
	 * <p> Method: clearInvalidInviteCodes() </p>
	 * 
	 * <p>Clears any invite code that is no longer current.</p>
	 * 
	 * 
	 */
	public void clearInvalidInviteCodes() {
		 String query = "DELETE FROM InvitationCodes " +
                 "WHERE expiresAt <= CURRENT_TIMESTAMP";

		  try (PreparedStatement pstmt = connection.prepareStatement(query)) 
		  {
		      int rowsDeleted = pstmt.executeUpdate();
		      System.out.println("Removed " + rowsDeleted + " expired invite codes.");
		  } 
		  catch (SQLException e) 
		  {
		      e.printStackTrace();
		  }
		  
	}


	
	/*******
	 * <p> Method: validateInviteCode(String code, String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given code, email address and role, this method check the database to see if the code
	 * is valid.  If it is valid it will return True and for invalid it returns False. </p>
	 * 
	 *
	 * @param code specifies the code for this new user.
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return True if code is valid or False if code is invalid
	 * 
	 */
	public boolean validateInviteCode(String code, String emailAddress, String role) {
		String query = "SELECT expiresAT, usedAt, emailAddress, role FROM InvitationCodes WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			
			try(ResultSet rs = pstmt.executeQuery()){
				if(!rs.next()) return false;
				
				Timestamp expiresAt = rs.getTimestamp("expiresAt");
				Timestamp usedAt = rs.getTimestamp("usedAt");
				String dbEmailAddress = rs.getString("emailAddress");
				String dbRole= rs.getString("role");
				
				if(usedAt != null) return false;
				if(expiresAt != null && expiresAt.before(new Timestamp(System.currentTimeMillis()))) return false;
				if (dbEmailAddress != null && !dbEmailAddress.equalsIgnoreCase(emailAddress)) return false;
	            if (dbRole != null && !dbRole.equals(role)) return false;

				
				String update = "UPDATE InvitationCodes SET usedAt = CURRENT_TIMESTAMP WHERE code = ?";
				try(PreparedStatement upstmt = connection.prepareStatement(update)){
					upstmt.setString(1, code);
					return upstmt.executeUpdate() == 1;
				}
			}
		} 
		catch (SQLException e) {
	        e.printStackTrace();
	        return false;
		}
		
		
	}
	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				System.out.print(resultSet);
				System.out.print(resultSet.getInt("count"));
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	 //     System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter >= 0) {
	        		System.out.println("TEST 1");
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			System.out.println("TEST 2");
	        			pstmt2.setString(1, code);
	        			System.out.println(pstmt2);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        			System.out.println("TEST 3");
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}

	/*******
	 * <p> Method: void updatePassword(String username, String password) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param password is the new password of the user
	 *  
	 */
	public void updatePassword(String username, String password) {
	    String query = "UPDATE userDB SET password = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, password);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPassword = password;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentNewRole1 = rs.getBoolean(10);
	    	currentNewRole2 = rs.getBoolean(11);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	
	// Amy updated so it can verify admin role verification

	/**
	 * <p> Method: boolean updateUserRole(String username, String role, String value, String currentUsername, boolean currentUserIsAdmin) </p>
	 * 
	 * <p> Description: Updates a user's role.
	 * Only admin users can update user roles.
	 * Authorization check: Verifies current user has adminRole = TRUE.
	 * </p>
	 * 
	 * @param username the username of the user whose role is being updated
	 * @param role the role to update ("Admin", "Role1", "Role2")
	 * @param value the new value ("true" or "false")
	 * @param currentUsername the username of the user requesting update
	 * @param currentUserIsAdmin whether current user has admin role
	 * @return true if update successful, false if unauthorized or error
	 */
	public boolean updateUserRole(String username, String role, String value, String currentUsername, boolean currentUserIsAdmin) {
	    // Verify inputs
	    if (username == null || username.isEmpty() || role == null || role.isEmpty() || 
	        value == null || value.isEmpty() || currentUsername == null || currentUsername.isEmpty()) {
	        System.err.println("ERROR: Invalid parameters");
	        return false;
	    }
	    
	    // Verify current user is admin for authorization check
	    if (!currentUserIsAdmin) {
	        System.err.println("ERROR: CWE-863 Authorization Failure - User is not admin");
	        System.err.println("  Current user: " + currentUsername);
	        System.err.println("  Admin privilege required: YES");
	        System.err.println("  Current user is admin: NO");
	        return false;  // ← DENY non-admin role update
	    }
	    
	    // Additional check: Can only be done during initial setup or by system admin
	    if (role.equals("Admin") && value.equals("true") && !username.equals(currentUsername)) {

	    }
	    
	    // Authorization verified, proceed with role update
	    String roleColumn = null;
	    if (role.equals("Admin")) {
	        roleColumn = "adminRole";
	    } else if (role.equals("Role1")) {
	        roleColumn = "newRole1";
	    } else if (role.equals("Role2")) {
	        roleColumn = "newRole2";
	    } else {
	        System.err.println("ERROR: Invalid role specified");
	        return false;
	    }
	    
	    String updateQuery = "UPDATE userDB SET " + roleColumn + " = ? WHERE userName = ?";
	    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
	        // Convert "true"/"false" string to boolean value for database
	        boolean boolValue = value.equals("true");
	        updateStmt.setBoolean(1, boolValue);
	        updateStmt.setString(2, username);
	        
	        int rowsAffected = updateStmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            System.out.println("SUCCESS: User " + username + " role " + role + " updated to " + value + " by admin " + currentUsername);
	            
	            // Update current user's role if it's the current user being modified
	            if (username.equals(currentUsername)) {
	                if (role.equals("Admin"))
	                    currentAdminRole = boolValue;
	                else if (role.equals("Role1"))
	                    currentNewRole1 = boolValue;
	                else if (role.equals("Role2"))
	                    currentNewRole2 = boolValue;
	            }
	            
	            return true;
	        } else {
	            System.err.println("ERROR: Failed to update user role");
	            return false;
	        }
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error updating user role: " + e.getMessage());
	        return false;
	    }
	}
	
	// Amy updated so it can verify admin role verification

	/**
	 * <p> Method: boolean deleteUserByUsername(String usernameToDelete, String currentUsername, boolean currentUserIsAdmin) </p>
	 * 
	 * <p> Description: Deletes a user account from the database.
	 * Only admin users can delete user accounts.
	 * Authorization check: Verifies current user has adminRole = TRUE.
	 * </p>
	 * 
	 * @param usernameToDelete the username of the user to delete
	 * @param currentUsername the username of the user requesting deletion
	 * @param currentUserIsAdmin whether current user has admin role
	 * @return true if deletion successful, false if unauthorized or error
	 */
	public boolean deleteUserByUsername(String usernameToDelete, String currentUsername, boolean currentUserIsAdmin) {
	    // Verify inputs
	    if (usernameToDelete == null || usernameToDelete.isEmpty() || 
	        currentUsername == null || currentUsername.isEmpty()) {
	        System.err.println("ERROR: Invalid username");
	        return false;
	    }
	    
	    // Verify current user is admin for authorization check
	    if (!currentUserIsAdmin) {
	        System.err.println("ERROR: CWE-863 Authorization Failure - User is not admin");
	        System.err.println("  Current user: " + currentUsername);
	        System.err.println("  Admin privilege required: YES");
	        System.err.println("  Current user is admin: NO");
	        return false;  // Deny non-admin user deletion
	    }
	    
	    // Prevent admin from deleting themselves
	    if (currentUsername.equals(usernameToDelete)) {
	        System.err.println("ERROR: Admin cannot delete their own account");
	        return false;
	    }
	    
	    // Verify user to delete exists
	    String checkUserQuery = "SELECT username FROM userDB WHERE userName = ?";
	    try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery)) {
	        checkUserStmt.setString(1, usernameToDelete);
	        ResultSet rs = checkUserStmt.executeQuery();
	        
	        if (!rs.next()) {
	            System.err.println("ERROR: User to delete not found: " + usernameToDelete);
	            return false;
	        }
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error checking if user exists: " + e.getMessage());
	        return false;
	    }
	    
	    // Authorization verified, proceed with deletion
	    String deleteQuery = "DELETE FROM userDB WHERE userName = ?";
	    try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
	        deleteStmt.setString(1, usernameToDelete);
	        int rowsAffected = deleteStmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            System.out.println("SUCCESS: User " + usernameToDelete + " deleted by admin " + currentUsername);
	            return true;
	        } else {
	            System.err.println("ERROR: Failed to delete user");
	            return false;
	        }
	    } catch (SQLException e) {
	        System.err.println("ERROR: Database error deleting user: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * <p> Description: Get the current user's username.</p>
	 * @return the username value is returned
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * <p> Description: Get the current user's password.</p>
	 * @return the password value is returned  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * <p> Description: Get the current user's first name.</p> 
	 * @return the first name value is returned 
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * <p> Description: Get the current user's middle name.</p>
	 * @return the middle name value is returned 
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * <p> Description: Get the current user's last name.</p>
	 * @return the last name value is returned 
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * <p> Description: Get the current user's preferred first name.</p>
	 * @return the preferred first name value is returned
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * <p> Description: Get the current user's email address name.</p>
	 * @return the email address value is returned 
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * @return true if this user plays an Admin role, else false
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * @return true if this user plays a Student role, else false 
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * @return true if this user plays a Reviewer role, else false 
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};

	
	/*******
	 * <p> Debugging method</p> 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * @throws SQLException if there is an issues accessing the database.
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * <p> Description: Closes the database statement and connection.</p>
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
}
