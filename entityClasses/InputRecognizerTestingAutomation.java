package entityClasses;

/*******
 * <p> Title: InputRecognizerTestAutomation Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests for InputRecognizer validation</p>
 * 
 * <p> Copyright: Testing Team © 2026 </p>
 * 
 * @author Amairani Caballero
 * 
 * @version 1.00	2026-02-11 A set of semi-automated test cases covering username, password, email, and name validation
 * 
 */
public class InputRecognizerTestingAutomation {
	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/*
	 * This mainline displays a header to the console, performs a sequence of
	 * test cases, and then displays a summary of the results
	 */
	public static void main(String[] args) {
		System.out.println("INPUT RECOGNIZER TEST AUTOMATION\n");
		System.out.println("Testing: Username, Password, Email, and Name Validation\n");
		System.out.println("Total Test Cases: 70");

		/************** USERNAME VALIDATION TEST CASES **************/
		System.out.println("\n---------- USERNAME VALIDATION TESTS ----------");
		
		performTestCase(1, "username", "Username1", true);
		performTestCase(2, "username", "ab", false);
		performTestCase(3, "username", "1username", false);
		performTestCase(4, "username", "@User", false);
		performTestCase(5, "username", "UserName@", false);
		performTestCase(6, "username", "Us-er_Na.me", true);
		performTestCase(7, "username", "User--Name", false);
		performTestCase(8, "username", "Abcd1234Efgh5678", true);
		performTestCase(9, "username", "Abcd1234Efgh56789", false);
		performTestCase(10, "username", "", false);

		/************** PASSWORD VALIDATION TEST CASES **************/
		System.out.println("\n---------- PASSWORD VALIDATION TESTS ----------\n");
		
		performTestCase(11, "password", "Pass1234", true);
		performTestCase(12, "password", "Pass123", false);
		performTestCase(13, "password", "Passw8rd", true);
		performTestCase(14, "password", "password1", true);
		performTestCase(15, "password", "PASSWORD2", true);
		performTestCase(16, "password", "1234567890", true);
		performTestCase(17, "password", "Pass8~`!@#$%^&*()_-+{}[]|:,.?/", true);
		performTestCase(18, "password", "59BAAJUDMqy8S4s78IAOf7yIFW0YgfDzulzckUhdkflYwWjaOZpUeqVaYCiJHZ6R", true);
		performTestCase(19, "password", "Ld06vaANJRRzuVdIbxOCykeBF5uYdTUbOrxOaWcmPEx7RXa96hx9cdY5tGvNIHTzJ", false);
		performTestCase(20, "password", "", false);
		performTestCase(21, "password", "Pass word", false);

		/************** EMAIL VALIDATION TEST CASES **************/
		System.out.println("\n---------- EMAIL VALIDATION TESTS ----------\n");
		
		performTestCase(22, "email", "user@example.com", true);
		performTestCase(23, "email", "user123@example.com", true);
		performTestCase(24, "email", "user.name123@example.com", true);
		performTestCase(25, "email", "user@example123.com", true);
		performTestCase(26, "email", "user@ex-ample.com", true);
		performTestCase(27, "email", "user@example-dp.com", true);
		performTestCase(28, "email", "user@example@com", false);
		performTestCase(29, "email", "user @example.com", false);
		performTestCase(30, "email", ".user@example.com", false);
		performTestCase(31, "email", "user.@example.com", false);
		performTestCase(32, "email", "user..name@example.com", false);
		performTestCase(33, "email", "user@-example.com", false);
		performTestCase(34, "email", "user@example-.com", false);
		performTestCase(35, "email", "", false);
		performTestCase(36, "email", "@", false);

		/************** NAME VALIDATION TEST CASES **************/
		System.out.println("\n---------- NAME VALIDATION TESTS ----------\n");
		
		performTestCase(37, "name", "Johnny", true);
		performTestCase(38, "name", "Al", false);
		performTestCase(39, "name", "Bob", true);
		performTestCase(40, "name", "Alice1", false);
		performTestCase(41, "name", "Mary-Ann", false);
		performTestCase(42, "name", "Mary Ann", false);
		performTestCase(43, "name", "ALICE", true);
		performTestCase(44, "name", "alice", true);
		performTestCase(45, "name", "AABBCCDDEEFFGGHHIIJJKKLLMMNNOOPP", true);
		performTestCase(46, "name", "AABBCCDDEEFFGGHHIIJJKKLLMMNNOOPPP", false);
		performTestCase(47, "name", "", false);

		/************** Test Cases Summary **************/
		System.out.println("\nTest Automation Summary:\n");
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
		System.out.println("Total tests run: " + (numPassed + numFailed));
		System.out.println("Pass rate: " + String.format("%.1f", (numPassed * 100.0 / (numPassed + numFailed))) + "%");
		System.out.println("\n----------------------------");
	}
	
	/**********
	 * This method sets up the input value for the test cases
	 */
	public static void performTestCase(int testCase, String validatorType, String inputText, boolean expectedPass) {
		/************** Display an individual test case **************/
		System.out.println("\nTest case: " + testCase);
		System.out.println("Validator: " + validatorType.toUpperCase());
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("Expected: " + (expectedPass ? "VALID" : "INVALID"));
	//	System.out.println("\n-------------------------------------\n");
		
		//Call  Input Recognizer to process input
		String resultText = validateInput(validatorType, inputText);
		
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText.isEmpty()) {
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("\n***Success***\n"+ validatorType + ": <" + inputText + 
						"> is valid, so this is a pass!");
				System.out.println("\n-------------------------------------\n");

				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("\n***Failure***\n" + validatorType + ": <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				System.out.println("\n-------------------------------------\n");

				numFailed++;
			}
		}
		// If the resulting text is not empty, the recognizer rejected the input
		else {
			// If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println("\n***Failure***\n" + validatorType + ": <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!");
				System.out.println("Error message: " + resultText);
				System.out.println("\n-------------------------------------\n");
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("\n***Success***\n" + validatorType + ": <" + inputText + "> is invalid." + 
						"\nThis is a pass!");
				System.out.println("Error message: " + resultText);
				System.out.println("\n-------------------------------------\n");
				numPassed++;
			}
		}
	}

	/*
	 * This method validates the input based on the type
	 */
	private static String validateInput(String validatorType, String input) {
		switch (validatorType.toLowerCase()) {
			case "username":
				return InputRecognizer.checkUsername(input);
			case "password":
				return InputRecognizer.checkPassword(input);
			case "email":
				return InputRecognizer.checkEmailAddress(input);
			case "name":
				return InputRecognizer.checkName(input);
			default:
				return "Unknown validator type";
		}
	}

}