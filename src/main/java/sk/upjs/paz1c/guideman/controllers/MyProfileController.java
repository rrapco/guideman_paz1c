package sk.upjs.paz1c.guideman.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.User;
import sk.upjs.paz1c.guideman.storage.UserDao;

public class MyProfileController {

	private User loggedUser;
	private Window owner;
	private UserDao userDao = DaoFactory.INSTANCE.getUserDao();

	// obrazok
	private File selectedFile;
	private String filePath = null;
	private String oldFilePath = null;
	private String nameOfFile;
	private byte[] bytes = null; // obrazok v bytoch

	@FXML
	private Button changeImageButton;

	@FXML
	private Label dateOfBirthLabel;

	@FXML
	private TextField dateOfBirthTextField;

	@FXML
	private Button editAndSaveButton;

	@FXML
	private Label emailLabel;

	@FXML
	private TextField emailTextField;

	@FXML
	private ImageView imageImageView;

	@FXML
	private Label myProfileLabel;

	@FXML
	private Label nameLabel;

	@FXML
	private TextField nameTextField;

	@FXML
	private Label phoneNumberLabel;

	@FXML
	private TextField phoneNumberTextField;

	@FXML
	private Button showFavouriteGuidemansButton;

	@FXML
	private Label surnameLabel;

	@FXML
	private TextField surnameTextField;

	@FXML
	void changeImageButtonAction(ActionEvent event) throws IOException, SerialException, SQLException {
		System.out.println("change");

		owner = changeImageButton.getScene().getWindow();
		if (event.getSource() == changeImageButton) {

			JFileChooser fileChooser = new JFileChooser();
			// https://community.oracle.com/tech/developers/discussion/2508757/jfilechooser-problem-on-mac-os
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.image", "jpg", "png");
			fileChooser.addChoosableFileFilter(filter);

			int response = fileChooser.showSaveDialog(null); // select file to open

			if (response == JFileChooser.APPROVE_OPTION) {
				bytes = null;
				selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath()); // File , jeho cesta
				filePath = selectedFile.getAbsolutePath(); // string
				if (filePath.endsWith(".jpg") || filePath.endsWith(".JPG") || filePath.endsWith(".PNG")
						|| filePath.endsWith(".png")) {
					System.out.println(filePath);
					bytes = Files.readAllBytes(Paths.get(filePath));
					nameOfFile = selectedFile.getName();

				} else {
					infoBox("Please Select Image File", null, "Warning !");
					selectedFile = null;
					filePath = null;
				}
			}

			if (bytes != null) {
				Blob blobisko = null;
				blobisko = new SerialBlob(bytes);

				System.out.println(blobisko.length() + " velkost blobu");

				// velke nez 16 mb
				if (blobisko.length() > 16000000L) {
					System.out.println("Error");
					showAlert(Alert.AlertType.ERROR, owner, "Error !", "Please upload smaller image !");
					return;
				}
				Blob usersBlob = blobisko;
				InputStream in = usersBlob.getBinaryStream();
				Image image = new Image(in);
				imageImageView.setImage(image);
				centerImage();
			}
		}
	}

	@FXML
	void editAndSaveButtonAction(ActionEvent event) throws IOException, SerialException, SQLException {
		owner = editAndSaveButton.getScene().getWindow();
		System.out.println("save");
		boolean changed = false;

		String changedName = nameTextField.getText();
		String changedSurname = surnameTextField.getText();
		String changedEmail = emailTextField.getText();
		String changedPhone = phoneNumberTextField.getText();

		String changedDateBirth = dateOfBirthTextField.getText();
		System.out.println(changedDateBirth);
		String pole[] = changedDateBirth.split("\\.");
		String finalDateBirth = pole[2] + "-" + pole[1] + "-" + pole[0];

		LocalDate birthdateParsed = null;

		try {
			birthdateParsed = LocalDate.parse(finalDateBirth);
		} catch (Exception e1) {
			showAlert(Alert.AlertType.WARNING, owner, "Wrong date format !", "Try using date format -> DD.MM.YYYY");
			return;
		}

		Blob blobisko = null;
		if (filePath != null && filePath != oldFilePath) {
			BufferedImage image = ImageIO.read(new File(filePath));
			ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
			// zmena
			if (filePath.endsWith(".jpg")) {
				ImageIO.write(image, "jpg", outStreamObj);
				System.out.println("je jpg");
			}
			if (filePath.endsWith(".png")) {
				ImageIO.write(image, "png", outStreamObj);
				System.out.println("je jpg");
			}

			byte[] byteArray = outStreamObj.toByteArray();

			if (byteArray != null) {
				blobisko = new SerialBlob(byteArray);
				// zmena z 16000000L na
				if (blobisko.length() > 62000000L) {
					System.out.println("Error");
					showAlert(Alert.AlertType.ERROR, owner, "Error !", "Please upload smaller image !");
					return;
				}
			}
			oldFilePath = filePath;
			filePath = null;
		} else {
			blobisko = loggedUser.getImage();
		}

		// ci su prazdne
		if (changedName == "") {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !", "Please enter name");
			return;
		}

		if (changedSurname == "") {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !", "Please enter surname");
			return;
		}

		if (changedEmail == "") {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !", "Please enter email");
			return;
		}

		if (changedDateBirth == "") {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !", "Please enter date of birth");
			return;
		}

		// telcislo treba poriesit null alebo ""
		System.out.println(changedPhone);
		if (changedPhone == null) {
			System.out.println("changedPhone = null");
		} else {
			if (changedPhone.equals("")) {
				if (loggedUser.getTelNumber() != null) {
					changed = true;
				}
				changedPhone = null;
			} else {
				if (!(changedPhone.equals(loggedUser.getTelNumber()))) {
					changed = true;
					System.out.println("DALO SA NA TRUE PRI TEL");
				}
			}
		}

		if (!(changedName.equals(loggedUser.getName()))) {
			System.out.println("chyba v name");
			changed = true;
		}

		if (!(changedSurname.equals(loggedUser.getSurname()))) {
			System.out.println("chyba v surname");
			changed = true;
		}

		if (!(changedEmail.equals(loggedUser.getEmail()))) {
			System.out.println("chyba v mail");
			changed = true;
		}

		if (!(birthdateParsed.equals(LocalDate.parse(loggedUser.getBirthdate().toString())))) {
			System.out.println("chyba v date");
			changed = true;
		}

		// porovnanie ci si nedal rovnaky obrazok dnu
		byte[] blobiskoAsBytes = null;
		if (blobisko != null) {
			int blobiskoL = (int) blobisko.length();
			blobiskoAsBytes = blobisko.getBytes(1, blobiskoL);
		}
		Blob zDB = loggedUser.getImage();
		int zDBL = 0;
		byte[] zDBLAsBytes = null;
		if (zDB != null) {
			zDBL = (int) zDB.length();
			zDBLAsBytes = zDB.getBytes(1, zDBL);
		}

		if (Arrays.equals(zDBLAsBytes, blobiskoAsBytes)) {
			System.out.println("su rovnake bloby");
		} else {
			System.out.println("nie su rovnake bloby");
			changed = true;
		}

		if (changed) {
			changed = false;
			User user = new User(loggedUser.getId(), changedName, changedSurname, changedEmail, changedPhone,
					birthdateParsed, loggedUser.getLogin(), loggedUser.getPassword(), blobisko);

			int sizeBe4 = userDao.getAll().size();

			try {
				userDao.save(user);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			int sizeAfter = userDao.getAll().size();
			if (sizeBe4 == sizeAfter) {
				System.out.println("Edited and Saved successfuly");
				infoBox("Edited and Saved successfuly !", null, "Success");
			}
			System.out.println(loggedUser + " stary");
			LoggedUser.INSTANCE.setLoggedUser(user);

			loggedUser = LoggedUser.INSTANCE.getLoggedUser();
			System.out.println(loggedUser + " novy");
		} else {
			infoBox("No change has been made !", null, "Warning");
		}
	}

	@FXML
	void showFavouriteGuidemansButtonAction(ActionEvent event) {
		System.out.println("guideman");

	}

	//////////////// menu

	@FXML
	private Button myProfileButton;

	@FXML
	private Button myToursButton;

	@FXML
	private Button searchTourButton;

	@FXML
	private Button createTourButton;

	@FXML
	private Button logOutButton;

	@FXML
	void myProfileButtonAction(ActionEvent event) {
		System.out.println("profile");
	}

	@FXML
	void myToursButtonAction(ActionEvent event) {
		Menu.INSTANCE.openMyTours(changeImageButton);
	}

	@FXML
	void searchTourButtonAction(ActionEvent event) {
		Menu.INSTANCE.openSearchTour(changeImageButton);
	}

	@FXML
	void createTourButtonAction(ActionEvent event) {
		Menu.INSTANCE.openCreateTour(changeImageButton);
	}

	@FXML
	void logOutButtonAction(ActionEvent event) {
		Menu.INSTANCE.logOut(changeImageButton);
	}

	///////////////

	@FXML
	void initialize() throws SQLException, IOException {
		loggedUser = LoggedUser.INSTANCE.getLoggedUser();
		System.out.println(loggedUser);

		nameTextField.setText(loggedUser.getName());
		surnameTextField.setText(loggedUser.getSurname());
		emailTextField.setText(loggedUser.getEmail());
		phoneNumberTextField.setText(loggedUser.getTelNumber());

		String dateOfBirth = loggedUser.getBirthdate().toString();
		String pole[] = dateOfBirth.split("-");
		String newDate = pole[2] + "." + pole[1] + "." + pole[0];
		dateOfBirthTextField.setText(newDate);

		Blob usersBlob = loggedUser.getImage();

		if (usersBlob != null) {
			System.out.println(usersBlob);
			InputStream in = usersBlob.getBinaryStream();
			Image image = new Image(in);
			imageImageView.setImage(image);
			centerImage();
			System.out.println(image + " image");
		}
	}

	// https://stackoverflow.com/questions/32781362/centering-an-image-in-an-imageview
	public void centerImage() {
		Image img = imageImageView.getImage();
		if (img != null) {
			double w = 0;
			double h = 0;

			double ratioX = imageImageView.getFitWidth() / img.getWidth();
			double ratioY = imageImageView.getFitHeight() / img.getHeight();

			double reducCoeff = 0;
			if (ratioX >= ratioY) {
				reducCoeff = ratioY;
			} else {
				reducCoeff = ratioX;
			}

			w = img.getWidth() * reducCoeff;
			h = img.getHeight() * reducCoeff;

			imageImageView.setX((imageImageView.getFitWidth() - w) / 2);
			imageImageView.setY((imageImageView.getFitHeight() - h) / 2);

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