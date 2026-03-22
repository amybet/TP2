/**
 * <p> Title: FoundationsF25 Module </p>
 * <p> Description: The main module configuration for the Foundations application. 
 * This file defines the required system dependencies, including JavaFX for the 
 * graphical user interface and java.sql for database. </p>
 * 
 */
module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
