package application;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class controls the Movie Settings where the user selects theatre, showtime, and quantity of their booking
public class MovieSettingsController implements Initializable {

	// initialize fields
	@FXML
	private Label nameOfMovie;

	@FXML
	private ImageView selectedMoviePoster;

	// this is for dropdowns
	@FXML
	private ComboBox<String> theatreComboBox;

	@FXML
	private ComboBox<String> quantityComboBox;

	@FXML
	private ComboBox<String> showtimeComboBox;

	// initialize variables to store into global scope
	String selectedMovie;
	String finalName;
	String finalCardNum;
	String finalUsername;
	String finalMovie;
	ImageView finalSelectedPoster;
	String finalSelectedPosterURL;

	//this method is called initially when this scene is put onto the stage
	public void getSelectedMovie(String movie, ImageView selectedPoster, String selectedPosterURL, String name,String cardNum, String username) {
		//store the values into the global scope
		finalName = name;
		finalCardNum = cardNum;
		finalUsername = username;
		selectedMovie = movie;
		finalMovie = movie;
		finalSelectedPoster = selectedPoster;
		finalSelectedPosterURL = selectedPosterURL;
		//crate a new image to output the movie poster onto the settings scene
		Image image = new Image(selectedPosterURL, 248, 340, false, true);
		//set the image of the ImageView
		selectedMoviePoster.setImage(image);
		//nameOfMovie is a Label and we want to set it's text to the actual movie the user has selected
		nameOfMovie.setText(movie);
	}

	//this method is used to create the options of a combobox(dropdown)
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//set the options on the three settings comboxes
		theatreComboBox.setItems(FXCollections.observableArrayList("Winston Churchill & VIP", "Oakville & VIP", "Don Mills & VIP"));
		showtimeComboBox.setItems(FXCollections.observableArrayList("6:15pm", "8:30pm", "10:45pm"));
		quantityComboBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8"));
	}

	//this method is called when the submit settings button is pressed
	public void selectSettings(ActionEvent evt) throws IOException, SQLException {
		//get the values selected in each combobox
		String selectedTheatre = theatreComboBox.getValue();
		String selectedShowtime = showtimeComboBox.getValue();
		String selectedQuantity = quantityComboBox.getValue();

		FXMLLoader loader = new FXMLLoader(); // we need access to the loader
		//load SeatSelection.fxml
		loader.setLocation(getClass().getResource("SeatSelection.fxml"));
		//create sceneParent of type AnchorPane
		AnchorPane sceneParent = (AnchorPane) loader.load();

		//create new scene using the sceneParent created above
		Scene scene = new Scene(sceneParent);
		
		//connect it to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		// use the loader to get the controller from the fxml file
		SeatSelectionController controller = loader.getController();
		// call the getMovieSettings method from the new controller and pass these values
		controller.getMovieSettings(selectedMovie, selectedTheatre, selectedShowtime, selectedQuantity, finalName,
				finalCardNum, finalUsername, finalMovie, finalSelectedPoster, finalSelectedPosterURL);

		//locate the stage by tracking the button click
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		//put the scene onto the stage
		stage.setScene(scene);
		//output
		stage.show();

	}

	//this method is called when the back button is pressed to direct the user to the previous scene
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
