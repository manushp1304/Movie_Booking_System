package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

//Manush Patel - FlickPlex Cinemas Movie Booking Application - January 21, 2021
//This class is to initially launch a JavaFX window

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//create a root of type AnchorPane and load Login.fxml
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Login.fxml"));
			//create new scene that is 700x400 pixels in dimension
			Scene scene = new Scene(root,700,400);
			//connect to the css file
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			//put the scene onto the stage
			primaryStage.setScene(scene);
			//output
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
