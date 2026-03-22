package entityClasses;

/*******
 * <p> Title: InputRecognizer Class </p>
 * 
 * <p> Description: This InputRecognizer class has functions for validating the input for emails, passwords, usernames, and names. </p>
 * 
 * 
 * @author Nicholas Hamilton
 * @author Jarod DeFilippo
 * 
 * 
 */ 


public class InputRecognizer {
	/**
	 * <p> Title: FSM-translated InptuRecognizer. </p>
	 * 
	 * <p> Description: A demonstration of the mechanical translation of Finite State Machine 
	 * diagram into an executable Java program using the Input Recognizer. The code 
	 * detailed design is based on a while loop with a select list</p>
	 * 
	 * 
	 * @author Nicholas Hamilton
	 * @author Jarod DeFilippo
	 * 
	 * @version 0.00		2018-02-04	Initial baseline 
	 * @version 2.00		2022-01-06	Rewritten to recognize email addresses and enhanced
	 * 										to support FSM with up through 999 states for the 
	 * 										trace output to align nicely
	 * @version 3.00		2022-03-22	Adjusted to clean up the code and resolving alignment
	 * 										issues with the design and to correct the issue
	 * 										with an empty email address
	 * @version 3.01		2026-02-10	Updated implementation to follow FSM diagrams
	 * 										
	 * 
	 */

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	public static String emailAddressErrorMessage = "";	// The error message text
	public static String emailAddressInput = "";		// The input being processed
	public static int emailAddressIndexofError = -1;	// The index where the error was located
	
	public static String passwordErrorMessage = "";	// The error message text
	public static String passwordInput = "";		// The input being processed
	public static int passwordIndexOfError = -1;	// The index where the error was located
	
	public static String usernameErrorMessage = "";	// The error message text
	public static String usernameInput = "";		// The input being processed
	public static int usernameIndexOfError = -1;	// The index where the error was located
	
	public static String nameErrorMessage = "";	// The error message text
	public static String nameInput = "";		// The input being processed
	public static int nameIndexOfError = -1;	// The index where the error was located
	
	
	
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running
	private static int domainPartCounter = 0;			// A domain name may not exceed 63 characters
	
	private static final String SPECIALCHARS = "~`!@#$%^&*()_-+{}[]|:,.?/"; // Set containing special characters allowed in password
	private static final String TRACE_HEADER = "\nCurrent Final Input  Next  Date\nState   State Char  State  Size";
	
	
	
	/**********
	 * This private method checks if a char c is in the set defined as a string SPECIALCHARS. This is helper method that currently 
	 * is only used to check if a character in a password belongs to the set of SPECIALCHARS
	 * 
	 * @param c				The input string
	 * @return				True or False if c is in the string SPECIALCHARS
	 */
	private static boolean isSpecial(char c) {
		return SPECIALCHARS.indexOf(c) >= 0;
	}
	

	/**********
	 * This private method display the input line and then on a line under it displays an up arrow
	 * at the point where an error should one be detected.  This method is designed to be used to 
	 * display the error message on the console terminal.
	 * 
	 * @param input				The input string
	 * @param currentCharNdx	The location where an error was found
	 * @return					Two lines, the entire input line followed by a line with an up arrow
	 */
	private static String displayInput(String input, int currentCharNdx) {
		// Display the entire input line
		String result = input.substring(0,currentCharNdx) + "\n";

		return result;
	}

	// Private method to display debugging data
	private static void displayDebuggingInfo() {
		// Display the current state of the FSM as part of an execution trace
		if (currentCharNdx >= inputLine.length())
			// display the line with the current state numbers aligned
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
					((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
					nextState + "     " + domainPartCounter);
	}
	
	// Private method to move to the next character within the limits of the input line
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			System.out.println("End of input was found!");
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method. This method is updated for Email Address Validator FSM. 
	 * LPChar: Letters (A-Z, a-z), numbers (0-9). If there is a period it must be between two LPChars.
	 * DPChar: Letters (A-Z, a-z), numbers (0-9). If there is a period or hyphen, it must 
	 * be between two DPChars
	 * A valid input must consist of at least one LPChar followed by the @ symbol and then two DPChar.  
	 * The maximum length of the input is 320 characters. No spaces are allowed.  
	 * After the @ symbol the amount of DPChars must be between inclusive 2 to 63 characters.
	 * The @ symbol must be after an LPChar and followed by a DPChar.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	public static String checkEmailAddress(String input) {
		// The following are the local variable used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		int LPCharCounter = 0;
		int DPCharCounter = 0;


		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		emailAddressInput = input;			// Save a copy of the input

		// Let's ensure there is input
		if (input.length() <= 0) {
			emailAddressErrorMessage = "Email address cannot be empty.";
			return emailAddressErrorMessage + displayInput(input, 0);
		}
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// Let's ensure the address is not too long
		if (input.length() > 320) {
			emailAddressErrorMessage = "A valid email address must be no more than 320 characters.\n";
			return emailAddressErrorMessage + displayInput(input, 320);
		}
		running = true;						// Start the loop
		System.out.println(TRACE_HEADER);

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			nextState = -1;						// Default to there is no next state		
			
			switch (state) {
			case 0: 
				// State 0 has 3 valid transitions.

				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')) {	// Digit
					nextState = 1;
					LPCharCounter++;
				}
				// If it is none of those characters, the FSM halts
				else { 
					running = false;
				}
				
				break;				
				// The execution of this state is finished
			
			case 1: 
				// State 1 has three valid transitions.  
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')) {	// Digit
					nextState = 1;
					LPCharCounter++;
				
				}
				else if(currentChar == '.') {
					nextState = 0;
					LPCharCounter++;
				}
				else if(currentChar == '@') {
					nextState = 2;
				}
				else {
					running = false;
				}

				break;
				// The execution of this state is finished
			case 2: 
				// State 1 has three valid transitions.  
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')) {	// Digit
					nextState = 3;
					DPCharCounter++;
				}
				
				else {
					running = false;
				}

				break;
				// The execution of this state is finished
							
			case 3: 
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')){	// Digit
					nextState = 3;
					DPCharCounter++;
				}
				else if(currentChar == '.'){
					nextState = 5;
					DPCharCounter++;
				}
				else if(currentChar == '-'){
					nextState = 4;
					DPCharCounter++;
				}
				else {
					running = false;
				}
				
				break;
	
			case 4:
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')){	// Digit
					nextState = 3;
					DPCharCounter++;
				}
				
				else {
					running = false;
				}
				break;
				
			case 5:
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')){	// Digit
					nextState = 6;
					DPCharCounter++;
				}
				else {
					running = false;
				}
				break;
				
			case 6:
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')){	// Digit
					nextState = 6;
					DPCharCounter++;
				}
				else if(currentChar == '-'){
					nextState = 7;
					DPCharCounter++;
				}
				else {
					running = false;
				}
				break;
				
			case 7:
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')){	// Digit
					nextState = 6;
					DPCharCounter++;
				}
				else {
					running = false;
				}
				break;
			}
			
			if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next character
				// in the input and if there is one, it fetches that character and updates the 
				// currentChar.  If there is no next character the currentChar is set to a blank.
				
				moveToNextCharacter();
				
				// Move to the next state
				state = nextState;
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again

		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");

		emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
			if(currentCharNdx <= 0) {
				emailAddressErrorMessage = "Email may only start with alphanumeric characters.\n";
			}
			else {
				emailAddressErrorMessage = "Local part of email can only contain a period between two alphanumeric characters.\n";
			}
			return emailAddressErrorMessage;

		case 1:
			// State 1 is not a final state, so we can return a very specific error message
			emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
			emailAddressErrorMessage = "Email requires only alphanumeric characters followed by '@'.\n";
			return emailAddressErrorMessage;

		case 2:
			// State 2 is not a final state, so we can return a very specific error message
			emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
			emailAddressErrorMessage = "Domain part of email requires alphanumeric characters following '@' and can contain '.' between two alphanumeric characters.\n";
			return emailAddressErrorMessage;
			// Replace this with the required code

		case 3:
			// State 3 is a Final State, so this is not an error if the input is empty, otherwise
			// we can return a very specific error message.
			
			emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character
			emailAddressErrorMessage = "Domain part can only be alphanumeric characters and must have a '.' after at least one alphanumeric character.\n";
			return emailAddressErrorMessage;
		

		case 4:
			// State 4 is not a final state, so we can return a very specific error message. 
			emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
			emailAddressErrorMessage = "Domain part of email can contain '-' between two alphanumeric characters.\n";
			return emailAddressErrorMessage;
			
		case 5:
			// State 4 is not a final state, so we can return a very specific error message. 
			emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
			emailAddressErrorMessage = "Domain part after the '.' must be contain at least one alphanumeric character.\n";
			return emailAddressErrorMessage;
		case 6:
			// State 6 is a final state, so we can return a very specific error message or pass. 
			if(LPCharCounter < 1) {
				emailAddressErrorMessage = "Local part of email must contain at least one alphanumeric character.\n";
				return emailAddressErrorMessage;

			}
			else if(DPCharCounter < 2) {
				emailAddressErrorMessage = "Domain part of email must contain at least two alphanumeric characters.\n";
				return emailAddressErrorMessage;

			}
			else if(DPCharCounter > 63) {
				emailAddressErrorMessage = "Domain part of email must contain at most 63 characters.\n";
				return emailAddressErrorMessage;

			}
			else {
				// Valid input
				emailAddressErrorMessage = "";
				return emailAddressErrorMessage;
			}
			
		case 7:
			// State 7 is not a final state, so we can return a very specific error message. 
			emailAddressIndexofError = currentCharNdx;		// Copy the index of the current character;
			emailAddressErrorMessage = "Domain part of email can contain '-' between two alphanumeric characters.\n";
			return emailAddressErrorMessage;
			
		// default case should be reached
		default:
			return "";
		}
	}
	
	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method.  This method is now updated for the NewUsernameRecognizer Finite State Machine diagram 
	 * that requires the first character to be of alphabetic Char (A-Z, a-z) and  expands upon list of special
	 * characters from just period to period dash (minus) and underscore
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it is a String
	 * 						with a helpful description of the error
	 */
	public static String checkUsername(String input) {
		// The following are the local variable used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		usernameInput = input;			// Save a copy of the input

		// Let's ensure there is input
		if (input.length() < 4) {
			usernameErrorMessage = "Username must be at least 4 characters.\n";
			return usernameErrorMessage;
		}
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// Let's ensure the address is not too long
		if (input.length() > 16) {
			usernameErrorMessage = "Username cannot be more than 16 characters.\n";
			return usernameErrorMessage;
		}
		running = true;						// Start the loop
		System.out.println(TRACE_HEADER);

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			nextState = -1;						// Default to there is no next state		
			
			switch (state) {
			case 0: 
				// State 0 has just 1 valid transition.
				// The current character is must be checked against 62 options. If any are matched
				// the FSM must go to state 1
				// The first and the second check for an alphabet character the third a numeric
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z')) {	// Digit
					nextState = 1;
				}
				// If it is none of those characters, the FSM halts
				else { 
					running = false;
				}
				
				break;				
				// The execution of this state is finished
			
			case 1: 
				// State 1 has three valid transitions.  
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')) {	// Digit 	
					nextState = 1;
				
				}
				else if((currentChar == '-') ||(currentChar == '_') || (currentChar == '.')) {
					nextState = 2;
				}
				else {
					running = false;
				}

				break;
				// The execution of this state is finished
				
				
			case 2: 
				// State 1 has three valid transitions.  
				if ((currentChar >= 'A' && currentChar <= 'Z')|| 		// Upper case
						(currentChar >= 'a' && currentChar <= 'z') ||	// Lower case
						(currentChar >= '0' && currentChar <= '9')) {	// Digit 	
					nextState = 1;
				
				}
				else {
					running = false;
				}

				break;
			}
			
			if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next character
				// in the input and if there is one, it fetches that character and updates the 
				// currentChar.  If there is no next character the currentChar is set to a blank.
				
				moveToNextCharacter();
				
				// Move to the next state
				state = nextState;
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again

		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");

		usernameIndexOfError = currentCharNdx;		// Copy the index of the current character;
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			usernameIndexOfError = currentCharNdx;		// Copy the index of the current character;
			usernameErrorMessage = "Username must start with a letter.\n";
			return usernameErrorMessage;
			
		case 1:
			// State 1 is a final state only when the whole input has been consumed.
			usernameIndexOfError = currentCharNdx;		// Copy the index of the current character;
			if (currentCharNdx == input.length()) {
				usernameErrorMessage = "";
				return usernameErrorMessage;
			}

			usernameErrorMessage =
					"Username can only contain letters, numbers, and '-', '_', '.'.\n";
			return usernameErrorMessage;
			 
		case 2:
			// State 2 is not a final state, so we can return a very specific error message
			usernameIndexOfError = currentCharNdx;		// Copy the index of the current character;
			usernameErrorMessage = "Username can only contain '-', '_', '.' between two alphanumeric characters.\n";
			return usernameErrorMessage;

		// Default case should not be reached
		default:
			return "";
		}
		
		
	}
	
	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method. This method is now updated for the passwordRecognizer Finite State Machine diagram 
	 * that requires at least 8 and at most 64 characters that can be and include lower and upper
	 * case alphanumerics and the set 
	 * of special characters " ~ ` ! @ # $ % ^ & * ( ) _ - + { } [ ] | : , . ? / "
	 * 
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	public static String checkPassword(String input) {
		// Check to ensure that there is input to process
		if(input.length() <= 0) {
			passwordIndexOfError = 0;	// Error at first character;
			return "The password cannot be empty";
		}
		// The following are the local variable used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position


		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		passwordInput = input;			// Save a copy of the input
		running = true;						// Start the loop
		nextState = -1;						// There is no next state
		System.out.println(TRACE_HEADER);
		
		// This is the place where semantic actions for a transition to the initial state occur
		
		int passwordSize = 0;					// Initialize the password size
		
		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
				case 0:
					// State 0 has 6 valid transition that is addressed by if statements.
					if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
							(currentChar >= 'a' && currentChar <= 'z' ) ||		// Check for a-z
							(currentChar >= '0' && currentChar <= '9' ) ||		// check for 0-9
							(isSpecial(currentChar) == true)) {					// Check for special chars
						nextState = 0;
						passwordSize++;
						// Check if input was fully consumed
						if(passwordSize == input.length()) {
							nextState = 1;
						}
					}
					else {
						running = false;
					}
					break;
				
				case 1:
					running = false;
					break;
					
			}
			if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;
				
				// Is the new state a final state?  If so, signal this fact.
				if (state == 1) finalState = true;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
			
		passwordIndexOfError = currentCharNdx; // Set index of a possible error;
		passwordErrorMessage = "";
		
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			passwordErrorMessage += "Password can only contain alphanumerics and the special characters '~`!@#$%^&*()_-+{}[]|:,.?/'\n";
			return passwordErrorMessage;
		case 1:
			//State 1 is a final state
			if(passwordSize < 8) {
				passwordErrorMessage = "Password must be at least 8 characters";
				return passwordErrorMessage;
			}
			else if(passwordSize > 64) {
				passwordErrorMessage = "Password must be at most 64 characters";
				return passwordErrorMessage;
			}
			else {
				passwordErrorMessage = "";
				return passwordErrorMessage;
			}
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
	}
}
	
	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method.  This method is now updated for the NameRecognizer Finite State Machine diagram 
	 * that requires at least 3 and at most 32 upper and lower case alphabetic characters

	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	public static String checkName(String input) {
		// Let's ensure there is input
		if (input.length() <= 0) {
			nameErrorMessage = "Input cannot be empty\n";
			nameIndexOfError = 0;
			return nameErrorMessage + displayInput(input, input.length());
		}
		
		// The following are the local variable used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		nameInput = input;			// Save a copy of the input
		running = true;						// Start the loop
		nextState = -1;						// There is no next state
		System.out.println(TRACE_HEADER);
		// This is the place where semantic actions for a transition to the initial state occur

		int nameSize = 0;					// Initialize the password size
		
		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
				case 0:
					// State 0 has 2 valid transition that is addressed by if statements.
					if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' )){		// Check for a-z		
							nextState = 0;
							nameSize++;
						// Check if input was fully consumed
						if(nameSize == input.length()) {
							nextState = 1;
						}
					}
					else {
						running = false;
					}
					break;
				
				case 1:
					running = false;
					break;
					
			}
			
			if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;
				
				// Is the new state a final state?  If so, signal this fact.
				if (state == 1) finalState = true;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
	
		}
		displayDebuggingInfo();
		
		System.out.println("The loop has ended.");
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		
		nameIndexOfError = currentCharNdx; // Set index of a possible error;
		nameErrorMessage = "";
		
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			nameErrorMessage += "Name can only contain alphabetic characters.\n";
			return nameErrorMessage;
		case 1:
			//State 1 is a final state
			if(nameSize < 3) {
				nameErrorMessage = "Name must be at least 3 characters";
				return nameErrorMessage;
			}
			else if(nameSize > 32) {
				nameErrorMessage = "Name must be at most 32 characters";
				return nameErrorMessage;
			}
			else {
				nameErrorMessage = "";
				return nameErrorMessage;
			}
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
		}
	}
		
}


