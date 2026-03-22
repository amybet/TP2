package entityClasses;

/**********
 * <p> Class: DiscussionInputRecognizer </p>
 * 
 * <p> Description: This class provides static methods to validate user input for discussion
 * functionality (posts, replies, usernames) before sending it to the database. Each method
 * returns an empty string if the input is valid, or an error message describing why it is
 * invalid.</p>
 * 
 */
public class DiscussionInputRecognizer {
    /**********
     * <p> Method: checkPostTitle </p>
     * 
     * <p> Description: Validates a post title. Checks minimum (3) and maximum (100) length.
     * Returns empty string if valid, otherwise an error message.</p>
     * 
     * @param input the post title to validate
     * @return empty string if valid, or an error message
     */
    public static String checkPostTitle(String input) {
        if (input == null || input.isBlank()) {
            return "Post title cannot be empty.\n";
        }
        if (input.length() < 3) {
            return "Post title must be at least 3 characters.\n";
        }
        if (input.length() > 32) {
            return "Post title cannot exceed 100 characters.\n";
        }
        return ""; // Valid
    }

    /**********
     * <p> Method: checkPostContent </p>
     * 
     * <p> Description: Validates post content. Checks minimum (5) and maximum (5000) length.
     * Returns empty string if valid, otherwise an error message.</p>
     * 
     * @param input the post content to validate
     * @return empty string if valid, or an error message
     */
    public static String checkPostContent(String input) {
        if (input == null || input.isBlank()) {
            return "Post content cannot be empty.\n";
        }
        if (input.length() < 5) {
            return "Post content must be at least 5 characters.\n";
        }
        if (input.length() > 500) {
            return "Post content cannot exceed 500 characters.\n";
        }
        return ""; // Valid
    }

    /**********
     * <p> Method: checkReplyContent </p>
     * 
     * <p> Description: Validates reply content. Checks minimum (1) and maximum (2000) length.
     * Returns empty string if valid, otherwise an error message.</p>
     * 
     * @param input the reply content to validate
     * @return empty string if valid, or an error message
     */
    public static String checkReplyContent(String input) {
        if (input == null || input.isBlank()) {
            return "Reply content cannot be empty.\n";
        }
        if (input.length() < 1) {
            return "Reply content must be at least 1 character.\n";
        }
        if (input.length() > 500) {
            return "Reply content cannot exceed 500 characters.\n";
        }
        return ""; // Valid
    }

}