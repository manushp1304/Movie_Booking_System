package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class controls the seat selection of a user's booking. It contains a seat map where taken seats are marked red and selected seats are marked green.
public class SeatSelectionController {

	// initialize fields
	@FXML
	GridPane seatScreen;

	@FXML
	Label movieLabel;

	@FXML
	Label theatreLabel;

	@FXML
	Label showtimeLabel;

	@FXML
	Label quantityLabel;

	@FXML
	Label seatSelectionError;

	Connection connection;

	// initialize variables to store values into global scope
	String selectedMovie;
	String selectedTheatre;
	String selectedShowtime;
	String selectedQuantity;
	int selectedQuantityInt;
	String takenSeatsString;

	Boolean seatTakenBoolean = false;
	int selectSeatsCounter = 0;
	String addTicketsToDatabase = "";

	ArrayList<String> takenSeatsArrayList = new ArrayList<String>();
	String finalName;
	String finalCardNum;
	String finalUsername;
	String finalMovie;
	ImageView finalSelectedPoster;
	String finalSelectedPosterURL;

	public void getMovieSettings(String passedMovie, String passedTheatre, String passedShowtime, String passedQuantity,
			String name, String cardNum, String username, String title, ImageView selectedPoster,
			String selectedPosterURL) throws SQLException {

		// store values into the global scope
		finalName = name;
		finalCardNum = cardNum;
		finalUsername = username;
		selectedMovie = passedMovie;
		selectedTheatre = passedTheatre;
		selectedShowtime = passedShowtime;
		selectedQuantity = passedQuantity;
		finalMovie = title;
		finalSelectedPoster = selectedPoster;
		finalSelectedPosterURL = selectedPosterURL;

		// add text to the labels present on the scene
		movieLabel.setText(movieLabel.getText() + selectedMovie);
		theatreLabel.setText(theatreLabel.getText() + selectedTheatre);
		showtimeLabel.setText(showtimeLabel.getText() + selectedShowtime);
		quantityLabel.setText(quantityLabel.getText() + selectedQuantity);

		// create an instance of LoginController for SQL Connectivity
		LoginController controller = new LoginController();
		// call the connectServer method in LoginController which will return a value of
		// type connection
		connection = controller.connectServer();

		// create a new statement based on the connection we created
		Statement statement = connection.createStatement();
		// execute the query which selected all values in the table Shows
		ResultSet rs = statement.executeQuery("SELECT * FROM Shows");

		// loop through every row on the table
		while (rs.next()) {
			// store the values under the provided column name
			String movie = rs.getString("Movie");
			String theatre = rs.getString("Theatre");
			String showtime = rs.getString("Showtime");
			String seatsTaken = rs.getString("SeatsTaken");

			// when the appropriate row is found in the table(show)
			if (selectedMovie.equals(movie) && selectedTheatre.equals(theatre) && selectedShowtime.equals(showtime)) {
				// store the seats taken for that show in a string
				takenSeatsString = seatsTaken;
				// split the string by commas and store the seats in a Array of Strings
				String[] tempArray = takenSeatsString.split(",");
				// add the seats to an ArrayList because we want to add to the list of taken
				// Seats
				for (int i = 0; i < tempArray.length; i++) {
					takenSeatsArrayList.add(tempArray[i]);
				}

			}
		}

		// get all the children on the seatScreen and store it in an Observable List
		ObservableList<Node> seats = seatScreen.getChildren();
		// for each seat in the seat map
		for (Node node : seats) {
			// iterate through the ArrayList
			for (int i = 0; i < takenSeatsArrayList.size(); i++) {
				// if seat is present in the taken seats Array
				if (node.getId().equals(takenSeatsArrayList.get(i))) {
					// add the red class to the node so that taken seats turn red
					node.getStyleClass().add("red");
				}
			}
		}
	}

	// this method is called when individual seats are clicked to select
	public void seatSelectHandler(ActionEvent evt) {

		// get the button information that was clicked
		Button clickedButton = (Button) evt.getTarget();
		String ButtonLabel = clickedButton.getText();
		// convert the quantity selected to an integer
		selectedQuantityInt = Integer.parseInt(selectedQuantity);

		// only allow this iteration for a specified number of times(quantity of tickets
		// selected)
		if (selectSeatsCounter < selectedQuantityInt) {
			// iterate through the taken seats ArrayList
			for (int i = 0; i < takenSeatsArrayList.size(); i++) {
				// if the clicked seat is in the Arraylist
				if (clickedButton.getText().equals(takenSeatsArrayList.get(i))) {
					// set the text of the errorMessageBox to an error message
					seatSelectionError.setText("***This Seat is Taken. Please Press Clear and Pick Again.");
					// set a Boolean value and break the loop
					seatTakenBoolean = true;
					break;
				}

			}

			// if the clicked seat is available
			if (seatTakenBoolean == false) {
				// if conditions are met and the ticket is allowed to be selected
				if (addTicketsToDatabase.contains(ButtonLabel) == false) {
					// increment the variable to continue iteration
					selectSeatsCounter += 1;
					// add a class to the button to make it green when selected
					clickedButton.getStyleClass().add("green");
					// store the seat in a variable
					addTicketsToDatabase += "," + ButtonLabel;
				}
				// if one seat is clicked more than once
				else {
					// seat an error message
					seatSelectionError.setText("***Seat Is Already Chosen");
				}

			}
		}
		// when the user tries to select more seats than the quantity of tickets they
		// want to purchase
		else {
			// output an error message
			seatSelectionError.setText("***" + selectedQuantity + " Tickets ONLY");
		}
	}

	// when the clear button is pressed
	public void clearHandler(ActionEvent evt) {
		// reset boolean value
		seatTakenBoolean = false;
		// clear the whatever was selected
		addTicketsToDatabase = "";
		// reset the counter of seats
		selectSeatsCounter = 0;
		// clear the error message label
		seatSelectionError.setText("");

		// iterate through each button on the screen
		ObservableList<Node> buttons = seatScreen.getChildren();
		for (Node node : buttons) {
			// remove the green class to show that the selection has been cleared
			node.getStyleClass().remove("green");
		}
	}

	// this method is called when the submit button is pressed
	public void submitHandler(ActionEvent evt) throws IOException, SQLException {
		// when the user has selected seats fewer than the number if tickets they want
		// to purchase
		if (selectSeatsCounter != selectedQuantityInt) {
			// output error message
			seatSelectionError.setText("***Please Select " + selectedQuantity + " Seats");
		}

		// else move onto the next scene
		else {
			FXMLLoader loader = new FXMLLoader(); // we need access to the loader
			// load Checkout.fxml
			loader.setLocation(getClass().getResource("Checkout.fxml"));
			// create sceneParent of type AnchorPane
			AnchorPane sceneParent = (AnchorPane) loader.load();

			// create new scene using the sceneParent created above
			Scene scene = new Scene(sceneParent);
			// attach it to the css file
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// use the loader to get the controller from the fxml file
			CheckoutController controller = loader.getController();
			// call the getOrder method from the new controller and pass values
			controller.getOrder(selectedMovie, selectedTheatre, selectedShowtime, selectedQuantityInt,
					addTicketsToDatabase, takenSeatsString, finalName, finalCardNum, finalUsername, finalMovie,
					finalSelectedPoster, finalSelectedPosterURL);

			// locate the stage by tracking the button click
			Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

			// put the scene onto the stage
			stage.setScene(scene);
			// output
			stage.show();
		}
	}

	// this method is called when the back button is clicked and the user should be
	// directed to the previous screen
	public void previousPage(ActionEvent evt) throws IOException {
		FXMLLoader loader = new FXMLLoader(); // we need access to the loader
		// load MovieSettings.fxml
		loader.setLocation(getClass().getResource("MovieSettings.fxml"));

		// create sceneParent of type AnchorPane
		AnchorPane sceneParent = (AnchorPane) loader.load();

		// create new scene using the sceneParent
		Scene scene = new Scene(sceneParent);

		// use the loader to get the controller from the fxml file
		MovieSettingsController controller = loader.getController();

		// call the getSelectedMovie method from the new controller and pass these
		// values
		controller.getSelectedMovie(finalMovie, finalSelectedPoster, finalSelectedPosterURL, finalName, finalCardNum,
				finalUsername);

		// connect the scene to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		// locate the Stage by tracking the button click
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		// put the scene onto the stage
		stage.setScene(scene);
		// output
		stage.show();
	}

}