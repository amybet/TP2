package entityClasses;
/*******
 * <p> Title: InputRecognizer Class </p>
 * 
 * <p> Description: This InputRecognizer class has functions for validating the input for emails, passwords, usernames, and names. </p>
 * 
 * 
 * @author Nicholas Hamilton
 * 
 * 
 */ 
import java.sql.*;



public class InvitationCode {
	/**
	 * <p> Title: FSM-translated InptuRecognizer. </p>
	 * 
	 * <p> Description: This represents a basic InvitationCode located in the database, with associated information. </p>
	 * 
	 * 
	 * @author Nicholas Hamilton
	 * 
	 * @version 0.00		2018-02-04	Initial baseline 
	 * 										
	 * 
	 */
    private String code;
    private Timestamp expiresAt;
    private String email;

    public InvitationCode(String code, Timestamp expiresAt, String email) {
        this.code = code;
        this.expiresAt = expiresAt;
        this.email = email;
    }

    public String getCode() { return code; }
    public Timestamp getExpiresAt() { return expiresAt; }
    public String getEmail() { return email; }
}
