package CRUDtesting;

import entityClasses.post;
import entityClasses.reply;

/**
 * <p> Title: PostOperationsTests </p>
 *
 * <p> Description: A suite of semi-automated tests verifying the core CRUD (Create, Read, Update, Delete) 
 * operations for the post entity. Covers valid inputs, edge cases, and state transitions 
 * (created -> updated -> deleted) to ensure database integrity. </p>
 *
 * <p> <b>Students User Stories covered:</b> </p>
 * <ul>
 * <li> Create a post with title and content </li>
 * <li> Update a post </li>
 * <li> Delete a post with soft-delete and reply preservation </li>
 * <li> View a list of posts </li>
 * <li> View my posts </li>
 * </ul>
 * @author Amairani Caballero
 */
public class PostOperationsTests {

    /**
     * Default constructor is not used because this class runs tests.
     */
    public PostOperationsTests() {
    }

    /** Tracks the total number of tests that passed. */
    public static int numPassed = 0;

    /** Tracks the total number of tests that failed. */
    public static int numFailed = 0;

    /** The standard deletion marker displayed to viewers when a post is soft-deleted. */
    public static final String DELETED_MARKER = "(this post has been deleted)";

    /**
     * Runs all PostOperationsTests checks and prints a summary of the results.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("PostOperationsTests:");

        testCreatePost_validInputs();
        testCreatePost_deletedFlagSetsMarkers();
        testReadPost_getters();
        testUpdatePost_titleAndContent();
        testUpdatePost_authorUnchangedAfterTitleUpdate();
        testDeletePost_softDelete();
        testDeletePost_repliesPreservedAfterSoftDelete();
        testDeletePost_multipleRepliesPreserved();
        testStateTransition_createdToUpdatedToDeleted();
        testPostList_filterByAuthor();

        printSummary();
    }

    // Create post

    /**
     * Verifies that all fields are stored correctly within the object immediately after construction.
     * <p>
     * <b>Intent:</b> Supports the create-post user story by proving the system captures and stores user
     * input upon initialization, including the thread label.
     * </p>
     */
    public static void testCreatePost_validInputs() {
        section("1) Create Post – valid inputs stored correctly\n");

        post p = new post(1, "Alice", "Help with HW2", "Content text here.", "General", "2025-01-01 10:00", false);

        check("postID stored", p.getPostID() == 1);
        check("author stored", "Alice".equals(p.getAuthor()));
        check("title stored", "Help with HW2".equals(p.getTitle()));
        check("content stored", "Content text here.".equals(p.getContent()));
        check("thread stored", "General".equals(p.getThread()));
        check("timestamp stored", "2025-01-01 10:00".equals(p.getTimestamp()));
        check("not deleted", !p.getDeleted());
        check("no replies yet", p.getReplies().isEmpty());
    }

    /**
     * Verifies that a post instantiated with the deleted flag set to true automatically masks its title and content.
     * <p>
     * <b>Intent:</b> Ensures edge cases during creation still respect the secure deletion state, preventing
     * leaked information while preserving metadata like author and thread.
     * </p>
     */
    public static void testCreatePost_deletedFlagSetsMarkers() {
        section("2) Create Post – pre-deleted post sets deletion markers\n");

        post p = new post(2, "Bob", "Some Title", "Some content.", "General", "2025-01-02 11:00", true);

        check("isDeleted true", p.getDeleted());
        check("title is deletion marker", DELETED_MARKER.equals(p.getTitle()));
        check("content is deletion marker", DELETED_MARKER.equals(p.getContent()));
        check("author preserved", "Bob".equals(p.getAuthor()));
        check("thread preserved", "General".equals(p.getThread()));
    }

    // Read/view posts tests

    /**
     * Verifies that all getter methods return the exact data stored in the post object.
     * <p>
     * <b>Intent:</b> Supports post list and “view my posts” user stories by guaranteeing the backend can
     * retrieve stored data for the front-end to display.
     * </p>
     */
    public static void testReadPost_getters() {
        section("3) Read Post – getters return correct values\n");

        post p = new post(5, "Carol", "My Question", "Detailed content.", "Homework Help", "2025-03-01 08:30", false);

        check("getPostID", p.getPostID() == 5);
        check("getAuthor", "Carol".equals(p.getAuthor()));
        check("getTitle", "My Question".equals(p.getTitle()));
        check("getContent", "Detailed content.".equals(p.getContent()));
        check("getThread", "Homework Help".equals(p.getThread()));
        check("getTimestamp", "2025-03-01 08:30".equals(p.getTimestamp()));
        check("getDeleted", !p.getDeleted());
    }

    // Update post tests

    /**
     * Verifies that the title and content of a post can be updated independently.
     * <p>
     * <b>Intent:</b> Directly supports the “Update a post” user story by proving mutation methods work
     * without corrupting the object's surrounding data (author/thread/timestamp/deleted state).
     * </p>
     */
    public static void testUpdatePost_titleAndContent() {
        section("4) Update Post – title and content updated independently\n");

        post p = new post(10, "Dave", "Old Title", "Old content.", "General", "2025-01-10 09:00", false);

        p.setTitle("New Title");
        p.setContent("New content.");

        check("title updated", "New Title".equals(p.getTitle()));
        check("content updated", "New content.".equals(p.getContent()));
        check("author unchanged", "Dave".equals(p.getAuthor()));
        check("thread unchanged", "General".equals(p.getThread()));
        check("timestamp unchanged", "2025-01-10 09:00".equals(p.getTimestamp()));
        check("deleted flag unchanged", !p.getDeleted());
    }

    /**
     * Verifies that updating a post's title multiple times leaves the original author field intact.
     * <p>
     * <b>Intent:</b> Ensures data integrity during edits, preventing updates from accidentally reassigning
     * ownership of the post.
     * </p>
     */
    public static void testUpdatePost_authorUnchangedAfterTitleUpdate() {
        section("5) Update Post – author field preserved after title update\n");

        post p = new post(12, "Eve", "Draft Title", "Some content.", "General", "2025-01-12 11:00", false);

        String authorBefore = p.getAuthor();

        p.setTitle("Final Title");
        p.setTitle("Really Final Title");

        check("author same after two title updates", authorBefore.equals(p.getAuthor()));
        check("latest title stored", "Really Final Title".equals(p.getTitle()));
    }

    // Delete post tests

    /**
     * Verifies that soft-deleting a post sets the deleted flag and applies deletion markers.
     * <p>
     * <b>Intent:</b> Proves the core mechanics of soft-delete operate correctly, masking the post from
     * public view while preserving metadata.
     * </p>
     */
    public static void testDeletePost_softDelete() {
        section("6) Delete Post – soft delete sets isDeleted flag\n");

        post p = new post(20, "Frank", "Question", "Need help.", "General", "2025-01-20 07:00", false);

        p.setDeleted(true);
        p.setTitle(DELETED_MARKER);
        p.setContent(DELETED_MARKER);

        check("isDeleted true", p.getDeleted());
        check("title shows deletion msg", DELETED_MARKER.equals(p.getTitle()));
        check("content shows deletion msg", DELETED_MARKER.equals(p.getContent()));
        check("author preserved", "Frank".equals(p.getAuthor()));
        check("thread preserved", "General".equals(p.getThread()));
    }

    /**
     * Verifies that a reply attached to a post remains accessible after the post is soft-deleted.
     * <p>
     * <b>Intent:</b> Satisfies reply preservation requirements by ensuring community discussion data
     * is not erased when a post is deleted.
     * </p>
     */
    public static void testDeletePost_repliesPreservedAfterSoftDelete() {
        section("7) Delete Post – one reply preserved after soft delete\n");

        post p = new post(21, "Grace", "Post With Reply", "Content.", "General", "2025-01-21 06:00", false);
        reply r = new reply(1, 21, "Henry", "Reply text.", "2025-01-21 06:30");
        p.addReply(r);

        p.setDeleted(true);
        p.setTitle(DELETED_MARKER);
        p.setContent(DELETED_MARKER);

        check("reply count still 1", p.getReplies().size() == 1);
        check("reply still accessible", p.getReplies().contains(r));
    }

    /**
     * Verifies that multiple replies attached to a post all remain intact after the post is soft-deleted.
     * <p>
     * <b>Intent:</b> Stress-tests reply preservation to ensure no data loss occurs when soft-deleting
     * highly active threads.
     * </p>
     */
    public static void testDeletePost_multipleRepliesPreserved() {
        section("8) Delete Post – multiple replies preserved after soft delete\n");

        post p = new post(22, "Ivan", "Multi-reply Post", "Content.", "General", "2025-01-22 05:00", false);
        reply r1 = new reply(2, 22, "Julia", "Reply 1.", "2025-01-22 05:10");
        reply r2 = new reply(3, 22, "Kevin", "Reply 2.", "2025-01-22 05:20");
        reply r3 = new reply(4, 22, "Laura", "Reply 3.", "2025-01-22 05:30");

        p.addReply(r1);
        p.addReply(r2);
        p.addReply(r3);

        p.setDeleted(true);
        p.setTitle(DELETED_MARKER);
        p.setContent(DELETED_MARKER);

        check("all 3 replies preserved", p.getReplies().size() == 3);
    }

    // Verifies state transitions: created -> updated -> deleted

    /**
     * Verifies that a post can transition through its lifecycle: created -> updated -> soft-deleted.
     * <p>
     * <b>Intent:</b> Demonstrates overall stability when create, update, and delete behaviors are chained
     * together in a realistic user flow, preserving required metadata and applying deletion markers.
     * </p>
     */
    public static void testStateTransition_createdToUpdatedToDeleted() {
        section("9) State Transition – created -> updated -> soft-deleted\n");

        // Create
        post p = new post(30, "Mike", "Initial Title", "Initial content.", "General", "2025-02-01 10:00", false);
        check("state: created – not deleted", !p.getDeleted());
        check("state: created – title correct", "Initial Title".equals(p.getTitle()));
        check("state: created – thread correct", "General".equals(p.getThread()));

        // Update
        p.setTitle("Revised Title");
        p.setContent("Revised content.");
        check("state: updated – title changed", "Revised Title".equals(p.getTitle()));
        check("state: updated – content changed", "Revised content.".equals(p.getContent()));
        check("state: updated – not yet deleted", !p.getDeleted());

        // Delete
        p.setDeleted(true);
        p.setTitle(DELETED_MARKER);
        p.setContent(DELETED_MARKER);

        check("state: deleted – flag set", p.getDeleted());
        check("state: deleted – author preserved", "Mike".equals(p.getAuthor()));
        check("state: deleted – thread preserved", "General".equals(p.getThread()));
        check("state: deleted – markers applied", DELETED_MARKER.equals(p.getTitle()) && DELETED_MARKER.equals(p.getContent()));
    }

    // View my posts (filter by author)

    /**
     * Verifies that a collection of posts can be filtered by a specific author's name.
     * <p>
     * <b>Intent:</b> Supports "View my posts" by proving the system can isolate and return only the
     * records belonging to the active user.
     * </p>
     */
    public static void testPostList_filterByAuthor() {
        section("10) Filter Posts by Author – view my posts\n");

        java.util.List<post> allPosts = new java.util.ArrayList<>();
        allPosts.add(new post(40, "Nancy", "Nancy Q1", "Content.", "General", "2025-03-01 10:00", false));
        allPosts.add(new post(41, "Oscar", "Oscar Q1", "Content.", "General", "2025-03-01 11:00", false));
        allPosts.add(new post(42, "Nancy", "Nancy Q2", "Content.", "General", "2025-03-01 12:00", false));
        allPosts.add(new post(43, "Oscar", "Oscar Q2", "Content.", "General", "2025-03-01 13:00", false));

        java.util.List<post> myPosts = new java.util.ArrayList<>();
        for (post p : allPosts) {
            if ("Nancy".equals(p.getAuthor())) {
                myPosts.add(p);
            }
        }

        check("Nancy's post count is 2", myPosts.size() == 2);
        check("both posts are Nancy's", myPosts.stream().allMatch(p -> "Nancy".equals(p.getAuthor())));
    }

    // Helpers

    /**
     * Prints a formatted section header to the console for test readability.
     *
     * @param name The name of the test section being executed.
     */
    public static void section(String name) {
        System.out.println("------------------------------------------------------------");
        System.out.println("  " + name);
    }

    /**
     * Evaluates a boolean condition and prints whether the corresponding test passed or failed,
     * while updating the global pass/fail counters.
     *
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
        System.out.println("PostOperationsTests Summary\n");
        System.out.println("Passed: " + numPassed + "  Failed: " + numFailed);
    }
}