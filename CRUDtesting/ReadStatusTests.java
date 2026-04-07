package CRUDtesting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entityClasses.post;
import entityClasses.reply;

/**
 * <p> Title: ReadStatusTests </p>
 *
 * <p> Description: A suite of semi-automated tests verifying the read/unread tracking functionality 
 * for both posts and replies. Ensures that read states are properly tracked, filtered, and 
 * maintained independently for accurate UI display. </p>
 *
 * <p> <b>Students User Stories covered:</b> </p>
 * <ul>
 * <li> View list of posts and see which are read and which are unread; see reply count. </li>
 * <li> View replies and see how many are unread; filter to unread only. </li>
 * </ul>
 * @author Amairani Caballero
 */
public class ReadStatusTests {

    /**
     * Default constructor is not used because this class runs tests.
     */
    public ReadStatusTests() {
    }

    /** Tracks the total number of tests that passed. */
    public static int numPassed = 0;

    /** Tracks the total number of tests that failed. */
    public static int numFailed = 0;

    /**
     * Runs all ReadStatusTests checks and prints a summary of the results.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("ReadStatusTests:\n");

        testUnreadPosts_allUnreadInitially();
        testReadPost_markedRead();
        testReadPost_reReadIsIdempotent();
        testUnreadPosts_filterReturnsOnlyUnread();
        testReadPosts_filterReturnsOnlyRead();
        testReplyCount_correctOnPost();

        testUnreadReplies_allUnreadInitially();
        testReadReply_markedRead();
        testUnreadReplies_filterReturnsOnlyUnread();
        testUnreadCount_decrementAfterRead();

        testReadStatus_independentPerUser();

        printSummary();
    }

    // Post read/unread tracking

    /**
     * Verifies that before any posts are explicitly marked as read, all posts are considered unread by default.
     * <p>
     * <b>Intent:</b> Establishes baseline behavior so a first-time user sees posts as unread before viewing them.
     * </p>
     */
    public static void testUnreadPosts_allUnreadInitially() {
        section("1) Read Status – all posts unread before any are viewed\n");
        List<post> posts = buildSamplePosts();
        ReadTracker tracker = new ReadTracker();

        long unreadCount = posts.stream()
                .filter(p -> !tracker.isPostRead(p.getPostID()))
                .count();

        check("all posts unread initially", unreadCount == posts.size());
    }

    /**
     * Verifies that marking one post as read updates only that specific post's read status.
     * <p>
     * <b>Intent:</b> Ensures users can accurately track which posts they have viewed without affecting others.
     * </p>
     */
    public static void testReadPost_markedRead() {
        section("2) Read Status – marking a post as read\n");
        List<post> posts = buildSamplePosts();
        ReadTracker tracker = new ReadTracker();

        post first = posts.get(0);
        tracker.markPostRead(first.getPostID());

        check("first post is now read", tracker.isPostRead(first.getPostID()));
        check("second post is still unread", !tracker.isPostRead(posts.get(1).getPostID()));
    }

    /**
     * Verifies that marking the same post as read multiple times is idempotent (no double-counting).
     * <p>
     * <b>Intent:</b> Ensures stable metrics even if a user repeatedly opens the same post.
     * </p>
     */
    public static void testReadPost_reReadIsIdempotent() {
        section("3) Read Status – marking read twice has no extra effect\n");
        ReadTracker tracker = new ReadTracker();

        tracker.markPostRead(42);
        tracker.markPostRead(42);

        check("post still read after second mark", tracker.isPostRead(42));
        check("read count is still 1", tracker.totalPostsRead() == 1);
    }

    /**
     * Verifies that filtering for unread posts returns only posts not yet marked as read.
     * <p>
     * <b>Intent:</b> Supports UI filtering so users can focus only on posts they have not read.
     * </p>
     */
    public static void testUnreadPosts_filterReturnsOnlyUnread() {
        section("4) Read Status – filter unread returns only unread posts\n");
        List<post> posts = buildSamplePosts();
        ReadTracker tracker = new ReadTracker();

        tracker.markPostRead(posts.get(0).getPostID());
        tracker.markPostRead(posts.get(1).getPostID());

        List<post> unread = filterUnreadPosts(posts, tracker);

        check("unread list excludes the 2 read posts", unread.size() == posts.size() - 2);
        check("unread list contains no read posts", unread.stream().noneMatch(p -> tracker.isPostRead(p.getPostID())));
    }

    /**
     * Verifies that filtering for read posts returns exactly the posts that were explicitly marked as read.
     * <p>
     * <b>Intent:</b> Supports UI behavior so users can review what they have already read.
     * </p>
     */
    public static void testReadPosts_filterReturnsOnlyRead() {
        section("5) Read Status – filter read returns only read posts\n");
        List<post> posts = buildSamplePosts();
        ReadTracker tracker = new ReadTracker();

        tracker.markPostRead(posts.get(2).getPostID());

        List<post> read = filterReadPosts(posts, tracker);

        check("read list contains exactly 1 post", read.size() == 1);
        check("that post is the one marked read", read.get(0).getPostID() == posts.get(2).getPostID());
    }

    // Reply count on a post

    /**
     * Verifies that a post's reply list size correctly reflects reply count after additions.
     * <p>
     * <b>Intent:</b> Supports showing reply counts on the main post list view.
     * </p>
     */
    public static void testReplyCount_correctOnPost() {
        section("6) Reply Count – post shows correct reply count\n");

        // NOTE: thread added to post constructor; "General" used here as the standard test thread
        post p = new post(10, "Alice", "Q", "Body.", "General", "t1", false);

        check("reply count is 0 initially", p.getReplies().size() == 0);

        p.addReply(new reply(1, 10, "Bob", "R1.", "t2"));
        p.addReply(new reply(2, 10, "Carol", "R2.", "t3"));

        check("reply count is 2 after adding two replies", p.getReplies().size() == 2);
    }

    // Reply read/unread tracking

    /**
     * Verifies that before any replies are explicitly marked read, newly loaded replies default to unread.
     * <p>
     * <b>Intent:</b> Establishes baseline behavior so unread counts are accurate when opening replies for the first time.
     * </p>
     */
    public static void testUnreadReplies_allUnreadInitially() {
        section("7) Reply Read Status – all replies unread initially\n");

        post p = new post(20, "Dave", "Post", "Body.", "General", "t4", false);
        reply r1 = new reply(10, 20, "Eve", "R1.", "t5");
        reply r2 = new reply(11, 20, "Frank", "R2.", "t6");
        p.addReply(r1);
        p.addReply(r2);

        ReadTracker tracker = new ReadTracker();

        long unread = p.getReplies().stream()
                .filter(r -> !tracker.isReplyRead(r.getReplyID()))
                .count();

        check("both replies unread initially", unread == 2);
    }

    /**
     * Verifies that marking a single reply as read updates only that reply's read status.
     * <p>
     * <b>Intent:</b> Ensures accurate per-reply tracking so unread counts do not drop incorrectly.
     * </p>
     */
    public static void testReadReply_markedRead() {
        section("8) Reply Read Status – marking a reply as read\n");
        ReadTracker tracker = new ReadTracker();

        tracker.markReplyRead(10);

        check("reply 10 is now read", tracker.isReplyRead(10));
        check("reply 11 still unread", !tracker.isReplyRead(11));
    }

    /**
     * Verifies that filtering for unread replies returns only the replies not yet marked as read.
     * <p>
     * <b>Intent:</b> Directly supports the UI requirement to filter replies to unread only.
     * </p>
     */
    public static void testUnreadReplies_filterReturnsOnlyUnread() {
        section("9) Reply Read Status – filter to unread replies only\n");

        post p = new post(30, "Grace", "Post", "Body.", "General", "t7", false);
        reply r1 = new reply(20, 30, "Henry", "R1.", "t8");
        reply r2 = new reply(21, 30, "Ivan", "R2.", "t9");
        reply r3 = new reply(22, 30, "Julia", "R3.", "t10");
        p.addReply(r1);
        p.addReply(r2);
        p.addReply(r3);

        ReadTracker tracker = new ReadTracker();
        tracker.markReplyRead(r1.getReplyID());

        List<reply> unread = filterUnreadReplies(p.getReplies(), tracker);

        check("2 unread replies remain", unread.size() == 2);
        check("read reply not in list", !unread.contains(r1));
        check("unread replies r2 and r3 present", unread.contains(r2) && unread.contains(r3));
    }

    /**
     * Verifies that the unread reply count decreases as replies are marked read.
     * <p>
     * <b>Intent:</b> Ensures unread counters stay accurate as the user reads through a discussion thread.
     * </p>
     */
    public static void testUnreadCount_decrementAfterRead() {
        section("10) Reply Read Status – unread count decrements as replies are read\n");

        post p = new post(40, "Kevin", "Post", "Body.", "General", "t11", false);
        for (int i = 1; i <= 5; i++) {
            p.addReply(new reply(30 + i, 40, "user" + i, "Reply " + i, "t" + i));
        }

        ReadTracker tracker = new ReadTracker();

        long unread0 = countUnreadReplies(p.getReplies(), tracker);
        check("5 unread initially", unread0 == 5);

        tracker.markReplyRead(31);
        long unread1 = countUnreadReplies(p.getReplies(), tracker);
        check("4 unread after reading one", unread1 == 4);

        tracker.markReplyRead(32);
        tracker.markReplyRead(33);
        long unread3 = countUnreadReplies(p.getReplies(), tracker);
        check("2 unread after reading three total", unread3 == 2);
    }

    // Read status is per user (independent trackers)

    /**
     * Verifies that read/unread status is tracked per user via independent trackers.
     * <p>
     * <b>Intent:</b> Ensures one user's reading activity does not alter another user's unread flags.
     * </p>
     */
    public static void testReadStatus_independentPerUser() {
        section("11) Read Status – independent per user\n");

        List<post> posts = buildSamplePosts();
        ReadTracker trackerUserA = new ReadTracker();
        ReadTracker trackerUserB = new ReadTracker();

        trackerUserA.markPostRead(posts.get(0).getPostID());
        trackerUserA.markPostRead(posts.get(1).getPostID());

        check("user A has 2 posts read", trackerUserA.totalPostsRead() == 2);
        check("user B has 0 posts read", trackerUserB.totalPostsRead() == 0);
        check("user A sees post 0 as read", trackerUserA.isPostRead(posts.get(0).getPostID()));
        check("user B sees post 0 as unread", !trackerUserB.isPostRead(posts.get(0).getPostID()));
    }

    // ReadTracker simulation

    /**
     * A simulated tracker for read posts and replies using ID sets.
     * <p>
     * <b>Intent:</b> Demonstrates how read status can be stored independently of the database and
     * independently per user session (or per user account).
     * </p>
     */
    public static class ReadTracker {

        /**
         * Default constructor is not used because this class runs tests.
         */
        public ReadTracker() {
        }

        /** The set of post IDs that have been marked as read. */
        private final Set<Integer> readPostIds = new HashSet<>();

        /** The set of reply IDs that have been marked as read. */
        private final Set<Integer> readReplyIds = new HashSet<>();

        /**
         * Marks a post as read by adding its ID to the read set.
         *
         * @param postId The ID of the post.
         */
        public void markPostRead(int postId) {
            readPostIds.add(postId);
        }

        /**
         * Checks if a post is currently marked as read.
         *
         * @param postId The ID of the post.
         * @return true if read, false otherwise.
         */
        public boolean isPostRead(int postId) {
            return readPostIds.contains(postId);
        }

        /**
         * Gets the total number of posts marked as read.
         *
         * @return The total number of posts read by this tracker.
         */
        public int totalPostsRead() {
            return readPostIds.size();
        }

        /**
         * Marks a reply as read by adding its ID to the read set.
         *
         * @param replyId The ID of the reply.
         */
        public void markReplyRead(int replyId) {
            readReplyIds.add(replyId);
        }

        /**
         * Checks if a reply is currently marked as read.
         *
         * @param replyId The ID of the reply.
         * @return true if read, false otherwise.
         */
        public boolean isReplyRead(int replyId) {
            return readReplyIds.contains(replyId);
        }
    }

    // Filter helpers

    /**
     * Filters a list to return only posts that are currently unread according to the tracker.
     *
     * @param posts The list of posts to filter.
     * @param tracker The ReadTracker tracking the status.
     * @return A list of unread posts.
     */
    public static List<post> filterUnreadPosts(List<post> posts, ReadTracker tracker) {
        List<post> result = new ArrayList<>();
        for (post p : posts) {
            if (!tracker.isPostRead(p.getPostID())) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Filters a list to return only posts that are currently read according to the tracker.
     *
     * @param posts The list of posts to filter.
     * @param tracker The ReadTracker tracking the status.
     * @return A list of read posts.
     */
    public static List<post> filterReadPosts(List<post> posts, ReadTracker tracker) {
        List<post> result = new ArrayList<>();
        for (post p : posts) {
            if (tracker.isPostRead(p.getPostID())) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Filters a list to return only replies that are currently unread according to the tracker.
     *
     * @param replies The list of replies to filter.
     * @param tracker The ReadTracker tracking the status.
     * @return A list of unread replies.
     */
    public static List<reply> filterUnreadReplies(List<reply> replies, ReadTracker tracker) {
        List<reply> result = new ArrayList<>();
        for (reply r : replies) {
            if (!tracker.isReplyRead(r.getReplyID())) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Calculates the total count of replies that are unread according to the tracker.
     *
     * @param replies The list of replies to count.
     * @param tracker The ReadTracker tracking the status.
     * @return The number of unread replies.
     */
    public static long countUnreadReplies(List<reply> replies, ReadTracker tracker) {
        return replies.stream()
                .filter(r -> !tracker.isReplyRead(r.getReplyID()))
                .count();
    }

    /**
     * Builds a fixed sample list of posts for read/unread test scenarios.
     *
     * @return A populated list of dummy posts.
     */
    public static List<post> buildSamplePosts() {
        List<post> posts = new ArrayList<>();
        // Thread added to post constructor; "General" used here as the standard test thread
        posts.add(new post(1, "Alice", "Post A", "Body A.", "General", "t1", false));
        posts.add(new post(2, "Bob", "Post B", "Body B.", "General", "t2", false));
        posts.add(new post(3, "Carol", "Post C", "Body C.", "General", "t3", false));
        posts.add(new post(4, "Dave", "Post D", "Body D.", "General", "t4", false));
        return posts;
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
        System.out.println("ReadStatusTests Summary\n");
        System.out.println("Passed: " + numPassed + "  Failed: " + numFailed);
    }
}