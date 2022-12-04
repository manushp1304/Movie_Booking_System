package application;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class is for controlling the admin credentials scene when an admin wants to replace a movie after it is outdated

public class AdminCredentialsController {

	// initialize fields
	@FXML
	TextField adminUsername;

	@FXML
	PasswordField adminPassword;

	@FXML
	Label errorBox;

	String finalName;
	String finalCardNum;
	String finalUsername;

	// this method is called initially when admin credentials scene is create. Name,
	// cardNum, and username values are recieved
	public void getAttributes(String name, String cardNum, String username) {

		// store in global scope
		finalName = name;
		finalCardNum = cardNum;
		finalUsername = username;

	}

	// this method is called when the submit button is clicked
	public void submit(ActionEvent evt) throws IOException, SQLException {

		// Manush Patel is the only user allowed as an admin to change movies. Check if
		// the user is Manush Patel
		if (adminUsername.getText().equals("Manushpatel") && adminPassword.getText().equals("Manush@13")) {

			// if username is verified, load new scene - ChangeMovie.fxml - this scene is
			// where the movies are changed.
			FXMLLoader loader = new FXMLLoader(); // we need access to the loader
			//load ChangeMovie.fxml
			loader.setLocation(getClass().getResource("ChangeMovie.fxml"));
			AnchorPane sceneParent = (AnchorPane) loader.load();

			Scene scene = new Scene(sceneParent);

			// use the loader to get the controller from the fxml file
			ChangeMovieController controller = loader.getController();

			// call the getAttributes method from the new controller
			controller.getAttributes(finalName, finalCardNum, finalUsername);

			// connect to the CSS file
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// get stage information via the button click
			Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

			// show the scene onto the main stage
			stage.setScene(scene);
			stage.show();
		}

		// if user is not verified, output error message
		else {
			// empty error box(Label on fxml file) will output an error message
			errorBox.setText("***Admin Not Recognized Try Again");
			// the textfields will clear for the user to retry
			adminUsername.clear();
			adminPassword.clear();
		}
	}
	
	public void previousPage(ActionEvent evt) throws SQLException {
		//load the previous scene.fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
		
		//create a root of type AnchorPane
		AnchorPane root = null;
		try {
			root = (AnchorPane) loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create new scene that is 700x400 pixels in dimension
		Scene scene = new Scene(root, 700, 400);

		// get the controller of the new scene
		MainScreenController controller = loader.getController();

		// connect it to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		//	locate the stage by tracking the button click
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		// call initializeMain method on the MainScreen and pass these values
		controller.initializeMain(finalName, finalCardNum, finalUsername);

		// put the scene onto the stage
		stage.setScene(scene);
		//output
		stage.show();
	}
}
