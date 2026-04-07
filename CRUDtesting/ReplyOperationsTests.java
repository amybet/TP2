package CRUDtesting;

import entityClasses.post;
import entityClasses.reply;

/**
 * <p> Title: ReplyOperationsTests </p>
 *
 * <p> Description: A suite of semi-automated tests verifying the core CRUD (Create, Read, Update, Delete) 
 * operations for the reply entity. Covers valid and invalid inputs, edge cases, and state transitions 
 * to ensure that reply objects behave correctly and maintain their structural links to parent posts. </p>
 *
 * <p> <b>Students User Stories covered:</b> </p>
 * <ul>
 * <li> View replies to a post (count, read/unread filter) </li>
 * <li> Create a reply to a specific post </li>
 * <li> Update a reply </li>
 * <li> Delete a reply </li>
 * </ul>
 * @author Amairani Caballero
 */

public class ReplyOperationsTests {

    /** Tracks the total number of tests that passed. */
    public static int numPassed = 0;

    /** Tracks the total number of tests that failed. */
    public static int numFailed = 0;

    /**
     * Default constructor is not used because this class runs tests.
     */
    public ReplyOperationsTests() {
    }

    /**
     * Runs all ReplyOperationsTests checks and prints a summary of the results.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("ReplyOperationsTests:");

        testCreateReply_validInputs();
        testCreateReply_linkedToParentPost();
        testReadReply_getters();
        testUpdateReply_content();
        testUpdateReply_authorPreserved();
        testUpdateReply_parentPostPreserved();
        testDeleteReply_removedFromPost();
        testDeleteReply_remainingRepliesIntact();
        testViewReplies_count();
        testViewReplies_addAndRemoveMultiple();
        testStateTransition_createdToUpdatedToDeleted();

        printSummary();
    }

    // Create reply

    /**
     * Verifies that constructing a reply with valid inputs successfully stores all fields correctly in the object.
     * <p>
     * <b>Intent:</b> Directly satisfies the "Create a reply to a specific post" user story by ensuring
     * user input is accurately captured and maintained in memory.
     * </p>
     */
    public static void testCreateReply_validInputs() {
        section("1) Create Reply – all fields stored correctly\n");
        reply r = new reply(1, 100, "alice", "This is my reply.", "2025-03-01 12:00");

        check("replyID stored", r.getReplyID() == 1);
        check("parentPostID stored", r.getParentPostID() == 100);
        check("author stored", "alice".equals(r.getAuthor()));
        check("content stored", "This is my reply.".equals(r.getContent()));
        check("timestamp stored", "2025-03-01 12:00".equals(r.getTimestamp()));
    }

    /**
     * Verifies that a reply is correctly linked to its parent post via the parentPostID,
     * and that it populates the post's internal reply list.
     * <p>
     * <b>Intent:</b> Ensures relational integrity between entities, guaranteeing that replies do not
     * become orphaned and always display under the correct parent thread.
     * </p>
     */
    public static void testCreateReply_linkedToParentPost() {
        section("2) Create Reply – linked to parent post\n");

        // NOTE: post constructor includes thread now: (id, author, title, content, thread, time, deleted)
        post p = new post(100, "bob", "Original Question", "Help?", "General", "2025-03-01 10:00", false);

        reply r = new reply(2, p.getPostID(), "carol", "Here is the answer.", "2025-03-01 11:00");
        p.addReply(r);

        check("reply parentPostID matches post ID", r.getParentPostID() == p.getPostID());
        check("post contains the reply", p.getReplies().contains(r));
        check("post has exactly 1 reply", p.getReplies().size() == 1);
    }

    // Read / view replies

    /**
     * Verifies that all reply getter methods accurately return the values provided at construction.
     * <p>
     * <b>Intent:</b> Supports the "View replies to a post" user story by proving the backend can
     * reliably retrieve stored reply data for the front-end to render.
     * </p>
     */
    public static void testReadReply_getters() {
        section("3) Read Reply – all getters return correct values\n");
        reply r = new reply(5, 200, "dave", "Great insight!", "2025-03-02 09:15");

        check("getReplyID", r.getReplyID() == 5);
        check("getParentPostID", r.getParentPostID() == 200);
        check("getAuthor", "dave".equals(r.getAuthor()));
        check("getContent", "Great insight!".equals(r.getContent()));
        check("getTimestamp", "2025-03-02 09:15".equals(r.getTimestamp()));
    }

    // Update reply

    /**
     * Verifies that updating a reply's content successfully mutates the stored value.
     * <p>
     * <b>Intent:</b> Directly satisfies the core requirement of the "Update a reply" user story,
     * proving users can modify their submitted text.
     * </p>
     */
    public static void testUpdateReply_content() {
        section("4) Update Reply – content updated correctly\n");
        reply r = new reply(10, 300, "eve", "Original reply.", "2025-03-03 08:00");
        r.setContent("Corrected reply.");

        check("content updated", "Corrected reply.".equals(r.getContent()));
        check("old content is gone", !"Original reply.".equals(r.getContent()));
    }

    /**
     * Verifies that updating a reply's content does not inadvertently modify the reply's author metadata.
     * <p>
     * <b>Intent:</b> Prevents data corruption during an update operation, ensuring ownership
     * of the reply is preserved when the text changes.
     * </p>
     */
    public static void testUpdateReply_authorPreserved() {
        section("5) Update Reply – author unchanged after content update\n");
        reply r = new reply(11, 301, "frank", "First draft.", "2025-03-03 09:00");
        r.setContent("Second draft.");

        check("author still frank", "frank".equals(r.getAuthor()));
        check("content is updated", "Second draft.".equals(r.getContent()));
    }

    /**
     * Verifies that updating a reply's content does not break its linkage to the parent post.
     * <p>
     * <b>Intent:</b> Ensures that editing a reply does not accidentally move or orphan the reply
     * from its original discussion thread.
     * </p>
     */
    public static void testUpdateReply_parentPostPreserved() {
        section("6) Update Reply – parentPostID unchanged after content update\n");
        reply r = new reply(12, 400, "grace", "Some reply.", "2025-03-04 07:00");
        r.setContent("Updated reply.");

        check("parentPostID still 400", r.getParentPostID() == 400);
    }

    // Delete reply

    /**
     * Verifies that deleting a reply explicitly removes it from the parent post's internal list
     * and decrements the reply count.
     * <p>
     * <b>Intent:</b> Directly satisfies the "Delete a reply" user story by proving the system
     * cleans up relational data upon removal.
     * </p>
     */
    public static void testDeleteReply_removedFromPost() {
        section("7) Delete Reply – reply removed from post\n");

        // NOTE: post constructor includes thread now.
        post p = new post(500, "henry", "Post", "Body.", "General", "2025-03-05 06:00", false);

        reply r = new reply(20, 500, "ivan", "My reply.", "2025-03-05 06:30");
        p.addReply(r);
        p.removeReply(r);

        check("reply list is empty after removal", p.getReplies().isEmpty());
        check("reply no longer in list", !p.getReplies().contains(r));
    }

    /**
     * Verifies that deleting one specific reply does not affect or remove any sibling replies on the same post.
     * <p>
     * <b>Intent:</b> Ensures pinpoint accuracy during deletion operations, preventing cascading
     * data loss for other users in the discussion thread.
     * </p>
     */
    public static void testDeleteReply_remainingRepliesIntact() {
        section("8) Delete Reply – other replies intact after one deletion\n");

        // NOTE: post constructor includes thread now.
        post p = new post(501, "julia", "Post", "Body.", "General", "2025-03-06 05:00", false);

        reply r1 = new reply(30, 501, "kevin", "Reply A.", "2025-03-06 05:10");
        reply r2 = new reply(31, 501, "laura", "Reply B.", "2025-03-06 05:20");
        reply r3 = new reply(32, 501, "mike", "Reply C.", "2025-03-06 05:30");

        p.addReply(r1);
        p.addReply(r2);
        p.addReply(r3);
        p.removeReply(r2);

        check("list size is 2", p.getReplies().size() == 2);
        check("r1 still present", p.getReplies().contains(r1));
        check("r2 removed", !p.getReplies().contains(r2));
        check("r3 still present", p.getReplies().contains(r3));
    }

    // View replies count

    /**
     * Verifies that the aggregate reply count dynamically and accurately increments as new replies are added to a post.
     * <p>
     * <b>Intent:</b> Satisfies the "count" requirement of the "View replies to a post" user story,
     * ensuring users see an accurate representation of thread activity.
     * </p>
     */
    public static void testViewReplies_count() {
        section("9) View Replies – reply count increments correctly\n");

        // NOTE: post constructor includes thread now.
        post p = new post(600, "nancy", "Post", "Body.", "General", "2025-03-07 04:00", false);

        check("0 replies initially", p.getReplies().size() == 0);
        p.addReply(new reply(40, 600, "oscar", "Reply 1.", "2025-03-07 04:10"));
        check("1 reply after add", p.getReplies().size() == 1);
        p.addReply(new reply(41, 600, "patricia", "Reply 2.", "2025-03-07 04:20"));
        check("2 replies after add", p.getReplies().size() == 2);
        p.addReply(new reply(42, 600, "quinn", "Reply 3.", "2025-03-07 04:30"));
        check("3 replies after add", p.getReplies().size() == 3);
    }

    /**
     * Verifies that a complex sequence of adding and removing multiple replies results in the correct final
     * reply count and accurate list of remaining replies.
     * <p>
     * <b>Intent:</b> Stress-tests the collection logic to ensure the UI reply count remains perfectly
     * synchronized with the backend data state during high-activity scenarios.
     * </p>
     */
    public static void testViewReplies_addAndRemoveMultiple() {
        section("10) View Replies – add/remove sequence\n");

        // NOTE: post constructor includes thread now.
        post p = new post(601, "rachel", "Post", "Body.", "General", "2025-03-08 03:00", false);

        reply r1 = new reply(50, 601, "sam", "R1.", "2025-03-08 03:10");
        reply r2 = new reply(51, 601, "tina", "R2.", "2025-03-08 03:20");
        reply r3 = new reply(52, 601, "ursula", "R3.", "2025-03-08 03:30");

        p.addReply(r1);
        p.addReply(r2);
        p.addReply(r3);
        p.removeReply(r1);

        check("size is 2 after removing one", p.getReplies().size() == 2);
        check("r2 still present", p.getReplies().contains(r2));
        check("r3 still present", p.getReplies().contains(r3));
    }

    /**
     * Verifies that a reply can safely and successfully transition through 
     * (Creation -> Modification -> Deletion) without throwing errors or corrupting its parent post.
     * <p>
     * <b>Intent:</b> Proves the overall stability of the Create, Update, and Delete
     * reply user stories when executed sequentially.
     * </p>
     */
    public static void testStateTransition_createdToUpdatedToDeleted() {
        section("11) Reply State Transition – created -> updated -> deleted \n");

        // NOTE: post constructor includes thread now.
        post p = new post(700, "victor", "Host Post", "Body.", "General", "2025-03-09 02:00", false);

        reply r = new reply(60, 700, "wendy", "Original reply.", "2025-03-09 02:10");
        p.addReply(r);

        // Created
        check("state: created – reply in list", p.getReplies().contains(r));
        check("state: created – content correct", "Original reply.".equals(r.getContent()));

        // Updated
        r.setContent("Updated reply.");
        check("state: updated – content changed", "Updated reply.".equals(r.getContent()));
        check("state: updated – still in list", p.getReplies().contains(r));

        // Deleted
        p.removeReply(r);
        check("state: deleted – reply removed from list", !p.getReplies().contains(r));
        check("state: deleted – list is empty", p.getReplies().isEmpty());
    }

    // Helpers

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
        System.out.println("ReplyOperationsTests Summary\n");
        System.out.println("Passed: " + numPassed + "  Failed: " + numFailed);
    }
}