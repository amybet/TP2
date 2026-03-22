package CRUDtesting;

import entityClasses.DiscussionInputRecognizer;

/**
 * <p> Title: PostValidationTests </p>
 *
 * <p> Description: A suite of semi-automated input validation tests for post titles, post bodies, 
 * and reply content. Verifies boundary conditions (minimum/maximum lengths), null/empty inputs,
 * and special-character handling using the DiscussionInputRecognizer as the validator. </p>
 *
 * <p> <b>Students User Stories covered:</b> </p>
 * <ul>
 * <li> Create a post with title and body (following input validation rules). </li>
 * <li> Update a post (same validation applied to updated values). </li>
 * </ul>
 * @author Amairani Caballero
 */
public class PostValidationTests {
	
	/**
     * Default constructor is not used because this class runs tests.
     */
    public PostValidationTests() {
    }
    
    /** Tracks the total number of tests that passed. */
    public static int numPassed = 0;
    
    /** Tracks the total number of tests that failed. */
    public static int numFailed = 0;

    /**
     * Runs all PostValidationTests checks and prints a summary of the results.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("PostValidationTests:\n");

        // Title validation
        testTitle_nullInput();
        testTitle_emptyString();
        testTitle_whitespaceOnly();
        testTitle_belowMinLength();
        testTitle_atMinLength();
        testTitle_aboveMinLength();
        testTitle_atMaxLength();
        testTitle_oneAboveMaxLength();
        testTitle_wellBelowMax();
        testTitle_specialCharacters();
        testTitle_numbersOnly();
        testTitle_mixedCaseAndSymbols();

        // Content validation
        testContent_nullInput();
        testContent_emptyString();
        testContent_whitespaceOnly();
        testContent_belowMinLength();
        testContent_atMinLength();
        testContent_aboveMinLength();
        testContent_atMaxLength();
        testContent_oneAboveMaxLength();
        testContent_specialCharacters();
        testContent_newlinesAndTabs();

        // Reply content validation
        testReplyContent_nullInput();
        testReplyContent_emptyString();
        testReplyContent_singleCharacter();
        testReplyContent_atMaxLength();
        testReplyContent_oneAboveMaxLength();

        printSummary();
    }

    // Title validation
    
    /**
     * Verifies that a null title is successfully rejected by the title validator.
     * <p>
     * <b>Intent:</b> Prevents null pointer exceptions and database corruption when a user 
     * attempts to create or update a post without passing valid title data.
     * </p>
     */
    public static void testTitle_nullInput() {
        section("1) Title – null input is invalid\n");
        String result = DiscussionInputRecognizer.checkPostTitle(null);
        check("returns non-empty error message", !result.isEmpty());
    }

    /**
     * Verifies that an empty string title is successfully rejected by the title validator.
     * <p>
     * <b>Intent:</b> Ensures users cannot submit blank titles, satisfying the requirement 
     * to provide meaningful identifiers when creating a post.
     * </p>
     */
    public static void testTitle_emptyString() {
        section("2) Title – empty string is invalid\n");
        String result = DiscussionInputRecognizer.checkPostTitle("");
        check("returns non-empty error message", !result.isEmpty());
    }
    
    /**
     * Verifies that a whitespace-only title is successfully rejected by the title validator.
     * <p>
     * <b>Intent:</b> Prevents users from bypassing the empty-string checks using spaces, 
     * guaranteeing that the post title contains readable characters.
     * </p>
     */
    public static void testTitle_whitespaceOnly() {
        section("3) Title – whitespace-only string is invalid\n");
        String result = DiscussionInputRecognizer.checkPostTitle("   ");
        check("returns non-empty error message", !result.isEmpty());
    }

    /**
     * Verifies that a title below the minimum length constraint (2 characters) is rejected.
     * <p>
     * <b>Intent:</b> Satisfies the strict boundary validation rules for post creation, 
     * ensuring titles are long enough to be descriptive (minimum 3 characters).
     * </p>
     */
    public static void testTitle_belowMinLength() {
        section("4) Title – 2 chars is below minimum (3)\n");
        String result = DiscussionInputRecognizer.checkPostTitle("AB");
        check("returns error for 2-char title", !result.isEmpty());
    }

    /**
     * Verifies that a title exactly at the minimum length constraint (3 characters) is accepted.
     * <p>
     * <b>Intent:</b> Proves the inclusive lower boundary of the validation logic works correctly 
     * for short but valid post titles.
     * </p>
     */
    public static void testTitle_atMinLength() {
        section("5) Title – 3 chars is exactly at minimum\n");
        String result = DiscussionInputRecognizer.checkPostTitle("ABC");
        check("3-char title is valid", result.isEmpty());
    }

    /**
     * Verifies that a title just above the minimum length constraint (4 characters) is accepted.
     * <p>
     * <b>Intent:</b> Confirms that inputs slightly larger than the lower boundary are safely processed 
     * by the post creation/update logic.
     * </p>
     */
    public static void testTitle_aboveMinLength() {
        section("6) Title – 4 chars is above minimum\n");
        String result = DiscussionInputRecognizer.checkPostTitle("ABCD");
        check("4-char title is valid", result.isEmpty());
    }

    /**
     * Verifies that a title exactly at the maximum allowed length (32 characters) is accepted.
     * <p>
     * <b>Intent:</b> Proves the inclusive upper boundary of the validation logic works correctly, 
     * ensuring the database limit is respected without cutting off valid input.
     * </p>
     */
    public static void testTitle_atMaxLength() {
        section("7) Title – 32 chars is exactly at maximum\n");
        String title = "A".repeat(32);
        String result = DiscussionInputRecognizer.checkPostTitle(title);
        check("32-char title is valid", result.isEmpty());
    }

    /**
     * Verifies that a title exactly one character above the maximum constraint (33 characters) is rejected.
     * <p>
     * <b>Intent:</b> Satisfies the strict upper boundary validation rules, preventing UI overflow 
     * or database truncation errors when creating or updating a post.
     * </p>
     */
    public static void testTitle_oneAboveMaxLength() {
        section("8) Title – 33 chars is one above maximum\n");
        String title = "A".repeat(33);
        String result = DiscussionInputRecognizer.checkPostTitle(title);
        check("33-char title is invalid", !result.isEmpty());
    }

    /**
     * Verifies that a standard, normally formatted title is accepted.
     * <p>
     * <b>Intent:</b> Confirms the baseline functionality of the create/update user story 
     * under typical use conditions.
     * </p>
     */
    public static void testTitle_wellBelowMax() {
        section("9) Title – normal question title is valid\n");
        String result = DiscussionInputRecognizer.checkPostTitle("Help with HW2 problem");
        check("normal title is valid", result.isEmpty());
    }

    /**
     * Verifies that special characters in a title are safely accepted when length rules are satisfied.
     * <p>
     * <b>Intent:</b> Ensures that legitimate user input containing punctuation or HTML-like 
     * characters is not falsely flagged as invalid during post creation.
     * </p>
     */
    public static void testTitle_specialCharacters() {
        section("10) Title – title with special characters\n");
        String result = DiscussionInputRecognizer.checkPostTitle("Q: Why? <test> & #1!");
        check("special-char title accepted (length ok)", result.isEmpty());
    }

    /**
     * Verifies that a numbers-only title is accepted when length rules are satisfied.
     * <p>
     * <b>Intent:</b> Ensures the validation logic does not overly restrict alphanumeric inputs 
     * for users naming threads after course numbers or dates.
     * </p>
     */
    public static void testTitle_numbersOnly() {
        section("11) Title – numbers-only title\n");
        String result = DiscussionInputRecognizer.checkPostTitle("12345");
        check("numeric title is valid", result.isEmpty());
    }

    /**
     * Verifies that mixed-case text and punctuation in a title are accepted.
     * <p>
     * <b>Intent:</b> Confirms robust handling of standard human grammar in the title field 
     * for the post creation user story.
     * </p>
     */
    public static void testTitle_mixedCaseAndSymbols() {
        section("12) Title – mixed-case title with punctuation\n");
        String result = DiscussionInputRecognizer.checkPostTitle("What's the answer?");
        check("mixed-case punctuated title is valid", result.isEmpty());
    }

    // Content/body validation
    // min = 5 chars, max = 500 chars

    /**
     * Verifies that null post content is successfully rejected by the body validator.
     * <p>
     * <b>Intent:</b> Prevents system crashes and enforces the requirement that a post must have 
     * a valid body when created or updated.
     * </p>
     */
    public static void testContent_nullInput() {
        section("13) Content/body – null input is invalid\n");
        String result = DiscussionInputRecognizer.checkPostContent(null);
        check("returns error for null content", !result.isEmpty());
    }

    /**
     * Verifies that an empty post body is successfully rejected by the content validator.
     * <p>
     * <b>Intent:</b> Ensures users cannot submit blank posts, directly satisfying the user story 
     * requirements for meaningful discussion creation.
     * </p>
     */
    public static void testContent_emptyString() {
        section("14) Content/body – empty string is invalid\n");
        String result = DiscussionInputRecognizer.checkPostContent("");
        check("returns error for empty content", !result.isEmpty());
    }

    /**
     * Verifies that whitespace-only post content is successfully rejected by the validator.
     * <p>
     * <b>Intent:</b> Prevents users from bypassing the empty-string checks using spaces, 
     * ensuring legitimate body text is provided.
     * </p>
     */
    public static void testContent_whitespaceOnly() {
        section("15) Content/body – whitespace-only is invalid\n");
        String result = DiscussionInputRecognizer.checkPostContent("    ");
        check("returns error for whitespace-only content", !result.isEmpty());
    }

    /**
     * Verifies that post content below the minimum length constraint (4 characters) is rejected.
     * <p>
     * <b>Intent:</b> Satisfies boundary rules ensuring post bodies are at least 5 characters long, 
     * preventing spam or excessively short submissions.
     * </p>
     */
    public static void testContent_belowMinLength() {
        section("16) Content/body – 4 chars is below minimum (5)\n");
        String result = DiscussionInputRecognizer.checkPostContent("ABCD");
        check("4-char content is invalid", !result.isEmpty());
    }

    /**
     * Verifies that post content exactly at the minimum length constraint (5 characters) is accepted.
     * <p>
     * <b>Intent:</b> Proves the inclusive lower boundary of the content validation logic works correctly.
     * </p>
     */
    public static void testContent_atMinLength() {
        section("17) Content/body – 5 chars is exactly at minimum\n");
        String result = DiscussionInputRecognizer.checkPostContent("ABCDE");
        check("5-char content is valid", result.isEmpty());
    }

    /**
     * Verifies that post content just above the minimum length (6 characters) is accepted.
     * <p>
     * <b>Intent:</b> Confirms that text slightly larger than the lower boundary is safely processed.
     * </p>
     */
    public static void testContent_aboveMinLength() {
        section("18) Content/body – 6 chars is above minimum\n");
        String result = DiscussionInputRecognizer.checkPostContent("ABCDEF");
        check("6-char content is valid", result.isEmpty());
    }

    /**
     * Verifies that post content exactly at the maximum length (500 characters) is accepted.
     * <p>
     * <b>Intent:</b> Proves the inclusive upper boundary of the validation logic works correctly, 
     * ensuring long, detailed posts are permitted up to the strict database limit.
     * </p>
     */
    public static void testContent_atMaxLength() {
        section("19) Content/body – 500 chars is exactly at maximum\n");
        String content = "A".repeat(500);
        String result = DiscussionInputRecognizer.checkPostContent(content);
        check("500-char content is valid", result.isEmpty());
    }

    /**
     * Verifies that post content one character above the maximum (501 characters) is rejected.
     * <p>
     * <b>Intent:</b> Prevents database truncation errors by strictly enforcing the 500-character 
     * limit during the post creation or update flow.
     * </p>
     */
    public static void testContent_oneAboveMaxLength() {
        section("20) Content/body – 501 chars is one above maximum\n");
        String content = "A".repeat(501);
        String result = DiscussionInputRecognizer.checkPostContent(content);
        check("501-char content is invalid", !result.isEmpty());
    }

    /**
     * Verifies that special characters and HTML-like strings in the body are accepted safely.
     * <p>
     * <b>Intent:</b> Ensures that legitimate formatting attempts or code snippets are not 
     * falsely flagged as invalid by the text parser.
     * </p>
     */
    public static void testContent_specialCharacters() {
        section("21) Content/body – content with HTML-like characters\n");
        String result = DiscussionInputRecognizer.checkPostContent("<b>bold</b> & \"quoted\" content");
        check("special-char content accepted", result.isEmpty());
    }

    /**
     * Verifies that post content containing formatting characters like newlines and tabs is accepted.
     * <p>
     * <b>Intent:</b> Supports a positive user experience by allowing structured, multi-paragraph 
     * text inputs when creating or updating a post.
     * </p>
     */
    public static void testContent_newlinesAndTabs() {
        section("22) Content/body – content with newlines and tabs\n");
        String result = DiscussionInputRecognizer.checkPostContent("Line1\nLine2\tTabbed.");
        check("newline/tab content accepted", result.isEmpty());
    }

    // Reply content validation
    // min = 1 char, max = 500 chars

    /**
     * Verifies that null reply content is successfully rejected by the reply validator.
     * <p>
     * <b>Intent:</b> Enforces basic input safety for the reply creation process, preventing 
     * null data from breaking the post's reply list.
     * </p>
     */
    public static void testReplyContent_nullInput() {
        section("23) Reply Content – null is invalid\n");
        String result = DiscussionInputRecognizer.checkReplyContent(null);
        check("returns error for null reply content", !result.isEmpty());
    }

    /**
     * Verifies that an empty reply string is successfully rejected by the validator.
     * <p>
     * <b>Intent:</b> Ensures users cannot submit blank replies, maintaining the quality 
     * of community discussion threads.
     * </p>
     */
    public static void testReplyContent_emptyString() {
        section("24) Reply Content – empty string is invalid\n");
        String result = DiscussionInputRecognizer.checkReplyContent("");
        check("returns error for empty reply content", !result.isEmpty());
    }
    
    /**
     * Verifies that a single-character reply is accepted.
     * <p>
     * <b>Intent:</b> Proves the inclusive lower boundary (minimum 1 character) for replies 
     * is respected, allowing short acknowledgments (e.g., "+", "Y", "N").
     * </p>
     */
    public static void testReplyContent_singleCharacter() {
        section("25) Reply Content – single character is valid (min = 1)\n");
        String result = DiscussionInputRecognizer.checkReplyContent("X");
        check("single-char reply is valid", result.isEmpty());
    }

    /**
     * Verifies that reply content exactly at the maximum length (500 characters) is accepted.
     * <p>
     * <b>Intent:</b> Proves the inclusive upper boundary for replies works correctly, 
     * ensuring detailed responses are permitted up to the database limit.
     * </p>
     */
    public static void testReplyContent_atMaxLength() {
        section("26) Reply Content – 500 chars is at maximum\n");
        String content = "R".repeat(500);
        String result = DiscussionInputRecognizer.checkReplyContent(content);
        check("500-char reply is valid", result.isEmpty());
    }

    /**
     * Verifies that reply content one character above the maximum (501 characters) is rejected.
     * <p>
     * <b>Intent:</b> Satisfies strict upper boundary validation rules, preventing database 
     * truncation errors when users submit long replies.
     * </p>
     */
    public static void testReplyContent_oneAboveMaxLength() {
        section("27) Reply Content – 501 chars exceeds maximum\n");
        String content = "R".repeat(501);
        String result = DiscussionInputRecognizer.checkReplyContent(content);
        check("501-char reply is invalid", !result.isEmpty());
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
        System.out.println("PostValidationTests Summary\n");
        System.out.println("Passed: " + numPassed + "  Failed: " + numFailed);
    }
}