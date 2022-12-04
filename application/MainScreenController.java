package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class controls the main screen where the user selects an image to purchase tickets for.
public class MainScreenController {

	// initialize fields
	@FXML
	Label profileName;

	@FXML
	ImageView profileImage;

	Stage secondaryStage;

	@FXML
	private ImageView posterOne;

	@FXML
	private ImageView posterTwo;

	@FXML
	private ImageView posterThree;

	@FXML
	private ImageView posterFour;

	@FXML
	ImageView i;

	@FXML
	TextField adminUsername;

	@FXML
	PasswordField adminPassword;

	Connection connection;
	Statement statement;

	@FXML
	Button movieOne;
	@FXML
	Button movieTwo;
	@FXML
	Button movieThree;
	@FXML
	Button movieFour;

	// create these arraylist to iterate through movies, buttons, and posters
	ArrayList<String> movieNames = new ArrayList<String>();
	ArrayList<String> buttonValues = new ArrayList<String>();
	ArrayList<ImageView> posters = new ArrayList<ImageView>();

	// counter used for iterating
	int counter = 0;

	// initialze variables to store the passed values into the global scope
	String finalName;
	String finalCardNum;
	String finalUsername;

	// this method is called initially when this scene is loaded
	public void initializeMain(String name, String cardNum, String username) throws SQLException {
		// store the passed values into variables of the global scope
		finalUsername = username;
		finalCardNum = cardNum;
		finalName = name;

		// profileName is a label and set the text of that label to the name that was
		// passed
		profileName.setText(finalName);

		// poster(One),(Two),(Three),(Four) are ImageViews created in fxml, add these
		// ImageViews to the ArrayList of posters
		posters.add(posterOne);
		posters.add(posterTwo);
		posters.add(posterThree);
		posters.add(posterFour);

		// create and instance of the LoginController for SQL Connectivity
		LoginController loginController = new LoginController();
		// create a new connection by calling the connectServer method which will return
		// a value of type Connection
		connection = loginController.connectServer();

		// create a new statement based on the connection
		statement = connection.createStatement();
		// run the query --- select all values in the Movies table
		ResultSet rs = statement.executeQuery("SELECT * FROM Movies");

		// for every row on the Movies table
		while (rs.next()) {

			// get the value under the column MovieName, PosterURL and store the values
			String movieName = rs.getString("MovieName");
			String posterURL = rs.getString("PosterURL");

			// iterate through the poster ArrayList
			ImageView currentPoster = posters.get(counter);

			// call the loadMovie method for every movie and pass the posterURL and the
			// ImageView retrieved from the ImageView ArrayList
			loadMovie(posterURL, currentPoster);
			// add the movieName to the movieNames ArrayList
			movieNames.add(movieName);
			// increment the counter for iteration and data retrieval from the ArrayList
			counter += 1;
		}
		// close the statement and table
		statement.close();
		rs.close();

		// movie(One),(Two),(Three),(Four) are select buttons under the poster of each
		// movie. Add the buttons to the button ArrayList
		buttonValues.add("movieOne");
		buttonValues.add("movieTwo");
		buttonValues.add("movieThree");
		buttonValues.add("movieFour");

	}

	// this method is called when the select button is clicked for any movie
	public void selectFilm(ActionEvent evt) throws IOException {
		// get the button that was clicked and search for it's id
		Button selectedButton = (Button) evt.getTarget();
		String selectedButtonId = selectedButton.getId();

		// search through the buttons ArrayList for the given id(take the index) and get
		// the item in the movieNames ArrayList at that index
		String selectedMovie = movieNames.get(buttonValues.indexOf(selectedButtonId));
		// search through the buttons ArrayList for the given id(take the index) and get
		// the item in the posters ArrayList at that index
		ImageView selectedPoster = posters.get(buttonValues.indexOf(selectedButtonId));
		// get the url of the image of the imageview of the poster of the movie that the
		// user has selected
		String selectedPosterURL = selectedPoster.getImage().getUrl();

		
		FXMLLoader loader = new FXMLLoader(); // we need access to the loader
		//load MovieSettings.fxml
		loader.setLocation(getClass().getResource("MovieSettings.fxml"));
		//create a new sceneParent of type AnchorPane
		AnchorPane sceneParent = (AnchorPane) loader.load();

		//create new scene using the sceneParent
		Scene scene = new Scene(sceneParent);

		// use the loader to get the controller from the fxml file
		MovieSettingsController controller = loader.getController();
		// call the getSelectedMovie method from the new controller and pass these values
		controller.getSelectedMovie(selectedMovie, selectedPoster, selectedPosterURL, finalName, finalCardNum,
				finalUsername);
		//connect to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		//locate the stage by tracking the button click
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		//put the scene onto the stage
		stage.setScene(scene);
		//output
		stage.show();

	}

	//this method is called when the movie posters need to outputed onto the screen and the url and poster ImageView is passed
	public void loadMovie(String url, ImageView poster) {
		//create new image with given dimensions
		Image image = new Image(url, 124, 170, false, true);
		//set the image of the ImageView according to the url and dimensions above
		poster.setImage(image);
	}

	//this method is called when the logout button is pressed on this scene
	public void logout(ActionEvent evt) throws IOException {
		FXMLLoader loader = new FXMLLoader(); // we need access to the loader
		//load Login.fxml
		loader.setLocation(getClass().getResource("Login.fxml"));
		//create a sceneParent
		AnchorPane sceneParent = (AnchorPane) loader.load();

		//create new scene using the sceneParent created above
		Scene scene = new Scene(sceneParent);

		//connect it to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		//locate the stage by tracking the button click
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		//put the scene onto the stage
		stage.setScene(scene);
		//output
		stage.show();
	}

	//this method is called when the admin button is clicked to change movies on display
	public void adminView(ActionEvent evt) throws IOException {
		FXMLLoader loader = new FXMLLoader(); // we need access to the loader
		//load AdminCredentials.fxml
		loader.setLocation(getClass().getResource("AdminCredentials.fxml"));
		//create new sceneParent of type AnchorPane
		AnchorPane sceneParent = (AnchorPane) loader.load();

		//create new scene using the sceneParent created above
		Scene scene = new Scene(sceneParent);

		// use the loader to get the controller from the fxml file
		AdminCredentialsController controller = loader.getController();
		// call the getAttributes method and pass these values
		controller.getAttributes(finalName, finalCardNum, finalUsername);
		
		//connect it to the css file
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		//locate the stage by tracking the button click
		Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();

		//put the scene onto the stage
		stage.setScene(scene);
		
		//output
		stage.show();

	}

}
