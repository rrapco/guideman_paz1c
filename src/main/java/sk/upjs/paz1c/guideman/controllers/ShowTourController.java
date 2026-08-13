package sk.upjs.paz1c.guideman.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.EntityNotFoundException;
import sk.upjs.paz1c.guideman.storage.Event;
import sk.upjs.paz1c.guideman.storage.EventDao;
import sk.upjs.paz1c.guideman.storage.Location;
import sk.upjs.paz1c.guideman.storage.LocationDao;
import sk.upjs.paz1c.guideman.storage.Tour;
import sk.upjs.paz1c.guideman.storage.User;
import sk.upjs.paz1c.guideman.storage.UserDao;

public class ShowTourController {

	private Window owner;

	private UserDao userDao = DaoFactory.INSTANCE.getUserDao();
	private LocationDao locationDao = DaoFactory.INSTANCE.getLocationDao();
	private EventDao eventDao = DaoFactory.INSTANCE.getEventDao();

	private Tour loggedTour = ShowTour.INSTANCE.getLoggedTour();
	private Event loggedEvent = ShowTour.INSTANCE.getLoggedEvent();

	@FXML
	private Button createTourButton;

	@FXML
	private Label dateAndTimeFillLabel;

	@FXML
	private Label dateAndTimeLabel;

	@FXML
	private Label durationFillLabel;

	@FXML
	private ImageView imageImageView;

	@FXML
	private Button letsGoButton;

	@FXML
	private Button logOutButton;

	@FXML
	private Button myProfileButton;

	@FXML
	private Button myToursButton;

	@FXML
	private Label numberOfFreePlacesFillLabel;

	@FXML
	private Label priceFillLabel;

	@FXML
	private Label ratingFillLabel;

	@FXML
	private Button searchTourButton;

	@FXML
	private Button seeReviewsButton;

	@FXML
	private Label titleFillLabel;

	@FXML
	private Label guidemanFillLabel;

	@FXML
	private Label countryFillLabel;

	@FXML
	private Label cityFillLabel;

	@FXML
	private Label streetFillLabel;

	@FXML
	private Label streetNumberFillLabel;

	@FXML
	private TextArea bioTextArea;

	@FXML
	void myProfileButtonAction(ActionEvent event) {
		Menu.INSTANCE.openMyProfile(seeReviewsButton);
	}

	@FXML
	void myToursButtonAction(ActionEvent event) {
		Menu.INSTANCE.openMyTours(seeReviewsButton);
	}

	@FXML
	void searchTourButtonAction(ActionEvent event) {
		Menu.INSTANCE.openSearchTour(seeReviewsButton);
	}

	@FXML
	void createTourButtonAction(ActionEvent event) {
		Menu.INSTANCE.openCreateTour(seeReviewsButton);
	}

	@FXML
	void logOutButtonAction(ActionEvent event) {
		Menu.INSTANCE.logOut(seeReviewsButton);
	}

	@FXML
	void letsGoButtonAction(ActionEvent event) {
		boolean signed = false;

		if (event.getSource() == letsGoButton) {
			System.out.println("LETS GO");
			List<User> tourists = DaoFactory.INSTANCE.getUserDao().getAllTourists(loggedEvent.getId());

			for (User user : tourists) {
				if (user.getId() == LoggedUser.INSTANCE.getLoggedUser().getId()) {
					signed = true;
				}
			}

			if (tourists.size() < loggedTour.getMaxSlots()) {
				if (signed == false) {
					userDao.saveUserEvent(LoggedUser.INSTANCE.getLoggedUser().getId(), loggedEvent.getId());
					showAlert(Alert.AlertType.CONFIRMATION, owner, "Confirmation !",
							"You have successfully signed for this tour !");
					String temp = numberOfFreePlacesFillLabel.getText();
					numberOfFreePlacesFillLabel.setText(
							String.valueOf(tourists.size() + 1) + "/" + String.valueOf(loggedTour.getMaxSlots()));

					letsGoButton.setDisable(true);

				} else {
					showAlert(Alert.AlertType.WARNING, owner, "Warning !", "You are already signed to this tour !");
				}
			}

		}
	}

	@FXML
	void seeReviewsButtonAction(ActionEvent event) throws IOException {
		ShowReviewsController controller = new ShowReviewsController();
		showReviews(controller);

	}

	void showReviews(ShowReviewsController controller) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reviews.fxml"));
			fxmlLoader.setController(controller);
			Parent parent = fxmlLoader.load();
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Reviews");
			stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png"));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() throws SQLException {

		Tour loggedTour = ShowTour.INSTANCE.getLoggedTour();
		Event loggedEvent = ShowTour.INSTANCE.getLoggedEvent();
		titleFillLabel.setText(loggedTour.getTitle());
		bioTextArea.setWrapText(true);
		bioTextArea.setText(loggedTour.getBio());
		bioTextArea.setEditable(false);

		List<User> tourists = DaoFactory.INSTANCE.getUserDao().getAllTourists(loggedEvent.getId());
		if (tourists.size() == loggedTour.getMaxSlots()) {
			letsGoButton.setDisable(true);
		}
		numberOfFreePlacesFillLabel
				.setText(String.valueOf(tourists.size()) + "/" + String.valueOf(loggedTour.getMaxSlots()));
		LocalDateTime datetime = LocalDateTime.parse(loggedEvent.getDateOfTour().toString());
		System.out.println(datetime.toString() + " datetime");
		dateAndTimeFillLabel.setText(europeTimeZone(datetime));
		durationFillLabel.setText(loggedEvent.getDuration().toString());
		priceFillLabel.setText(String.valueOf(loggedEvent.getPrice()));
		User guideman = userDao.getById(loggedTour.getGuidemanId());
		guidemanFillLabel.setText(guideman.getName() + " " + guideman.getSurname());

		Location location = locationDao.getById(loggedTour.getLocationId());
		countryFillLabel.setText(location.getCountry());
		cityFillLabel.setText(location.getCity());
		streetFillLabel.setText(location.getStreet());
		if (location.getStreet_number() != null) {
			streetNumberFillLabel.setText(location.getStreet_number().toString());
		} else {
			streetNumberFillLabel.setText("N");
		}

		Blob toursBlob = loggedTour.getImage();

		if (toursBlob != null) {
			InputStream in = toursBlob.getBinaryStream();
			Image image = new Image(in);
			imageImageView.setImage(image);
			centerImage();
		}

		List<Integer> listOfRatings = eventDao.getRatings(loggedEvent.getTourId());
		System.out.println(listOfRatings + " list ratingov");
		Double averageRating = (double) 0;
		if (listOfRatings.size() > 0) {
			for (Integer integer : listOfRatings) {
				averageRating = averageRating + integer;
			}
		} else {
			ratingFillLabel.setText("No ratings yet");
		}

		averageRating = round(averageRating / (double) listOfRatings.size(), 2);
		ratingFillLabel.setText(averageRating.toString() + "/5");

	}

	// https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public String europeTimeZone(LocalDateTime datetime) {
		String[] dateToArray = datetime.toString().split("-");
		String year = dateToArray[0];
		String month = dateToArray[1];
		String excractMore = dateToArray[2];

		String day = excractMore.substring(0, 2);

		String time = excractMore.substring(3, 8);

		return day + "." + month + "." + year + " " + time;
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

	private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.initOwner(owner);
		alert.show();
	}

}
