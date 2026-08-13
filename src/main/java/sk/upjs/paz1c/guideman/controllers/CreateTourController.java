package sk.upjs.paz1c.guideman.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sk.upjs.paz1c.guideman.models.TourFxModel;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.EntityNotFoundException;
import sk.upjs.paz1c.guideman.storage.Event;
import sk.upjs.paz1c.guideman.storage.EventDao;
import sk.upjs.paz1c.guideman.storage.Location;
import sk.upjs.paz1c.guideman.storage.LocationDao;
import sk.upjs.paz1c.guideman.storage.Tour;
import sk.upjs.paz1c.guideman.storage.TourDao;
import sk.upjs.paz1c.guideman.storage.User;
import sk.upjs.paz1c.guideman.storage.UserDao;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Window;

public class CreateTourController {

	private byte[] bytes = null;
	private String nameOfFile;
	private String filePath;
	private File selectedFile;
	private Location savedLocation;
	private Event savedEvent;
	private Tour savedTour;
	private Window owner;
	private Blob imageFromDB = null;
	private boolean defaultTourImage = false;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
	private UserDao userDao = DaoFactory.INSTANCE.getUserDao();
	private TourDao tourDao = DaoFactory.INSTANCE.getTourDao();
	private LocationDao locationDao = DaoFactory.INSTANCE.getLocationDao();
	private EventDao eventDao = DaoFactory.INSTANCE.getEventDao();
	private TourFxModel tourModel;
	private ObservableList<Tour> comboBoxModel;
	private Tour tourToFill = null;
	private List<String> titles = null;
	private Long idLoggedUser = LoggedUser.INSTANCE.getLoggedUser().getId();
	private List<Tour> toursTemp;

	public CreateTourController() {
		this.tourModel = new TourFxModel();
	}

	@FXML
	void myProfileButtonAction(ActionEvent event) {
		Menu.INSTANCE.openMyProfile(createButton);
	}

	@FXML
	void myToursButtonAction(ActionEvent event) {
		Menu.INSTANCE.openMyTours(createButton);
	}

	@FXML
	void searchTourButtonAction(ActionEvent event) {
		Menu.INSTANCE.openSearchTour(createButton);
	}

	@FXML
	void createTourButtonAction(ActionEvent event) {
		System.out.println("create");
	}

	@FXML
	void logOutButtonAction(ActionEvent event) {
		Menu.INSTANCE.logOut(createButton);
	}

	@FXML
	private TextArea bioTextArea;

	@FXML
	private ComboBox<String> chooseTourComboBox;

	@FXML
	private TextField cityTextField;

	@FXML
	private TextField countryTextField;

	@FXML
	private Button createButton;

	@FXML
	private Button createTourButton;

	@FXML
	private TextField dateAndTimeOfTourTextField;

	@FXML
	private TextField durationTextField;

	@FXML
	private Button logOutButton;

	@FXML
	private Button myProfileButton;

	@FXML
	private Button myToursButton;

	@FXML
	private Label noSelectedImageLabel;

	@FXML
	private TextField numberOfPeopleTextField;

	@FXML
	private TextField priceTextField;

	@FXML
	private Button searchTourButton;

	@FXML
	private Button selectImageButton;

	@FXML
	private TextField streetNumberTextField;

	@FXML
	private TextField streetTextField;

	@FXML
	private TextField titleTextField;

	@FXML
	void createButtonAction(ActionEvent event) throws SerialException, SQLException, IOException {

		if (defaultTourImage == true) {
			filePath = "src/main/resources/sk/upjs/paz1c/guideman/defaultphotoTourResized.jpg";
			System.out.println("default PHOTO ----------------------");
			bytes = Files.readAllBytes(Paths.get(filePath));
		}

		Blob blobisko = null;
		if (bytes != null) {
			blobisko = new SerialBlob(bytes);

			System.out.println(blobisko.length() + " velkost blobu");

			// velke nez 16 mb
			if (blobisko.length() > 16000000L) {
				System.out.println("Error");
				showAlert(Alert.AlertType.WARNING, owner, "Warning !", "Please upload smaller image !");
				// owner neexistuje, robil problem, ak toto bude robit problem , tak treba tu
				// dat infobox
				return;
			}
		}

		String title = titleTextField.getText();
		String bio = bioTextArea.getText();
		String maxPeopleString = numberOfPeopleTextField.getText(); // int
		String country = countryTextField.getText();
		String city = cityTextField.getText();
		String street = streetTextField.getText();
		String streetNumberString = streetNumberTextField.getText(); // int
		String dateAndTimeOfTourString = dateAndTimeOfTourTextField.getText();
		String durationString = durationTextField.getText();
		String priceString = priceTextField.getText();

		Integer streetNumber = null;
		if (streetNumberString != "") {
			try {
				streetNumber = Integer.parseInt(streetNumberString);
			} catch (NumberFormatException e) {
				infoBox("Warning !", null, "Wrong format, check your street number");
				return;
			}
		} else {
			streetNumber = 0;
		}
		Location location = new Location(country, city, street, streetNumber);

		// ulozime location najprv potom tour a tak event

		boolean sameLocation = false;
		// ak uz existuje rovnaka location, neulozime ju
		boolean sameTour = false;
		// ak uz existuje rovnaka tour, neulozime ju

		// location
		List<Location> locations = locationDao.getAll();

		Location locationFromDB = null;

		for (Location eachLocation : locations) {
			String locationCountry = eachLocation.getCountry();
			String locationCity = eachLocation.getCity();
			String locationStreet = eachLocation.getStreet();
			Integer locationStreetNumber = eachLocation.getStreet_number();

			if (locationCountry.equals(location.getCountry()) && locationCity.equals(location.getCity())
					&& locationStreet.equals(location.getStreet()) && locationStreetNumber == streetNumber) {
				System.out.println("su rovnake");
				locationFromDB = eachLocation;
				sameLocation = true;
			}

		}

		try {

			Long idLocation;
			if (sameLocation == false) {
				savedLocation = locationDao.save(location);
				System.out.println(savedLocation + " = saved location");
				idLocation = savedLocation.getId();
			} else {
				savedLocation = locationFromDB;
				System.out.println(savedLocation + " = saved location");
				idLocation = locationFromDB.getId();
			}
			// location

			// tour
			if (bio == "") {
				bio = "No bio";
			}

			Long maxPeople = (long) Integer.parseInt(maxPeopleString);
			User user = userDao.getById(LoggedUser.INSTANCE.getLoggedUser().getId());
			Tour tour = new Tour(title, bio, maxPeople, idLocation, user.getId(), blobisko);

			List<Tour> toursFromDB = tourDao.getAll();
			System.out.println(toursFromDB + " TOURS V DB");
			for (Tour tour2 : toursFromDB) {
				if (tour.getTitle().equals(tour2.getTitle()) && tour.getMaxSlots() == tour2.getMaxSlots()
						&& tour.getLocationId() == tour2.getLocationId()
						&& tour.getGuidemanId() == tour2.getGuidemanId()) {
					System.out.println("existuje tour");
					sameTour = true;
					savedTour = tour2;
				}
			}
			if (sameTour == false) {
				savedTour = tourDao.save(tour);
				System.out.println("DAVAM SAVE TOUR LEBO neexistuje");
			}
			// tour

			// event
			LocalDateTime dateAndTimeOfTour = LocalDateTime.parse(dateAndTimeOfTourString, formatter);
			LocalDateTime now = LocalDateTime.now();
			if (now.isAfter(dateAndTimeOfTour)) {
				throw new DateTimeException("Date needs to be set in the future"); // ?
			}

			LocalTime duration = LocalTime.parse(durationString);
			double price = Double.parseDouble(priceString);
			Event newEvent = new Event(dateAndTimeOfTour, duration, price, savedTour.getId());

			savedEvent = eventDao.save(newEvent);
			// event

			System.out.println(tour);
			System.out.println(tour.getMaxSlots() + " max sloty");
			showAlert(Alert.AlertType.CONFIRMATION, owner, "Success", "Tour has been successfully created !");

			// prida sa title z novovytvorenej tour
			titles.add(titleTextField.getText());
			// a prida sa aj to listu
			toursTemp.add(tour);

			// reset vsetkeho

			resetButtons();
			resetTextFields();
			resetEverythingElse();

			// reset konci

		} catch (NullPointerException e) {
			infoBox("Wrong format", null, "Warning");
			if (savedEvent != null) {
				e.printStackTrace();
				eventDao.delete(savedEvent.getId());
				savedEvent = null;
			}
			if (savedTour != null && sameTour == false) {
				e.printStackTrace();
				tourDao.delete(savedTour.getId());
				savedTour = null;
			}
			if (savedLocation != null && sameLocation == false) {
				e.printStackTrace();
				System.out.println(savedLocation.getId());
				locationDao.delete(savedLocation.getId());
				savedLocation = null;
			}

			return;
		} catch (NumberFormatException e) {
			infoBox("Wrong number format", null, "Warning");
			if (savedEvent != null) {
				eventDao.delete(savedEvent.getId());
				savedEvent = null;
			}
			if (savedTour != null && sameTour == false) {
				tourDao.delete(savedTour.getId());
				savedTour = null;
			}
			if (savedLocation != null && sameLocation == false) {
				System.out.println(savedLocation.getId());
				locationDao.delete(savedLocation.getId());
				savedLocation = null;
			}

			return;
		} catch (DateTimeParseException e) {
			infoBox("Wrong date or time format", null, "Warning");
			if (savedEvent != null) {
				eventDao.delete(savedEvent.getId());
				savedEvent = null;
			}
			if (savedTour != null && sameTour == false) {
				tourDao.delete(savedTour.getId());
				savedTour = null;
			}
			if (savedLocation != null && sameLocation == false) {
				System.out.println(savedLocation.getId());
				locationDao.delete(savedLocation.getId());
				savedLocation = null;
			}

			return;
		} catch (DateTimeException e) {
			infoBox("Date needs to be set in the future ", null, "Warning");
			if (savedEvent != null) {
				eventDao.delete(savedEvent.getId());
				savedEvent = null;
			}
			if (savedTour != null && sameTour == false) {
				tourDao.delete(savedTour.getId());
				savedTour = null;
			}
			if (savedLocation != null && sameLocation == false) {
				System.out.println(savedLocation.getId());
				locationDao.delete(savedLocation.getId());
				savedLocation = null;
			}
			return;
		}

	}

	public void resetButtons() {
		System.out.println("vymazat vsetko :) ");
		titleTextField.setDisable(false);
		bioTextArea.setDisable(false);
		numberOfPeopleTextField.setDisable(false);
		selectImageButton.setDisable(false);
		countryTextField.setDisable(false);
		cityTextField.setDisable(false);
		streetTextField.setDisable(false);
		streetNumberTextField.setDisable(false);

	}

	public void resetTextFields() {
		titleTextField.setText("");
		bioTextArea.setText("");
		numberOfPeopleTextField.setText(""); // int
		countryTextField.setText("");
		cityTextField.setText("");
		streetTextField.setText("");
		streetNumberTextField.setText(""); // int
		dateAndTimeOfTourTextField.setText("");
		durationTextField.setText("");
		priceTextField.setText("");
	}

	public void resetEverythingElse() {
		bytes = null;
		noSelectedImageLabel.setText("No selected file");

		titleTextField.setStyle(null);
		bioTextArea.lookup(".content").setStyle("-fx-background-color: white;");
		bioTextArea.setStyle("-fx-background-color: #d0d0d0;");
		numberOfPeopleTextField.setStyle(null);
		countryTextField.setStyle(null);
		cityTextField.setStyle(null);
		streetTextField.setStyle(null);
		streetNumberTextField.setStyle(null);

		chooseTourComboBox.getSelectionModel().selectFirst();
		chooseTourComboBox.setItems(FXCollections.observableArrayList(titles));
	}

	@FXML
	void selectImageButtonAction(ActionEvent event) throws IOException {
		defaultTourImage = true;
		bytes = null;
		noSelectedImageLabel.setText("No selected file");

		if (event.getSource() == selectImageButton) {

			JFileChooser fileChooser = new JFileChooser();
			// https://community.oracle.com/tech/developers/discussion/2508757/jfilechooser-problem-on-mac-os
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.image", "jpg", "png");
			fileChooser.addChoosableFileFilter(filter);

			int response = fileChooser.showSaveDialog(null); // select file to open

			if (response == JFileChooser.APPROVE_OPTION) {
				defaultTourImage = false;
				selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath()); // File , jeho cesta
				filePath = selectedFile.getAbsolutePath(); // string
				if (filePath.endsWith(".jpg") || filePath.endsWith(".JPG") || filePath.endsWith(".PNG")
						|| filePath.endsWith(".png")) {
					System.out.println(filePath);
					bytes = Files.readAllBytes(Paths.get(filePath));
					nameOfFile = selectedFile.getName();
					noSelectedImageLabel.setText(nameOfFile);
				} else {
					defaultTourImage = true;
					infoBox("Please Select Image File", null, "Warning !");
				}
			}
		}
	}

	public void binding() {
		BooleanBinding bb = new BooleanBinding() {
			{
				super.bind(titleTextField.textProperty(), numberOfPeopleTextField.textProperty(),
						countryTextField.textProperty(), cityTextField.textProperty(), streetTextField.textProperty(),
						dateAndTimeOfTourTextField.textProperty(), durationTextField.textProperty(),
						priceTextField.textProperty());
			}

			@Override
			protected boolean computeValue() {
				return (titleTextField.getText().isEmpty() || numberOfPeopleTextField.getText().isEmpty()
						|| countryTextField.getText().isEmpty() || cityTextField.getText().isEmpty()
						|| streetTextField.getText().isEmpty() || dateAndTimeOfTourTextField.getText().isEmpty()
						|| durationTextField.getText().isEmpty() || priceTextField.getText().isEmpty());
			}
		};

		createButton.disableProperty().bind(bb);
	}

	@FXML
	void initialize() {
		defaultTourImage = true;
		bioTextArea.setWrapText(true);

		binding();

		toursTemp = tourDao.getAllToursByGuideman(idLoggedUser);
		titles = new ArrayList<>();
		// v combo boxe na zaciatku je choose existing tour
		titles.add("Choose existing tour");
		for (Tour tour : toursTemp) {
			titles.add(tour.getTitle());
		}

		chooseTourComboBox.getSelectionModel().selectFirst();
		chooseTourComboBox.setItems(FXCollections.observableArrayList(titles));

		// listener na combobox
		chooseTourComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {

			String chosen = chooseTourComboBox.getValue();
			if (chosen != null) {
				if (!chosen.equals("Choose existing tour")) {

					System.out.println(chosen + " vybraty value");

					for (Tour tour : toursTemp) {
						String tourString = tour.getTitle();
						if (chosen != null) {
							if (chosen.equals(tourString)) {
								tourToFill = tour;
							}
						}
					}

					System.out.println(tourToFill + " vybraty tour");
					if (tourToFill != null) {
						chosedTour();
					}
				} else {
					// setujem podla choosed tour
					clearedChooseTour();

				}
			}
		});

	}

	public void clearedChooseTour() {
		titleTextField.setDisable(false);
		bioTextArea.setDisable(false);
		numberOfPeopleTextField.setDisable(false);
		selectImageButton.setDisable(false);
		countryTextField.setDisable(false);
		cityTextField.setDisable(false);
		streetTextField.setDisable(false);
		streetNumberTextField.setDisable(false);

		titleTextField.setText("");
		bioTextArea.setText("");
		numberOfPeopleTextField.setText(""); // int
		countryTextField.setText("");
		cityTextField.setText("");
		streetTextField.setText("");
		streetNumberTextField.setText(""); // int
		dateAndTimeOfTourTextField.setText("");
		durationTextField.setText("");
		priceTextField.setText("");

		bytes = null;
		noSelectedImageLabel.setText("No selected file");

		titleTextField.setStyle(null);
		bioTextArea.lookup(".content").setStyle("-fx-background-color: white;");
		bioTextArea.setStyle("-fx-background-color: #d0d0d0;");
		numberOfPeopleTextField.setStyle(null);
		countryTextField.setStyle(null);
		cityTextField.setStyle(null);
		streetTextField.setStyle(null);
		streetNumberTextField.setStyle(null);
	}

	public void chosedTour() {
		selectImageButton.setDisable(true);

		String chosenTitle = tourToFill.getTitle();
		titleTextField.setText(chosenTitle);
		titleTextField.setDisable(true);
		titleTextField.setStyle("-fx-background-color: #f2f2f2;");

		String chosenBio = tourToFill.getBio();
		if (!chosenBio.equals("No bio")) {
			bioTextArea.setText(chosenBio);
		} else {
			bioTextArea.setText("");
		}
		bioTextArea.setDisable(true);

		bioTextArea.lookup(".content").setStyle("-fx-background-color: #f2f2f2;");
		bioTextArea.setStyle("-fx-background-color: #f2f2f2;");

		String chosenMaxSlots = tourToFill.getMaxSlots().toString();
		numberOfPeopleTextField.setText(chosenMaxSlots);
		numberOfPeopleTextField.setDisable(true);
		numberOfPeopleTextField.setStyle("-fx-background-color: #f2f2f2;");

		Location locationToFill = locationDao.getById(tourToFill.getLocationId());

		String chosenCountry = locationToFill.getCountry();
		countryTextField.setText(chosenCountry);
		countryTextField.setDisable(true);
		countryTextField.setStyle("-fx-background-color: #f2f2f2;");

		String chosenCity = locationToFill.getCity();
		cityTextField.setText(chosenCity);
		cityTextField.setDisable(true);
		cityTextField.setStyle("-fx-background-color: #f2f2f2;");

		String chosenStreet = locationToFill.getStreet();
		streetTextField.setText(chosenStreet);
		streetTextField.setDisable(true);
		streetTextField.setStyle("-fx-background-color: #f2f2f2;");

		String chosenStreetNumber = locationToFill.getStreet_number().toString();
		streetNumberTextField.setText(chosenStreetNumber);
		streetNumberTextField.setDisable(true);
		streetNumberTextField.setStyle("-fx-background-color: #f2f2f2;");

		// image
		imageFromDB = tourToFill.getImage();
		if (imageFromDB != null) {
			noSelectedImageLabel.setText("Selected image");
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
