package sk.upjs.paz1c.guideman.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.SwingUtilities;

import org.springframework.security.crypto.bcrypt.BCrypt;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.User;
import sk.upjs.paz1c.guideman.storage.UserDao;

public class LoginController {

	boolean hideWindow = false;
	private User currentUser;
	private Button button;
	private UserDao userDao;

	// FXML

	@FXML
	private TextField usernameTextField;

	@FXML
	private PasswordField passwordPasswordField;

	@FXML
	private Button logInButton;

	public LoginController() {

	}

	public LoginController(Button button) {
		this.button = button;
	}

	@FXML
	void initialize() {
		// ide iba na jeden field, idk ako to zmenit na dva
		userDao = DaoFactory.INSTANCE.getUserDao();

		BooleanBinding bb = new BooleanBinding() {
			{
				super.bind(usernameTextField.textProperty(), passwordPasswordField.textProperty());
			}

			@Override
			protected boolean computeValue() {
				return (usernameTextField.getText().isEmpty() || passwordPasswordField.getText().isEmpty());
			}
		};

		logInButton.disableProperty().bind(bb);

	}

	@FXML
	void logInButtonClick(ActionEvent event) throws SQLException {
		MyProfileController controller = new MyProfileController();
		logIntoApp(controller, event);
	}

	@FXML
	void logIntoApp(MyProfileController controller, ActionEvent event) throws SQLException {

		Window owner = logInButton.getScene().getWindow();

		if (usernameTextField.getText().isEmpty()) {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !", "Please enter username");
			return;
		}

		if (passwordPasswordField.getText().isEmpty()) {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !", "Please enter password");
			return;
		}

		String login = usernameTextField.getText();
		String password = passwordPasswordField.getText();
		List<User> allUsersInDb = userDao.getAll();
		boolean userExists = false;

		for (User user : allUsersInDb) {
			if (user.getLogin().equals(login)) {
				userExists = true;
				break;
			}
		}

		if (!userExists) {
			showAlert(Alert.AlertType.ERROR, owner, "Error !", "User does not exist !");
			return;
		}

		currentUser = userDao.getUserByUsername(login);
		LoggedUser.INSTANCE.setLoggedUser(currentUser);

		if (currentUser == null || !(BCrypt.checkpw(password, currentUser.getPassword()))) {
			passwordPasswordField.setText("");
			infoBox("Please enter correct Login and Password", null, "Failed login");
		} else {
			infoBox("Log in Successful !", null, "Successful log in");

			usernameTextField.getScene().getWindow().hide();
			button.getScene().getWindow().hide();
			// treba zavriet hlavne okno

			try {
				FXMLLoader fxmlLoader = new FXMLLoader(MyProfileController.class.getResource("myProfile2.fxml"));
				Stage stage = new Stage();
				fxmlLoader.setController(controller);
				Scene scene = new Scene(fxmlLoader.load());
				stage.setTitle("Guideman");
				stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png"));
				stage.setScene(scene);
//		            stage.getIcons().add(new Image("sk/upjs/favicon.png")); // danko tak ma icon
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void infoBox(String infoMessage, String headerText, String title) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText(infoMessage);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.showAndWait();
	}

	private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.initOwner(owner);
		alert.show();
	}

}
