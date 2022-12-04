package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class controls the screen that takes information about a new movie and update the database and main screen

public class ChangeMovieController {

	// initialize fields
	String finalName;
	String finalCardNum;
	String finalUsername;

	@FXML
	TextField newMovieName;

	@FXML
	TextField newPosterURL;

	@FXML
	Button movieOne;

	@FXML
	Button movieTwo;

	@FXML
	Button movieThree;

	@FXML
	Button movieFour;

	Connection connection;
	Statement statement;

	int counter = 0;

	// create arrayList for the movieNames
	ArrayList<Button> movieNames = new ArrayList<Button>();

	// create instance of loginController for MS SQL connectivity
	LoginController loginController = new LoginController();

	// this method is called when the scene is created and loaded onto the stage to
	// recieve values from the preivous screen.
	// name, cardNum and the username is recieved with this method
	public void getAttributes(String name, String cardNum, String username) throws SQLException {

		// store in global scope
		finalName = name;
		finalCardNum = cardNum;
		finalUsername = username;

		// add the four fxml buttons onto the arrayList created above called movieNames
		movieNames.add(movieOne);
		movieNames.add(movieTwo);
		movieNames.add(movieThree);
		movieNames.add(movieFour);

		// call connectServer method in loginController to establish a connection and a
		// value of type Connection is returned.
		// save the Connection into a variable
		connection = loginController.connectServer();

		// create a statement under connection recieved above
		statement = connection.createStatement();
		// execute a SQL query for statement created above
		// this query selects all rows of data in the Movies table
		ResultSet rs = statement.executeQuery("SELECT * FROM Movies");

		// this will loop through each row of the Movies table
		while (rs.next()) {

			// store the value under the MovieName column in variable type String
			String movieName = rs.getString("MovieName");

			// grab the appropriate button in ArrayList and set the text of the button to
			// the movie name retrieved from the database
			movieNames.get(counter).setText(movieName);

			// increment the counter variable by 1 to loop through the ArrayList of buttons
			counter += 1;
		}

		// close the statement, not the database connection
		statement.close();
		rs.close();

	}

	// this method is called when a movie is selected to be replaced
	public void changeMovie(ActionEvent evt) throws SQLException, IOException {
		//get the button click and store the text present in the button
		Button clickedButton = (Button) evt.getTarget();
		String buttonLabel = clickedButton.getText();

		//get the text from the newMovie TextField
		String newMovie = newMovieName.getText();
		//get the text from the posterURL TextField
		String newPoster = newPosterURL.getText();

		//create new statement from existing database connection
		statement = connection.createStatement();
		//create an update SQL query to update a row on the Movies table in the database
		//whatever movie is being replaced, find that movie name in the table, change the name with the new movie and change the poster url with the new url
		int rs = statement.executeUpdate("UPDATE Movies SET MovieName = '" + newMovie + "',PosterURL = '" + newPoster
				+ "'WHERE MovieName = '" + buttonLabel + "'");
		//in the shows table in the database, set the new movie name wherever the old movie name was present and reset the seat map
		int rs2 = statement.executeUpdate(
				"UPDATE Shows Set Movie = '" + newMovie + "',SeatsTaken = '' WHERE Movie = '" + buttonLabel + "'");

		//call method to go back to main screen
		switchBackToMain();
	}

	//this method contains the code to switch back to the main screen
	public void switchBackToMain() throws SQLException, IOException {
		FXMLLoader loader = new FXMLLoader(); // we need access to the loader
		//load MainScreen.fxml
		loader.setLocation(getClass().getResource("MainScreen.fxml"));
		//load type AnchorPane
		AnchorPane sceneParent = (AnchorPane) loader.load();

		//load the AnchorPane onto a new scene
		Scene scene = new Scene(sceneParent);

		// use the loader to get the controller from the fxml file
		MainScreenController controller = loader.getController();
		
		// call the initializeMain method from the new controller and pass values in the global scope
		controller.initializeMain(finalName, finalCardNum, finalUsername);
		
		//connect it to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		// get stage information
		Stage stage = (Stage) movieOne.getScene().getWindow();

		//update the scene onto the new stage
		stage.setScene(scene);
		stage.show();
	}

}
