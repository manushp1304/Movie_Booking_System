package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class controls the Login/Registration system by iterating through the database to login a user or create a user
public class LoginController {

	// initialize fields
	@FXML
	Button switchToRegister;

	// Login Page
	@FXML
	private TextField LoginUsername;

	@FXML
	PasswordField LoginPassword;

	@FXML
	Label InvalidRegistration;

	@FXML
	Label InvalidLogin;

	@FXML
	Label brand;

	// Register Page
	@FXML
	TextField regName;
	@FXML
	TextField regUsername;
	@FXML
	TextField regCard;
	@FXML
	TextField regPassword;

	// Main Screen
	String finalUsername;
	String finalPassword;
	String finalName;
	String finalCardNum;

	Boolean serverConnected = false;
	String url;
	String username;
	String password;
	Connection connection;

	Boolean loginSuccessful;
	Boolean invalidRegistration = false;

	Boolean addUserToDatabase = true;
	String message;

	// this method connects to the server I have established initially and value of
	// type Connection is returned when method is called
	public Connection connectServer() throws SQLException {
		// jdbc - JAVA DATABASE CONNECTIVITY - localhost;portnumber;name of database
		url = "jdbc:sqlserver://localhost:1433;databaseName=master";
		// my database credentials
		username = "sa";
		password = "Manush@13";

		// create connection with the url, username, password
		connection = DriverManager.getConnection(url, username, password);
		return (connection);
	}

	// the method is called when the login submit button is pressed
	public void loginClickHandler(ActionEvent evt) throws SQLException {
		// if a connection has not been established create one(if the user switches back
		// between register and login - no connection is made)
		if (serverConnected == false) {
			connectServer();
			serverConnected = true;
		}

		// create a statement on the connection that we just created
		Statement statement = connection.createStatement();
		// execute the query below to get all the data from the table Persons
		ResultSet rs = statement.executeQuery("select * from Persons");

		// get the text on the username and password textfields
		String enteredUsername = LoginUsername.getText();
		String enteredPassword = LoginPassword.getText();

		// loop through every row on the Persons table
		while (rs.next()) {
			// get the values under the specified column name
			String username = rs.getString("Username");
			String password = rs.getString("Password");
			String firstname = rs.getString("Firstname");
			String cardNum = rs.getString("Card#");

			// if the entered username and entered password match a user in the table
			if (enteredUsername.equals(username) && enteredPassword.equals(password)) {
				// set boolean variable to true
				loginSuccessful = true;
				// clear the textfields
				LoginUsername.clear();
				LoginPassword.clear();
				// store variables ins global scope
				finalUsername = username;
				finalPassword = password;
				finalName = firstname;
				finalCardNum = cardNum;
				// break the loop once user is recognized
				break;
			}

			// if user is not recognized
			else {
				// set boolean variable to false and clear the textfields
				loginSuccessful = false;
				LoginUsername.clear();
				LoginPassword.clear();
			}

		}

		// close the statement and table we opened
		statement.close();
		rs.close();

		// if user is recognized
		if (loginSuccessful) {
			// set the error box to empty and call method switchToMainScreen
			InvalidLogin.setText("");
			switchToMainScreen();
		}
		// if user is not recognized
		else {
			// output an error message in the error Label
			InvalidLogin.setText("Invalid Login/Password");
		}
	}

	// this method is called when the register submit is pressed
	public void registerClickHandler() throws SQLException {
		// if not yet connected to the server, connect to the server as necessary
		if (serverConnected == false) {
			connectServer();
			serverConnected = true;
		}

		// create new statement for the connection we have made previously
		Statement statement = connection.createStatement();
		// execute the following query which selects all the data from table known as
		// Persons
		ResultSet rs = statement.executeQuery("select * from Persons");

		// get the text values in the register text-fields
		String enteredUsername = regUsername.getText();
		String enteredPassword = regPassword.getText();
		String enteredName = regName.getText();
		String enteredCard = regCard.getText();

		// loop through each row in the Persons table
		while (rs.next()) {
			// get the username of each user in the database
			String username = rs.getString("Username");

			// if the enteredUsername exists in the database
			if (enteredUsername.equals(username)) {
				// set boolean variable to true and break
				invalidRegistration = true;
				break;
			}
		}

		// close the statement and table as we have already retrieved our values
		statement.close();
		rs.close();

		// if the registration is invalid
		if (invalidRegistration == true) {
			// set the errorBoxMessage to a specific error message
			message = "User already exists. Try Again";
			// clear the textfields
			regUsername.clear();
			regPassword.clear();
			regName.clear();
			regCard.clear();
			// reset the boolean variables to allow another input
			invalidRegistration = false;
		}

		// if user does not already exist in the database, check for further attributes
		else {
			// clear the fields
			regUsername.clear();
			regPassword.clear();
			regName.clear();
			regCard.clear();

			// if the username exceeds 15 characters
			if (enteredUsername.length() > 15) {
				// output error message and reset boolean variable
				message = "***Username must be less than 15 characters";
				addUserToDatabase = false;
			}

			// if the password exceeds 15 characters
			if (enteredPassword.length() > 15) {
				// output error message and reset boolean variable
				message = "***Password must be less than 15 characters";
				addUserToDatabase = false;
			}

			// if the name entered exceeds 15 characters
			if (enteredName.length() > 15) {
				// output error message and reset boolean variable
				message = "***Name must be less than 15 characters";
				addUserToDatabase = false;
			}

			// if the card number is 16 characters
			if (enteredCard.length() == 16) {
				// check if the 16 characters are integers, if numbers are not integer values
				if (checkCardifDigit(enteredCard) == false) {
					// output error message and reset boolean variable
					message = "***Card# must be numbers";
					addUserToDatabase = false;
				}
			}
			// if the card number is not 16 characters
			else {
				// output error message and reset boolean variable
				message = "***Card# must be 16 numbers";
				addUserToDatabase = false;
			}
		}

		// a boolean variable was initially set to true and turns false if the
		// conditions are not met, if it stays true
		if (addUserToDatabase == true) {
			// store variables in global scope
			finalUsername = enteredUsername;
			finalPassword = enteredPassword;
			finalName = enteredName;
			finalCardNum = enteredCard;
			// call method addToDatabase and pass the required information of the user
			addToDatabase(finalUsername, finalPassword, finalName, finalCardNum);
			// clear the error message box
			InvalidRegistration.setText("");
			// call method switchToMainScreen
			switchToMainScreen();
		}

		// if the boolean variable mentioned above was altered by any of the conditions
		if (addUserToDatabase == false) {
			// set the text on the error message box to the associated message and reset the
			// boolean variable if the user enters again
			InvalidRegistration.setText(message);
			addUserToDatabase = true;
		}
	}

	// if the user switches to register from the log in page
	public void switchToRegister(ActionEvent evt) {
		// load the Register.fxml file
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
		//create the root
		AnchorPane root = null;
		try {
			root = (AnchorPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create new scene that is 700x400 pixels in dimension
		Scene scene = new Scene(root, 700, 400);
		// connect to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		//locate the stage by tracking the button click
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
		// load the scene onto the stage
		stage.setScene(scene);
		// output
		stage.show();
	}

	public void switchToLogin(ActionEvent evt) {
		// load the Login.fxml file
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
		//create the root
		AnchorPane root = null;
		try {
			root = (AnchorPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create new scene that is 700x400 pixels in dimension
		Scene scene = new Scene(root, 700, 400);
		// connect to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		//locate the stage by tracking the button click
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
		// load the scene onto the stage
		stage.setScene(scene);
		// output
		stage.show();

	}

	//this method is called to verify if the card number entered is a numerical value
	public boolean checkCardifDigit(String value) {
		try {
			//convert the string into a long type variable
			Long.parseLong(value);
			//return true if it successfully converts the string
			return true;
			//if there is a NumberFormatException(Error with number format) because the entered value is not numerical
		} catch (NumberFormatException e) {
			//return false
			return false;
		}
	}

	//this method is called when the user needs to be added to the database
	//the user is not already in the database and registration conditions are verified
	public void addToDatabase(String username, String password, String name, String card) throws SQLException {
		
		//create a new statement based on the connection we created while connecting to the server
		Statement statement = connection.createStatement();
		//update the existing table(Persons) by add a new row with the entered values of username, passowrd, name, and card number
		statement.executeUpdate("INSERT INTO Persons (Username, Password, Firstname, Card#) VALUES ('" + username
				+ "', '" + password + "', '" + name + "', '" + card + "');");

	}
	
	//this method is called when the user is added to the database or the login was successful and the user needs to be directed to the main screen
	public void switchToMainScreen() throws SQLException {
		// set the FXMLLoader to the new fxml file of the Main Screen
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
		//create the root
		AnchorPane root = null;
		try {
			root = (AnchorPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create new scene that is 700x400 pixels in dimensions
		Scene scene = new Scene(root, 700, 400);

		// get the controller of the new scene
		MainScreenController controller = loader.getController();

		// connect it to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		// locate the stage by tracking the logo's scene and the scene's stage
		Stage stage = (Stage) brand.getScene().getWindow();
		// put the scene onto the stage
		stage.setScene(scene);
		//call the initializeMain method in the new controller and pass provided values
		controller.initializeMain(finalName, finalCardNum, finalUsername);
		//output
		stage.show();

	}

}
