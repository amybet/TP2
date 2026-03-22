package entityClasses;

/*******
 * <p> Title: reply.java</p>
 *
 * <p> Description: This is the class for a reply object. It stores important info for the reply including the reply ID number, the ID for the parent post, the author, title,
 * contents and timestamp of posting the reply. It contains a contrustor for the posts
 * and getters/setters to edit post object info
 * </p>
 *
 *  <p>@author: Alexander Fuentes</p>
 */
public class reply {
    private int replyID;
    private int parentPostID;
    private String author;
    private String content;
    private String timestamp;

    /*******
	 * <p> Method: reply(int id, int parentID, String author, String content, String time) </p>
	 * 
	 * <p> Description: The constructor for replys. It takes an ID value, Strings for the post Author, string for the reply author,
	 *  contents and time of post. </p>
	 *  
	 * @param id : the id value for this reply
	 * 
	 * @param parentID : the id of the post this reply is going to
	 * 
	 * @param author : the author of the reply
	 * 
	 * @param content : the info that the reply contains
	 * 
	 * @param time : the time of the reply's creation/last edited
	 * 
	*/
    public reply(int id, int parentID, String author, String content, String time) {
        this.replyID = id;
        this.parentPostID = parentID;
        this.author = author;
        this.content = content;
        this.timestamp = time;
    }
    

    /*******
	 * <p> Method:int getReplyID() </p>
	 * 
	 * <p> Description: Getter for a reply's ID value </p>
	 *  
	 *  @return this.replyID : Returns the ID value of this reply
	 * 
	*/	
    public int getReplyID() {return this.replyID;}
    
    /*******
	 * <p> Method: void setReplyID(int newID)) </p>
	 * 
	 * <p> Description: Sets the value of a reply's ID to a new ID </p>
	 *  
	 *  @param newID : The new value to set the reply's ID to
	 * 
	*/	
    public void setReplyID(int newID) {this.replyID = newID;}
    
    
    /*******
	 * <p> Method: void getParentPostID() </p>
	 * 
	 * <p> Description: Gets the id value for the parent post </p>
	 *  
	 *  @return this.parentPostID : The value of the parent post
	 * 
	*/
    public int getParentPostID() {return this.parentPostID;}
    
    /*******
	 * <p> Method: void setParentPostID(int newParentID) </p>
	 * 
	 * <p> Description: Sets the parent of a reply to a new post </p>
	 *  
	 *  @param newParentID : The new post id to set this reply to
	 * 
	*/
    public void setParentPostID(int newParentID) {this.parentPostID = newParentID;}
    
    /*******
	 * <p> Method: void String getAuthor() </p>
	 * 
	 * <p> Description: gets the author of the reply </p>
	 *  
	 *  @return this.author : The author of the reply
	 * 
	*/
    public String getAuthor() {return this.author;}
    
    /*******
	 * <p> Method: void setAuthor(String newAuthor) </p>
	 * 
	 * <p> Description: Sets the author of a reply to a new author </p>
	 *  
	 *  @param newAuthor : The new author string to set the post's author to
	 * 
	*/
    public void setAuthor(String newAuthor) {this.author = newAuthor;}
    
    /*******
	 * <p> Method: String getBody() </p>
	 * 
	 * <p> Description: Getter for a reply's content string </p>
	 *  
	 *  @return this.content : Returns the actual content string of this post. This is the main info of the post
	 * 
	*/	
    public String getContent() {return this.content;}
    
    /*******
	 * <p> Method: void setContent(String newContent) </p>
	 * 
	 * <p> Description: Sets the content of a reply to new content. This is used for deleting and editing replys </p>
	 *  
	 *  @param newContent : The new content string to set the reply's content to
	 * 
	*/
    public void setContent(String newContent) {this.content = newContent;}
    
    /*******
	 * <p> Method: String getTimestamp() </p>
	 * 
	 * <p> Description: Getter for a reply's timestamp string. This is when the post of made or last edited </p>
	 *  
	 *  @return this.timestamp : Returns the timestamp saved in a reply	 
	 *  
	*/
    public String getTimestamp() {return this.timestamp;}
    
    /*******
   	 * <p> Method: void setTimeStamp(String newTimestamp) </p>
   	 * 
   	 * <p> Description: Sets the timestamp of a reply to a new timestamp. This can happen when a reply is edited </p>
   	 *  
   	 *  @param newTimestamp : The new timestamp string to set the reply's timestamp to
   	 * 
   	*/
    public void setTimeStamp(String newTimestamp) {this.timestamp = newTimestamp;}
}