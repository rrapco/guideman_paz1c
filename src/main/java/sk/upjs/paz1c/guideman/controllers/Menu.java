package sk.upjs.paz1c.guideman.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public enum Menu {
	
	INSTANCE;
	
	public void openMyProfile(Button button) {
		System.out.println("profile");
		
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyProfileController.class.getResource("myProfile2.fxml"));
            Stage stage = new Stage();
            fxmlLoader.setController(new MyProfileController());
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Guideman");
            stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png")); 
            stage.setScene(scene);
            stage.show();
            button.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void openMyTours(Button button) {
		System.out.println("tours");
		
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyToursController.class.getResource("myTours.fxml"));
            Stage stage = new Stage();
            fxmlLoader.setController(new MyToursController());
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Guideman");
            stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png")); 
            stage.setScene(scene);
            stage.show();
            button.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void openSearchTour(Button button) {
		System.out.println("search");
		
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(SearchTourController.class.getResource("searchTour.fxml"));
            Stage stage = new Stage();
            fxmlLoader.setController(new SearchTourController());
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Guideman");
            stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png")); 
            stage.setScene(scene);
            stage.show();
            button.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void openCreateTour(Button button) {
		System.out.println("profile");
		
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(CreateTourController.class.getResource("createTour.fxml"));
            Stage stage = new Stage();
            fxmlLoader.setController(new CreateTourController());
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Guideman");
            stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png")); 
            stage.setScene(scene);
            stage.show();
            button.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void logOut(Button button) {
		System.out.println("logout");

		try {
            FXMLLoader fxmlLoader = new FXMLLoader(WelcomeController.class.getResource("logInSignUp.fxml"));
            LoggedUser.INSTANCE.setLoggedUser(null);
            ShowTour.INSTANCE.setLoggedTour(null);
            ShowTour.INSTANCE.setLoggedEvent(null);
            Filter.INSTANCE.setCountry(null);
            Filter.INSTANCE.setMonth(null);
            Filter.INSTANCE.setGuideman(null);
            Filter.INSTANCE.setPrice(null);
            Stage stage = new Stage();
            fxmlLoader.setController(new WelcomeController());
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Guideman");
            stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png")); 
            stage.setScene(scene);
            stage.show();
            button.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
