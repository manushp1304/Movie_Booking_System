package application;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class controls the checkout screen where the user pays for their purchase
public class CheckoutController {
	
	//initialize all fields
	@FXML
	Label movie;
	
	@FXML
	Label theatre;
	
	@FXML
	Label showtime;
	
	@FXML
	Label quantity;
	
	@FXML
	Label selectedSeats;
	
	@FXML
	Label username;
	
	@FXML
	Label cardNum;
	
	@FXML
	TextField expiry;
	
	@FXML
	TextField cvv;
	
	@FXML
	Button confirmButton;
	
	@FXML
	Label confirmationError;
	
	@FXML
	Label subtotal;
	
	@FXML
	Label hst;
	
	@FXML
	Label total;
	
	//initialize variable to store values in global scope
	String movieSelect;
	String theatreSelect;
	String showtimeSelect;
	int quantitySelect;
	String seatSelect;
	String priorSeats;
	String finalName;
	String finalCardNum;
	String finalUsername;
	String finalMovieName;
	ImageView finalSelectedPoster;
	String finalSelectedPosterURL;
	
	//DecimalFormat will round the total amount to 2 decimal places
	private static final DecimalFormat df = new DecimalFormat("0.00");
	
	//this method is called when the checkout screen is called, it also recieved certain values
	public void getOrder(String selectedMovie, String selectedTheatre, String selectedShowtime, int selectedQuantity, String seats, String seatsBefore, String name, String cardNumber, String usrName, String title, ImageView selectedPoster, String selectedPosterURL) {
		
		//these values are now store in the global scope
		finalName = name;
		finalCardNum = cardNumber;
		finalUsername = usrName;
		finalSelectedPoster = selectedPoster;
		finalSelectedPosterURL = selectedPosterURL;
		movieSelect = selectedMovie;
		theatreSelect = selectedTheatre;
		showtimeSelect = selectedShowtime;
		quantitySelect = selectedQuantity;
		seatSelect = seats;
		priorSeats = seatsBefore;
		
		//update the labels on the screen with associate information
		movie.setText(movie.getText() + selectedMovie);
		theatre.setText(theatre.getText() + selectedTheatre);
		showtime.setText(showtime.getText() + selectedShowtime);
		quantity.setText(quantity.getText() + selectedQuantity);
		selectedSeats.setText(selectedSeats.getText() + seats.substring(1));
		cardNum.setText(cardNum.getText() + cardNumber);
		username.setText(username.getText() + usrName);
		
		//calculate the total amount to be paid and update the label on the screen
		double subtotalAmount = selectedQuantity*10;
		//we also need to put the total amount in 0.00 format
		subtotal.setText(subtotal.getText() + df.format(subtotalAmount));
		hst.setText(hst.getText() + df.format(subtotalAmount*0.13));
		total.setText(total.getText() + df.format(subtotalAmount + subtotalAmount*0.13));
		
	}
	
	//when the confirm payment button is pressed this method is called
	public void confirmHandler(ActionEvent evt) throws IOException, SQLException {
		
		//validate the expiry and cvv values of card
		//checkIsDigit method validates if the value entered is an integer or not
		if (checkIsDigit(expiry.getText()) && checkIsDigit(cvv.getText())){
			
			//if the value is an integer, check the length. Expiry should be 4 integers and CVV should be 3
			if(expiry.getText().length() == 4 && cvv.getText().length() == 3) {
				
				//if validation is successful, clear the error box
				confirmationError.setText("");
				//call switchToEndScreen Method
				switchToEndScreen();
			}
			
			//if the values do not comply with the associated lengths,
			else {
				//output an error message
				confirmationError.setText("***Expiry should be 4 digits and CVV should be 3");
			}
		}
		
		//if the values are not integers
		else {
			//output an error message
			confirmationError.setText("***Expiry and CVV Must Be Numbers");
		}
		
	}
	
	//this method checks if the passed values is an integer or not
	public Boolean checkIsDigit(String value) {
		//try parsing the value into an integer
		try {  
		    Integer.parseInt(value);  
		    //if successful, return true
		    return true;
		  } 
		//if there is an error, return false
			catch(NumberFormatException e){  
		    return false;  
		  }  
	}
	
	//this method is called to redirect the user to the end screen
	public void switchToEndScreen() throws IOException, SQLException {
		FXMLLoader loader = new FXMLLoader();   // we need access to the loader
		//load EndScreen.fxml
		loader.setLocation(getClass().getResource("EndScreen.fxml")); 
		//create a scene parent of AnchorPane type
		AnchorPane sceneParent = (AnchorPane)loader.load();   
		
		//load the AnchorPane onto a new Scene
		Scene scene = new Scene(sceneParent);
		//connect to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		//use the loader to get the controller from the fxml file
		EndScreenController controller = loader.getController();
		// call the payOrder method from the new controller and pass certain values
		controller.payOrder(movieSelect, theatreSelect, showtimeSelect, quantitySelect, seatSelect, priorSeats, finalName, finalCardNum, finalUsername);
		
		// get stage information
		Stage stage = (Stage)expiry.getScene().getWindow();
		
		//output the scene onto the main stage
		stage.setScene(scene);
		stage.show();
	}
	
	//this method is called to redirect the user to the Main Screen
	public void backToMainScreen(ActionEvent evt) throws IOException, SQLException{
		FXMLLoader loader = new FXMLLoader();   // we need access to the loader
		//load MainScreen.fxml
		loader.setLocation(getClass().getResource("MainScreen.fxml"));  
		//create a scene parent of AnchorPane type
		AnchorPane sceneParent = (AnchorPane)loader.load();   
		
		//load the AnchorPane onto a new Scene
		Scene scene = new Scene(sceneParent);
		//connect to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		//use the loader to get the controller from the fxml file
		MainScreenController controller = loader.getController();
		// call the initializeMain method from the new controller and pass certain values
		controller.initializeMain(finalName, finalCardNum, finalUsername);
		// get stage information
		Stage stage = (Stage)((Node)evt.getSource()).getScene().getWindow();
		
		//output the scene onto the main stage
		stage.setScene(scene);
		stage.show();
	}
	
	//this method is called to redirect the user to the previous page --- back button pressed
	public void previousPage(ActionEvent evt) throws SQLException, IOException {
		FXMLLoader loader = new FXMLLoader();   // we need access to the loader
		//load SeatSelection.fxml
		loader.setLocation(getClass().getResource("SeatSelection.fxml"));  
		
		//create a scene parent of AnchorPane type
		AnchorPane sceneParent = (AnchorPane)loader.load();   
		
		//load the AnchorPane onto a new Scene
		Scene scene = new Scene(sceneParent);
		
		//connect to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		//use the loader to get the controller from the fxml file
		SeatSelectionController controller = loader.getController();
		
		//call the getMovieSettings method from the new controller
		controller.getMovieSettings(movieSelect, theatreSelect, showtimeSelect, String.valueOf(quantitySelect), finalName, finalCardNum, finalUsername, finalMovieName, finalSelectedPoster, finalSelectedPosterURL);
		
		//get stage information
		Stage stage = (Stage)((Node)evt.getSource()).getScene().getWindow();
		
		//output the scene onto the main stage
		stage.setScene(scene);
		stage.show();
	}
}
