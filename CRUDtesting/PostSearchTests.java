package CRUDtesting;

import java.util.ArrayList;
import java.util.List;

import entityClasses.post;

/**
 * <p> Title: PostSearchTests </p>
 *
 * <p> Description: A suite of semi-automated tests verifying the robustness of the post keyword 
 * search functionality. Ensures that the system accurately filters posts by keywords in titles 
 * or bodies, handles edge cases (case-insensitivity, special characters), and properly excludes 
 * soft-deleted records from search results. </p>
 *
 * <p> <b>Students User Stories covered:</b> </p>
 * <ul>
 * <li> View a list of all posts; search with keywords; all threads searched when no thread is specified. </li>
 * <li> Identify read vs. unread posts. </li>
 * </ul>
 * @author Amairani Caballero
 */
public class PostSearchTests {

    /**
     * Default constructor is not used because this class runs tests.
     */
    public PostSearchTests() {
    }

    /** Tracks the total number of tests that passed. */
    public static int numPassed = 0;

    /** Tracks the total number of tests that failed. */
    public static int numFailed = 0;

    /** The standard deletion marker displayed to viewers when a post is soft-deleted. */
    public static final String DELETED_MARKER = "(this post has been deleted)";

    /**
     * Runs all PostSearchTests checks and prints a summary of the results.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("PostSearchTests:");

        testSearch_exactTitleMatch();
        testSearch_partialTitleMatch();
        testSearch_contentKeyword();
        testSearch_caseInsensitive();
        testSearch_noMatch();
        testSearch_emptyKeyword_returnsAll();
        testSearch_deletedPostsExcluded();
        testSearch_multipleMatches();
        testSearch_keywordInBothTitleAndContent();
        testSearch_specialCharacterKeyword();
        testSearch_acrossMultipleThreads();
        testSearch_singleThreadFilter();

        printSummary();
    }

    // Keyword search tests

    /**
     * Verifies that searching for an exact full title successfully returns the specific matching post.
     * <p>
     * <b>Intent:</b> Satisfies the baseline "search with keywords" user story by proving the system
     * can reliably locate a specific record when given an exact query.
     * </p>
     */
    public static void testSearch_exactTitleMatch() {
        section("1) Search – exact title match returns post\n");
        List<post> posts = buildSamplePosts();
        List<post> results = searchPosts(filterActive(posts), "Help with HW2");
        check("1 result found", results.size() == 1);
        check("result title matches", "Help with HW2".equals(results.get(0).getTitle()));
    }

    /**
     * Verifies that searching by a partial title keyword returns all posts containing that substring.
     * <p>
     * <b>Intent:</b> Proves the search engine is flexible and user-friendly, satisfying the requirement
     * to find content without requiring users to type out exact, full titles.
     * </p>
     */
    public static void testSearch_partialTitleMatch() {
        section("2) Search – partial title keyword returns matching posts\n");
        List<post> posts = buildSamplePosts();
        List<post> results = searchPosts(filterActive(posts), "HW2");
        check("at least 1 result for 'HW2'", results.size() >= 1);
        check("all results contain 'HW2'",
                results.stream().allMatch(p -> containsKeyword(p, "HW2")));
    }

    /**
     * Verifies that a keyword found only in the post body (and not the title) successfully returns the post.
     * <p>
     * <b>Intent:</b> Confirms the search feature thoroughly scans all user-generated content,
     * fulfilling the user expectation that "search with keywords" includes the actual discussion text.
     * </p>
     */
    public static void testSearch_contentKeyword() {
        section("3) Search – keyword in body only is found\n");
        List<post> posts = buildSamplePosts();
        List<post> results = searchPosts(filterActive(posts), "arraylist");
        check("posts with 'arraylist' in body found", results.size() >= 1);
    }

    /**
     * Verifies that the search functionality is completely case-insensitive.
     * <p>
     * <b>Intent:</b> Ensures a forgiving user experience. Users should not miss out on relevant
     * search results due to capitalization errors, satisfying robust search requirements.
     * </p>
     */
    public static void testSearch_caseInsensitive() {
        section("4) Search – search is case-insensitive\n");
        List<post> posts = filterActive(buildSamplePosts());

        List<post> upper = searchPosts(posts, "HOMEWORK");
        List<post> lower = searchPosts(posts, "homework");
        List<post> mixed = searchPosts(posts, "Homework");

        check("upper-case search matches", upper.size() >= 1);
        check("lower-case search matches", lower.size() >= 1);
        check("mixed-case search matches", mixed.size() >= 1);
        check("all three return same count",
                upper.size() == lower.size() && lower.size() == mixed.size());
    }

    /**
     * Verifies that searching for a keyword not present in any posts returns an empty list.
     * <p>
     * <b>Intent:</b> Ensures the system gracefully handles failed searches without crashing
     * or returning false positives to the user.
     * </p>
     */
    public static void testSearch_noMatch() {
        section("5) Search – keyword with no match returns empty list\n");
        List<post> posts = filterActive(buildSamplePosts());
        List<post> results = searchPosts(posts, "no_match_123");
        check("no results for unknown keyword", results.isEmpty());
    }

    /**
     * Verifies that submitting an empty or null keyword returns the full, unfiltered list of active posts.
     * <p>
     * <b>Intent:</b> Directly satisfies the "View a list of all posts" user story when the user
     * navigates to the forum without applying a specific search filter.
     * </p>
     */
    public static void testSearch_emptyKeyword_returnsAll() {
        section("6) Search – empty/null keyword returns all posts\n");
        List<post> posts = buildSamplePosts();
        List<post> active = filterActive(posts);

        List<post> resultsEmpty = searchPosts(active, "");
        List<post> resultsNull = searchPosts(active, null);

        check("empty keyword returns all active posts", resultsEmpty.size() == active.size());
        check("null keyword returns all active posts", resultsNull.size() == active.size());
    }

    /**
     * Verifies that soft-deleted posts are completely excluded from the active set and search results.
     * <p>
     * <b>Intent:</b> Maintains viewer privacy by ensuring deleted content cannot be discovered
     * or leaked through the search feature.
     * </p>
     */
    public static void testSearch_deletedPostsExcluded() {
        section("7) Search – deleted posts are excluded from search results\n");
        List<post> posts = buildSamplePosts();

        List<post> active = filterActive(posts);
        boolean allActive = active.stream().noneMatch(post::getDeleted);
        check("no deleted posts in active list", allActive);

        // Also ensure searching "deleted marker" doesn't return deleted posts since we search only active list
        List<post> results = searchPosts(active, DELETED_MARKER);
        check("search does not return deleted post marker content", results.isEmpty());
    }

    /**
     * Verifies that a keyword appearing in multiple distinct posts returns a list of all matching posts.
     * <p>
     * <b>Intent:</b> Proves the system can handle bulk retrievals for common search terms,
     * aggregating all relevant discussions for the user.
     * </p>
     */
    public static void testSearch_multipleMatches() {
        section("8) Search – keyword matching multiple posts returns all\n");
        List<post> posts = new ArrayList<>();
        posts.add(new post(1, "Alice", "Question about loops", "Loops in Java?", "General", "t1", false));
        posts.add(new post(2, "Bob", "How do loops work", "I need help with them.", "General", "t2", false));
        posts.add(new post(3, "Carol", "Recursion question", "Not related to this.", "General", "t3", false));

        List<post> results = searchPosts(posts, "loops");
        check("two posts match 'loops'", results.size() == 2);
    }

    /**
     * Verifies that a post containing the keyword in both its title and its body is only included once in the results.
     * <p>
     * <b>Intent:</b> Prevents duplicate entries in the UI, ensuring a clean and accurate
     * "list of all posts" for the viewer.
     * </p>
     */
    public static void testSearch_keywordInBothTitleAndContent() {
        section("9) Search – post matching keyword in both title and body appears once\n");
        List<post> posts = new ArrayList<>();
        posts.add(new post(1, "Alice", "Java recursion", "Recursion in Java is tricky.", "General", "t1", false));

        List<post> results = searchPosts(posts, "recursion");
        check("post appears exactly once despite two matches", results.size() == 1);
    }

    /**
     * Verifies that keywords containing special characters are processed and matched correctly.
     * <p>
     * <b>Intent:</b> Ensures technical queries (such as programming languages like "C++")
     * work without breaking the search algorithm.
     * </p>
     */
    public static void testSearch_specialCharacterKeyword() {
        section("10) Search – keyword with special chars searched correctly\n");
        List<post> posts = new ArrayList<>();
        posts.add(new post(1, "Dave", "C++ vs Java", "C++ syntax.", "General", "t1", false));

        List<post> results = searchPosts(posts, "C++");
        check("special char keyword finds matching post", results.size() == 1);
    }

    /**
     * Verifies that posts across multiple threads are searched when no specific thread is targeted.
     * <p>
     * <b>Intent:</b> Directly satisfies the explicit user story requirement:
     * "all threads searched when no thread is specified."
     * </p>
     */
    public static void testSearch_acrossMultipleThreads() {
        section("11) Search – search covers all threads when no thread specified\n");

        List<post> thread1 = new ArrayList<>();
        thread1.add(new post(10, "Alice", "General Q", "General body.", "General", "t10", false));

        List<post> thread2 = new ArrayList<>();
        thread2.add(new post(11, "Bob", "Exam Q", "Exam body.", "Exams", "t11", false));

        List<post> thread3 = new ArrayList<>();
        thread3.add(new post(12, "Carol", "Homework HW2", "Homework body.", "Homework", "t12", false));

        // Simulated "no thread specified" behavior: search all posts in all threads.
        List<post> allThreads = new ArrayList<>();
        allThreads.addAll(thread1);
        allThreads.addAll(thread2);
        allThreads.addAll(thread3);

        List<post> results = searchPosts(allThreads, "body");
        check("keyword found across all threads", results.size() == 3);
    }

    /**
     * Verifies that when a search is restricted to a single thread, only posts from that thread are returned.
     * <p>
     * <b>Intent:</b> Proves the filtering logic can isolate threads, allowing users to
     * search within specific contexts.
     * </p>
     */
    public static void testSearch_singleThreadFilter() {
        section("12) Search – restricting search to one thread results\n");

        List<post> thread1 = new ArrayList<>();
        thread1.add(new post(20, "Alice", "Thread1 post A", "Shared keyword.", "Thread1", "t20", false));

        List<post> thread2 = new ArrayList<>();
        thread2.add(new post(21, "Bob", "Thread2 post B", "Shared keyword.", "Thread2", "t21", false));

        // Search within thread1 only
        List<post> results = searchPosts(thread1, "keyword");

        check("only 1 result when searching single thread", results.size() == 1);
        check("result is from thread1", results.get(0).getPostID() == 20);
        check("result thread is Thread1", "Thread1".equals(results.get(0).getThread()));

        // Sanity: thread2 not searched here
        List<post> resultsThread2 = searchPosts(thread2, "keyword");
        check("thread2 also has 1 result when searched alone", resultsThread2.size() == 1);
    }

    // Search and filter utilities

    /**
     * Returns posts whose title or body contains the keyword (case-insensitive).
     * If keyword is null or empty, all posts are returned.
     *
     * @param posts The list of posts to search through.
     * @param keyword The term to search for in the post titles and content.
     * @return A list of posts matching the search criteria.
     */
    public static List<post> searchPosts(List<post> posts, String keyword) {
        List<post> results = new ArrayList<>();

        if (keyword == null || keyword.isEmpty()) {
            results.addAll(posts);
            return results;
        }

        String lc = keyword.toLowerCase();
        for (post p : posts) {
            if (containsKeyword(p, lc)) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Evaluates if the post title or body contains the keyword (case-insensitive).
     *
     * @param p The post to evaluate.
     * @param keyword The keyword to search for (any case).
     * @return True if the keyword is found in either title or content, false otherwise.
     */
    public static boolean containsKeyword(post p, String keyword) {
        if (p == null || keyword == null) return false;
        String lc = keyword.toLowerCase();

        return (p.getTitle() != null && p.getTitle().toLowerCase().contains(lc))
                || (p.getContent() != null && p.getContent().toLowerCase().contains(lc));
    }

    /**
     * Filters a list of posts, returning only those that are not marked as deleted.
     *
     * @param posts The complete list of posts.
     * @return A list containing only active (non-deleted) posts.
     */
    public static List<post> filterActive(List<post> posts) {
        List<post> active = new ArrayList<>();
        for (post p : posts) {
            if (!p.getDeleted()) active.add(p);
        }
        return active;
    }

    /**
     * Builds a small, fixed test data set covering several search scenarios.
     *
     * @return A populated list of sample posts for testing.
     */
    public static List<post> buildSamplePosts() {
        List<post> posts = new ArrayList<>();

        posts.add(new post(1, "Alice", "Help with HW2",
                "I need help on HW2 homework.", "Homework", "t1", false));

        posts.add(new post(2, "Bob", "ArrayList question",
                "How do I use an arraylist?", "General", "t2", false));

        posts.add(new post(3, "Carol", "Recursion homework help",
                "Stuck on recursion.", "Homework", "t3", false));

        posts.add(new post(4, "Dave", "Exam study tips",
                "Any tips for the final?", "Exams", "t4", false));

        posts.add(new post(5, "Eve", "Java syntax question",
                "When to use semicolons?", "General", "t5", false));

        // A deleted post should not appear in active searches
        post deleted = new post(6, "Frank", "Old Topic", "Old body.", "General", "t6", false);
        deleted.setDeleted(true);
        deleted.setTitle(DELETED_MARKER);
        deleted.setContent(DELETED_MARKER);
        posts.add(deleted);

        return posts;
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
        System.out.println("PostSearchTests Summary\n");
        System.out.println("Passed: " + numPassed + "  Failed: " + numFailed);
    }
}