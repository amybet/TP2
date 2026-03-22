package CRUDtesting;

import entityClasses.post;
import entityClasses.reply;

/**
 * <p> Title: PostDeletionTests </p>
 *
 * <p> Description: A suite of semi-automated tests verifying the correct behavior of the soft-deletion 
 * system. These tests prove that removing a post hides it from viewers while preserving database 
 * integrity and relational data (replies). </p>
 *
 * <p> <b>Students User Stories covered:</b> </p>
 * <ul>
 * <li> Delete a post with confirmation; keeps replies and shows deletion marker. </li>
 * <li> Delete a reply with confirmation. </li>
 * </ul>
 * @author Amairani Caballero
 */


public class PostDeletionTests {
	
	/**
	 * Default constructor is not used because this class runs tests.
	 */
	public PostDeletionTests() {
	}
	
	/** Tracks the total number of tests that passed. */
    public static int numPassed = 0;
    
    /** Tracks the total number of tests that failed. */
    public static int numFailed = 0;

    /** The standard deletion marker displayed to viewers when a post is soft-deleted. */
    public static final String DELETED_MARKER = "(this post has been deleted)";

    /**
     * Runs all PostDeletionTests checks and prints a summary of the results.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("PostDeletionTests:\n");

        testSoftDelete_flagSetTrue();
        testSoftDelete_titleReplacedWithMarker();
        testSoftDelete_contentReplacedWithMarker();
        testSoftDelete_authorPreserved();
        testSoftDelete_postIdPreserved();
        testSoftDelete_noRepliesLost();
        testSoftDelete_repliesAccessible();
        testSoftDelete_replyContentUnchanged();
        testDeletedPost_markerVisibleToViewer();
        testDeletedPost_multipleRepliesAllPreserved();
        testReplyDeletion_removedFromList();
        testReplyDeletion_postUnaffected();
        testReplyDeletion_otherRepliesUnaffected();
        testDoubleSoftDelete();

        printSummary();
    }

    
    /**
     * Verifies that a post is correctly soft-deleted by setting its 'isDeleted' flag to true.
     * <p>
     * <b>Intent:</b> Ensures the post remains in memory to preserve its relational data (like replies), 
     * satisfying the core requirement of the soft-deletion user story.
     * </p>
     */
    public static void testSoftDelete_flagSetTrue() {
        section("1) Soft Delete - isDeleted flag becomes true\n");
        post p = buildPost(1, "Alice");
        softDelete(p);
        check("isDeleted is true", p.getDeleted());
    }

    /**
     * Verifies that the post title is replaced with a standard deletion marker after a soft delete.
     * <p>
     * <b>Intent:</b> Ensures that viewers do not see the original title of a deleted post, 
     * satisfying the requirement to display a deletion marker.
     * </p>
     */
    public static void testSoftDelete_titleReplacedWithMarker() {
        section("2) Soft Delete – title replaced with deletion marker\n");
        post p = buildPost(2, "Bob");
        String originalTitle = p.getTitle();
        softDelete(p);
        check("post title is not the original title", !originalTitle.equals(p.getTitle()));
        check("post title is the deletion marker",     DELETED_MARKER.equals(p.getTitle()));
    }
    
    /**
     * Verifies that the post content is replaced with a standard deletion marker after a soft delete.
     * <p>
     * <b>Intent:</b> Ensures the original content is hidden from viewers, directly supporting 
     * the user story requirement to mask deleted information.
     * </p>
     */
    public static void testSoftDelete_contentReplacedWithMarker() {
        section("3) Soft Delete – content replaced with deletion marker\n");
        post p = buildPost(3, "Carol");
        String originalContent = p.getContent();
        softDelete(p);
        check("post content is not the original content", !originalContent.equals(p.getContent()));
        check("post content is the deletion marker",     DELETED_MARKER.equals(p.getContent()));
    }

    /**
     * Verifies that the post's author is preserved and not corrupted after a soft delete.
     * <p>
     * <b>Intent:</b> Proves that the deletion process only alters the title and content, 
     * maintaining the integrity of the original author's metadata.
     * </p>
     */
    public static void testSoftDelete_authorPreserved() {
        section("4) Soft Delete – author is preserved after soft delete\n");
        post p = buildPost(4, "John");
        softDelete(p);
        check("author is still 'John'", "John".equals(p.getAuthor()));
    }

    /**
     * Verifies that the post keeps its original ID after a soft delete.
     * <p>
     * <b>Intent:</b> Confirms that the post's unique identifier remains intact, which is 
     * crucial for keeping all existing replies successfully linked to the post.
     * </p>
     */
    public static void testSoftDelete_postIdPreserved() {
        section("5) Soft Delete – post ID is preserved after soft delete\n");
        post p = buildPost(5, "Amy");
        softDelete(p);
        check("postID is still 5", p.getPostID() == 5);
    }

    /**
     * Verifies that the total number of replies attached to a post remains unchanged after the post is soft-deleted.
     * <p>
     * <b>Intent:</b> Directly satisfies the user story requirement stating that deleting a post 
     * "keeps replies" and does not wipe out associated community data.
     * </p>
     */
    public static void testSoftDelete_noRepliesLost() {
        section("6) Soft Delete – reply count unchanged after soft delete\n");
        post p = buildPost(6, "Frank");
        p.addReply(new reply(1, 6, "Luis", "Reply 1.", "t1"));
        p.addReply(new reply(2, 6, "Jimmy", "Reply 2.", "t2"));
        p.addReply(new reply(3, 6, "Ivan",  "Reply 3.", "t3"));
        int countBefore = p.getReplies().size();
        softDelete(p);
        check("reply count unchanged after soft delete", p.getReplies().size() == countBefore);
    }

    /**
     * Verifies that individual replies remain accessible from the post's reply list after a soft delete.
     * <p>
     * <b>Intent:</b> Ensures the system can still retrieve and display replies for a deleted post, 
     * supporting the requirement that all replies remain accessible to users.
     * </p>
     */
    public static void testSoftDelete_repliesAccessible() {
        section("7) Soft Delete – replies are still accessible after soft delete\n");
        post p = buildPost(7, "Julia");
        reply r = new reply(4, 7, "Kevin", "Still visible.", "t4");
        p.addReply(r);
        softDelete(p);
        check("reply list is not empty",  !p.getReplies().isEmpty());
        check("specific reply found",  p.getReplies().contains(r));
    }

    /**
     * Verifies that the content and author of a reply are completely unaltered when its parent post is deleted.
     * <p>
     * <b>Intent:</b> Proves that the post's soft-deletion logic does not cascade and improperly 
     * overwrite the text or ownership of the community replies.
     * </p>
     */
    public static void testSoftDelete_replyContentUnchanged() {
        section("8) Soft Delete – reply content are not altered by post deletion\n");
        post p = buildPost(8, "Laura");
        reply r = new reply(5, 8, "Mike", "Original reply content.", "t5");
        p.addReply(r);
        softDelete(p);
        check("reply content unchanged", "Original reply content.".equals(r.getContent()));
        check("reply author unchanged",  "Mike".equals(r.getAuthor()));
    }

    /**
     * Verifies that the post displays the correct deletion marker to a simulated viewer.
     * <p>
     * <b>Intent:</b> Simulates the viewer experience to guarantee the UI will show the deletion marker 
     * instead of the original text, fulfilling the viewer visibility requirement.
     * </p>
     */
    public static void testDeletedPost_markerVisibleToViewer() {
        section("9) Deleted Post – viewer sees deletion marker, not the original title/content\n");
        post p = buildPost(9, "Nancy");
        softDelete(p);

        String displayedTitle   = p.getTitle();
        String displayedContent = p.getContent();

        check("viewer sees the deleted title marker",   DELETED_MARKER.equals(displayedTitle));
        check("viewer sees the deleted content marker", DELETED_MARKER.equals(displayedContent));
        check("isDeleted flag true for viewer",     p.getDeleted());
    }

    /**
     * Verifies that a post with a large number of replies preserves all of them upon deletion.
     * <p>
     * <b>Intent:</b> Stress-tests the requirement that "all replies attached to the deleted post 
     * are still accessible," ensuring no data loss occurs when multiple replies exist.
     * </p>
     */
    public static void testDeletedPost_multipleRepliesAllPreserved() {
        section("10) Deleted Post – all 5 replies are preserved\n");
        post p = buildPost(10, "Oscar");
        for (int i = 1; i <= 5; i++) {
            p.addReply(new reply(10 + i, 10, "user" + i, "Reply " + i, "t" + i));
        }
        softDelete(p);
        check("all 5 replies preserved", p.getReplies().size() == 5);
    }

    /**
     * Verifies that removing a reply successfully deletes it from the parent post's internal list.
     * <p>
     * <b>Intent:</b> Satisfies the user story "Delete a reply with confirmation" by ensuring 
     * the backend properly unlinks the reply from the post.
     * </p>
     */
    public static void testReplyDeletion_removedFromList() {
        section("11) Reply Deletion – reply removed from post's list\n");
        post p = buildPost(11, "Patricia");
        reply r = new reply(20, 11, "Harley", "A reply.", "t20");
        p.addReply(r);
        p.removeReply(r);
        check("reply list is empty", p.getReplies().isEmpty());
        check("reply not in list",   !p.getReplies().contains(r));
    }

    /**
     * Verifies that deleting a reply does not accidentally delete or modify the parent post.
     * <p>
     * <b>Intent:</b> Proves the isolation of the reply deletion logic, ensuring the post itself 
     * and its content remain completely unaffected.
     * </p>
     */
    public static void testReplyDeletion_postUnaffected() {
        section("12) Reply Deletion – parent post not modified by reply deletion\n ");
        post p = buildPost(12, "Rachel");
        reply r = new reply(21, 12, "Sam", "A reply.", "t21");
        p.addReply(r);
        p.removeReply(r);
        check("post title unchanged",   "Test Title".equals(p.getTitle()));
        check("post content unchanged", "Test content.".equals(p.getContent()));
        check("post not deleted",       !p.getDeleted());
    }

    /**
     * Verifies that deleting one specific reply does not alter or remove any sibling replies.
     * <p>
     * <b>Intent:</b> Confirms that removing a reply is a targeted action, satisfying the requirement 
     * that "all other replies are unaffected."
     * </p>
     */
    public static void testReplyDeletion_otherRepliesUnaffected() {
        section("13) Reply Deletion – other replies unaffected\n");
        post p = buildPost(13, "tina");
        reply r1 = new reply(22, 13, "Karla", "Reply A.", "t22");
        reply r2 = new reply(23, 13, "Victor", "Reply B.", "t23");
        reply r3 = new reply(24, 13, "Wendy",  "Reply C.", "t24");
        p.addReply(r1);
        p.addReply(r2);
        p.addReply(r3);
        p.removeReply(r2);
        check("r1 still present", p.getReplies().contains(r1));
        check("r2 removed",       !p.getReplies().contains(r2));
        check("r3 still present", p.getReplies().contains(r3));
        check("list size is 2",   p.getReplies().size() == 2);
    }

    /**
     * Verifies that triggering the soft delete process twice on the same post does not cause errors or data loss.
     * <p>
     * <b>Intent:</b> Handles edge cases where a user might double-click or submit a delete request twice, 
     * ensuring the system remains stable and replies are still preserved.
     * </p>
     */
    public static void testDoubleSoftDelete() {
        section("14) Double Soft Delete – delete works properly if triggered twice\n");
        post p = buildPost(14, "Xavier");
        reply r = new reply(25, 14, "Jasmine", "Reply text.", "t25");
        p.addReply(r);
        softDelete(p);
        softDelete(p); // second delete should not change anything
        check("still deleted",             p.getDeleted());
        check("title still marker",        DELETED_MARKER.equals(p.getTitle()));
        check("content still marker",      DELETED_MARKER.equals(p.getContent()));
        check("reply still preserved",     p.getReplies().size() == 1);
    }

    /**
     * Builds a non-deleted post with fixed title, content, and timestamp for testing purposes. 
     * @param id The ID to assign to the test post.
     * @param author The author name to assign to the test post.
     * @return A constructed post object ready for testing.
     */
    public static post buildPost(int id, String author) {
        return new post(id, author, "Test Title", "Test content.", "2025-01-01 00:00", false);
    }

    /**
     * Performs a soft delete operation by setting the isDeleted flag to true and replacing the title and content. 
     * @param p The post object to be soft-deleted.
     */
    public static void softDelete(post p) {
        p.setDeleted(true);
        p.setTitle(DELETED_MARKER);
        p.setContent(DELETED_MARKER);
    }

    /**
     * Prints a formatted section header to the console for test readability.
     * @param name The name of the test section being executed.
     */
    public static void section(String name) {
    	System.out.println("------------------------------------------------------------");
        System.out.println("  " + name);
    }

    /**
     * Evaluates a boolean condition and prints whether the corresponding test passed or failed, 
     * while updating the global pass/fail counters.
     * @param description A brief description of the condition being checked.
     * @param passed The boolean result of the test condition.
     */
    public static void check(String description, boolean passed) {
        if (passed) {
            System.out.println("Test Status: [PASS] - " + description);
            numPassed++;
        } else {
            System.out.println("Test Status: [FAIL] - " + description);
            numFailed++;
        }
    }

    /**
     * Prints the final summary of all executed tests, including total passes and failures.
     */
    public static void printSummary() {
    	System.out.println("------------------------------------------------------------");
        System.out.println("PostDeletionTests Summary\n");
        System.out.println("Passed: " + numPassed + "  Failed: " + numFailed);
    }
}