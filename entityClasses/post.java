package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: post.java </p>
 *
 * <p> Description: This is the class for a post object. It stores important info for the post including the post ID number, author, title,
 * contents, timestamp of posting, if it is marked as deleted and a list of the replys to the post. It contains a contrustor for the posts
 * contents, timestamp of posting, if it is marked as deleted and a list of the replys to the post. It contains a constructor for the posts
 * and getters/setters to edit post object info
 * </p>
 *
 *  <p>@author: Alexander Fuentes</p>
 */
public class post {
    private int postID;
    private String author;
    private String title;
    private String content;
    private String timestamp;
    private boolean isDeleted;
    private List<reply> replies;

    /*******
	 * <p> Method: post(int id, String author, String title, String content, String time, boolean deleted) </p>
	 * 
	 * <p> Description: The constructor for posts. It takes an ID value, Strings for the post Author, title, contents
	 * 	and time of post. It also holds a flag to tell if a post has been deleted or not </p>
	 *  
	 * @param id : the id value for this post
	 * 
	 * @param author : the author of this post
	 * 
	 * @param title : the title of this post
	 * 
	 * @param content : the info that the post contains
	 * 
	 * @param time : the time of the post
	 * 
	 * @param deleted : a flag to show if a certain post has been deleted
	 * 
	*/
    public post(int id, String author, String title, String content, String time, boolean deleted) {
        this.postID = id;
        this.author = author;
        this.title = deleted ? "(this post has been deleted)" : title;
        this.content = deleted ? "(this post has been deleted)" : content;
        this.timestamp = time;
        this.isDeleted = deleted;
        this.replies = new ArrayList<>();
    }
    
    /*******
	 * <p> Method:int getPostID() </p>
	 * 
	 * <p> Description: Getter for a post's ID value </p>
	 *  
	 *  @return this.postID : Returns the ID value of this post
	 * 
	*/
    public int getPostID() {return this.postID;}
    
    /*******
	 * <p> Method: void setPostID(int newID) </p>
	 * 
	 * <p> Description: Sets the value of a post to a new ID </p>
	 *  
	 *  @param newID : The new value to set the post's ID to
	 * 
	*/
    public void setPostID(int newID) {this.postID = newID;}
    
    
    /*******
	 * <p> Method: getAuthor() </p>
	 * 
	 * <p> Description: Sets the value of a post to a new ID </p>
	 *  
	 *  @return this.author : returns the author of the post
	 * 
	*/
    public String getAuthor() {return this.author;}
    /*******
	 * <p> Method: void setAuthor(String newAuthor) </p>
	 * 
	 * <p> Description: Sets the author of a post to a new author </p>
	 *  
	 *  @param newAuthor : The new author string to set the post's author to
	 * 
	*/
    public void setAuthor(String newAuthor) {this.author = newAuthor;}
    
    
    /*******
	 * <p> Method: String getTitle() </p>
	 * 
	 * <p> Description: Getter for a post's title string </p>
	 *  
	 *  @return this.title : Returns the title string of this post
	 * 
	*/
    public String getTitle() {return this.title;}
    
    /*******
	 * <p> Method: void setTitle(String newTitle) </p>
	 * 
	 * <p> Description: Sets the title of a post to a new title </p>
	 *  
	 *  @param newTitle : The new title string to set the post's title to
	 * 
	*/
    public void setTitle(String newTitle) {this.title = newTitle;}

    
    /*******
	 * <p> Method: String getBody() </p>
	 * 
	 * <p> Description: Getter for a post's content string </p>
	 *  
	 *  @return this.content : Returns the actual content string of this post. This is the main info of the post
	 * 
	*/
    public String getContent() {return this.content;}
    
    /*******
	 * <p> Method: void setBody(String newContent) </p>
	 * 
	 * <p> Description: Sets the content of a post to new content. This is used for deleting and editing posts </p>
	 *  
	 *  @param newContent : The new content string to set the post's content to
	 * 
	*/
    public void setContent(String newContent) {this.content = newContent;}
    
    
    /*******
   	 * <p> Method: String getTimestamp() </p>
   	 * 
   	 * <p> Description: Getter for a post's timestamp string. This is when the post of made or last edited </p>
   	 *  
   	 *  @return this.timestamp : Returns the timestamp saved in a post	 
   	 *  
   	*/
    public String getTimestamp() {return this.timestamp;}
    
    /*******
	 * <p> Method: void setTimeStamp(String newTimestamp) </p>
	 * 
	 * <p> Description: Sets the timestamp of a post to a new timestamp. This can happen when a post is edited </p>
	 *  
	 *  @param newTimestamp : The new timestamp string to set the post's timestamp to
	 * 
	*/
    public void setTimeStamp(String newTimestamp) {this.timestamp = newTimestamp;}
    
    
    /*******
	 * <p> Method: boolean getDeleted() </p>
	 * 
	 * <p> Description: Getter for a post's deleted status. This is used to know if a post should be displayed or not. </p>
	 *  
	 *  @return this.isDeleted : returns the deleted status of a post	 
	 *  
	*/
    public boolean getDeleted() {return this.isDeleted;}
    
    /*******
	 * <p> Method: void setDeleted(boolean i) </p>
	 * 
	 * <p> Description: Sets the deleted status of a post. This is used to delete or undelete posts. </p>
	 *  
	 *  @param i : the deleted status of the post
	 * 
	*/
    public void setDeleted(boolean i) {this.isDeleted = i;}
    
    
    /*******
	 * <p> Method: List<reply> getReplies() </p>
	 * 
	 * <p> Description: Getter for a post's reply list. This is used to get the replies needed to be displayed. </p>
	 *  
	 *  @return this.replies : returns the list of replys tied to a post
	 *  
	*/
    public List<reply> getReplies() {return this.replies;}
    
    /*******
	 * <p> Method: void addReply(reply newReply) </p>
	 * 
	 * <p> Description: Adds a reply to a posts reply list. This is done when a reply is first made. </p>
	 *  
	 *  @param newReply : the new reply that is being added to the posts reply list
	 *  
	*/
    public void addReply(reply newReply) {this.replies.add(newReply);}
    
    /*******
	 * <p> Method: removeReply(reply removedReply) </p>
	 * 
	 * <p> Description: Removes a reply to a posts reply list. This is done when a reply is deleted. </p>
	 *  
	 *  @param removedReply : the reply that should be removed for the post's reply list
	 *  
	*/
    public void removeReply(reply removedReply) {this.replies.remove(removedReply);}
    
}