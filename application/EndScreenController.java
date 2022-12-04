package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class controls the end screen which is simply a summary of the purchase. From here the user can logout or go back to the Main Screen
public class EndScreenController {

	// initialize fields
	@FXML
	Label movie;

	@FXML
	Label theatre;

	@FXML
	Label showtime;

	@FXML
	Label quantity;

	@FXML
	Label seats;

	@FXML
	Label subtotal;

	@FXML
	Label hst;

	@FXML
	Label total;

	// initialize variables to stores values in gloabl scope
	String finalName;
	String finalCardNum;
	String finalUsername;
	String finalQuantitySelect;
	String finalSeatsSelect;
	String totalAmount;

	// DecimalFormat is used to convert the total into 0.00 format
	private static final DecimalFormat df = new DecimalFormat("0.00");

	// this method is called when this scene is created and loaded onto the stage.
	// Certain values are passed in.
	public void payOrder(String movieSelect, String theatreSelect, String showtimeSelect, int quantitySelect,
			String seatsSelect, String beforeSeats, String name, String cardNum, String username) throws SQLException {

		// store values in global scope
		finalCardNum = cardNum;
		finalName = name;
		finalUsername = username;

		// update the Labels onthe screen by adding associated values
		movie.setText(movie.getText() + movieSelect);
		theatre.setText(theatre.getText() + theatreSelect);
		showtime.setText(showtime.getText() + showtimeSelect);
		quantity.setText(quantity.getText() + quantitySelect);
		seats.setText(seats.getText() + seatsSelect.substring(1));

		// convert quantity to string
		finalQuantitySelect = String.valueOf(quantitySelect);
		// get the sub string of seatsSelect because it includes an extra comma
		finalSeatsSelect = seatsSelect.substring(1);

		// calculate the subtotal
		double subtotalAmount = quantitySelect * 10;
		// get the subtotal, convert to 0.00 formate and update the Labels on the fxml
		// by adding their associated value
		subtotal.setText(subtotal.getText() + df.format(subtotalAmount));
		hst.setText(hst.getText() + df.format(subtotalAmount * 0.13));
		total.setText(total.getText() + df.format(subtotalAmount + subtotalAmount * 0.13));
		// store total amount to add into database
		totalAmount = df.format((subtotalAmount * 1.13));

		// call addSeatsToDatabase method add pass all the things that need to be added
		// into the database
		addSeatsToDatabase(movieSelect, theatreSelect, showtimeSelect, beforeSeats, seatsSelect);
	}

	// this method adds values into the database --- update seats on existing movies
	// and add to a user tracking table
	public void addSeatsToDatabase(String movie, String theatre, String showtime, String priorSeats, String seatsToAdd)
			throws SQLException {

		// create instance of LoginController to connect to the server
		LoginController controller = new LoginController();

		// call the connectServer method in the LoginController class which will return
		// a value of type Connection
		Connection connection = controller.connectServer();

		// get the taken seats and add the seats that were just booked in a variable
		String changeInTable = priorSeats + seatsToAdd;

		// takeout the last character because it has an extra comma
		seatsToAdd = seatsToAdd.substring(0, seatsToAdd.length() - 1);

		// create a statement based on the connection we created
		Statement statement = connection.createStatement();

		// execute the SQL query and update the taken seats in the shows table for the
		// specific movie, theatre, and showtime
		int rs = statement.executeUpdate("UPDATE Shows set SeatsTaken= '" + changeInTable + "' WHERE Movie= '" + movie
				+ "' AND Theatre= '" + theatre + "' AND Showtime= '" + showtime + "'");
		// execute the SQL query and insert a new row in the UserHistory table for every
		// purchase
		int rs2 = statement.executeUpdate(
				"INSERT INTO UserHistory (Username, Movie, Theatre, Showtime, NumOfTickets, Seats, AmountPaid) Values ('"
						+ finalUsername + "', '" + movie + "', '" + theatre + "','" + showtime + "', '"
						+ finalQuantitySelect + "', '" + finalSeatsSelect + "', '" + totalAmount + "')");

	}

	// this method is called to redirect the user to the Login Screen
	public void backToLogin(ActionEvent evt) throws IOException {
		FXMLLoader loader = new FXMLLoader(); // we need access to the loader
		// load Login.fxml
		loader.setLocation(getClass().getResource("Login.fxml"));
		// create a scene parent of AnchorPane type
		AnchorPane sceneParent = (AnchorPane) loader.load();

		// load the AnchorPane onto a new Scene
		Scene scene = new Scene(sceneParent);

		// connect to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		// get stage information
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		// output the scene onto the main stage
		stage.setScene(scene);
		stage.show();
	}

	// this method is called to redirect the user to the Main Screen
	public void backToMain(ActionEvent evt) throws IOException, SQLException {
		FXMLLoader loader = new FXMLLoader(); // we need access to the loader
		// load MainScreen.fxml
		loader.setLocation(getClass().getResource("MainScreen.fxml"));

		// create a scene parent of AnchorPane type
		AnchorPane sceneParent = (AnchorPane) loader.load();

		// load the AnchorPane onto a new Scene
		Scene scene = new Scene(sceneParent);

		// connect to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		// use the loader to get the controller from the fxml file
		MainScreenController controller = loader.getController();

		// call the initializeMain method from the new controller and pass certain
		// values
		controller.initializeMain(finalName, finalCardNum, finalUsername);

		// get stage information
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		// output the scene onto the main stage
		stage.setScene(scene);
		stage.show();
	}

}
