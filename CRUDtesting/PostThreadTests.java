package CRUDtesting;

import entityClasses.post;

/**
 * <p> Title: PostThreadTests </p>
 *
 * <p> Description: A suite of tests verifying the thread behavior for the post function, .
 * it ensures that a post stores the correct thread label. </p>
 *
 * <p> <b>Students User Stories covered:</b> </p>
 * <ul>
 *   <li> Create a post with a title and body so I can ask questions or share statements. </li>
 *   <li> I can post to different threads. </li>
 * </ul>
 *
 *
 * @author Amairani Caballero
 */
public class PostThreadTests {

    /**
     * Default constructor is not used because this class runs tests.
     */
    public PostThreadTests() {
    }

    /** Tracks the total number of tests that passed. */
    public static int numPassed = 0;

    /** Tracks the total number of tests that failed. */
    public static int numFailed = 0;

    /**
     * Runs all PostThreadTests checks and prints a summary of the results.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("PostThreadTests:");

        testCreatePost_threadStored();
        testCreatePost_nonGeneralThreadStored();
        testSetThread_doesNotChangeOtherFields();
        testThreadPreservedAfterSoftDelete();
        printSummary();
    }

    // Thread tests

    /**
     * Verifies that the thread field is stored correctly during post creation.
     * <p>
     * <b>Intent:</b> Confirms that a post can be created under the expected thread label so that
     * it can be displayed in the correct category.
     * </p>
     */
    public static void testCreatePost_threadStored() {
        section("1) Thread – stored correctly on create (General)\n");
        post p = new post(1, "Alice", "Help with HW2", "Content text here.", "General", "2025-01-01 10:00", false);

        check("thread stored as General", "General".equals(p.getThread()));
        check("not deleted", !p.getDeleted());
    }

    /**
     * Verifies that a post can be created under a non-General thread.
     * <p>
     * <b>Intent:</b> Satisfies the requirement that students can post to different threads and
     * that the post object correctly stores the thread label.
     * </p>
     */
    public static void testCreatePost_nonGeneralThreadStored() {
        section("2) Thread – stored correctly on create (Homework Help)\n");
        post p = new post(2, "Bob", "Need help", "I'm stuck on recursion.", "Homework Help", "2025-01-02 11:00", false);

        check("thread stored as Homework Help", "Homework Help".equals(p.getThread()));
    }


 

    /**
     * Verifies that changing the thread does not alter other post fields.
     * <p>
     * <b>Intent:</b> Ensures updating thread categorization preserves post integrity (author/title/content/time).
     * </p>
     */
    public static void testSetThread_doesNotChangeOtherFields() {
        section("4) Thread – changing thread does not change other fields\n");
        post p = new post(4, "Dave", "Original Title", "Original content.", "General", "2025-01-04 13:00", false);

        String authorBefore = p.getAuthor();
        String titleBefore = p.getTitle();
        String contentBefore = p.getContent();
        String timestampBefore = p.getTimestamp();
        boolean deletedBefore = p.getDeleted();

        p.setThread("Homework Help");

        check("author unchanged", authorBefore.equals(p.getAuthor()));
        check("title unchanged", titleBefore.equals(p.getTitle()));
        check("content unchanged", contentBefore.equals(p.getContent()));
        check("timestamp unchanged", timestampBefore.equals(p.getTimestamp()));
        check("deleted flag unchanged", deletedBefore == p.getDeleted());
        check("thread updated", "Homework Help".equals(p.getThread()));
    }

    /**
     * Verifies that thread value is preserved after soft-delete.
     * <p>
     * <b>Intent:</b> Confirms that deleting a post (soft delete) does not move or recategorize it.
     * </p>
     */
    public static void testThreadPreservedAfterSoftDelete() {
        section("5) Thread – preserved after soft delete\n");
        post p = new post(5, "Eve", "Some Title", "Some content.", "Quizzes", "2025-01-05 14:00", false);

        p.setDeleted(true);
        // mimic existing tests' deletion masking pattern
        p.setTitle("(this post has been deleted)");
        p.setContent("(this post has been deleted)");

        check("deleted flag true", p.getDeleted());
        check("thread preserved as Quizzes", "Quizzes".equals(p.getThread()));
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
        System.out.println("PostThreadTests Summary\n");
        System.out.println("Passed: " + numPassed + "  Failed: " + numFailed);
    }
}