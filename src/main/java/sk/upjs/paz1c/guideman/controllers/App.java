package sk.upjs.paz1c.guideman.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		WelcomeController controller = new WelcomeController();
		FXMLLoader loader = new FXMLLoader(App.class.getResource("logInSignUp.fxml"));
		loader.setController(controller);
		Parent parent = loader.load();
		Scene scene = new Scene(parent);
		stage.setScene(scene);
		stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png"));
		stage.setTitle("Find or Become Guideman");
		stage.show();
	}
}
