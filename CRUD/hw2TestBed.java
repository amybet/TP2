package CRUD;

public class hw2TestBed {
    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) {
        System.out.println("______________________________________");
        System.out.println("\nHomework 2 Validation Testing");

        //Title tests
        performTest(1, "Please help with question 3.1!", "Title", true); //this passes since the title is <= 32 characters
        performTest(2, "Please help with question 3.1!!!", "Title", true); //this passes since the title is = 32 characters
        performTest(3, "Please help with question 3.1!!!!!!!", "Title", false); //this does not pass since the title >32 characters
        performTest(4, "", "Title", false); //this does not pass since the title is null

        //Content Tests
        performTest(5, "I am having difficulties with this problem, if someone could help me out please reply: ", "Content", true); //this passes since the content is <= 500 characters
        performTest(6, "A".repeat(500), "Content", true); //this passes since the content is = 500 characters
        performTest(7, "A".repeat(501), "Content", false); //this does not pass since the content is > 500 characters
        performTest(8, "", "Content", false); //this does not pass since the content is null

        System.out.println("____________________________________________________________________________");
        System.out.println("\nTests Passed: " + numPassed);
        System.out.println("Tests Failed: " + numFailed);
    }

    private static void performTest(int caseNum, String input, String type, boolean expectedPass) {
        System.out.println("____________________________________________________________________________");
        System.out.println("Test case " + caseNum + " [" + type + "]: \"" + input + "\"");
        
        boolean actualPass = validateInput(input, type);

        System.out.println("Should this case pass? : " + expectedPass);
        System.out.println("Does this case pass? : " + actualPass);
        
        if (actualPass == expectedPass) {
            System.out.println("***Success*** Result matched expectations.");
            numPassed++;
        } else {
            System.out.println("***Failure*** Expected " + expectedPass + " but got " + actualPass);
            numFailed++;
        }
    }

    private static boolean validateInput(String input, String type) {
        if (input == null || input.trim().isEmpty()) {return false;}
        if (type.equals("Title")) {return input.length() <= 32;}
        if (type.equals("Content") || type.equals("Reply")) {return input.length() <= 500;}
        return true;
    }
}