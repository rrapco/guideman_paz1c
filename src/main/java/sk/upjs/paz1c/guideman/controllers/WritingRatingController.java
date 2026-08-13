package sk.upjs.paz1c.guideman.controllers;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.EventDao;
import sk.upjs.paz1c.guideman.storage.UserDao;

public class WritingRatingController {

	private int rating;
	private String review;
	private UserDao userDao;
	private EventDao eventDao;
	private Window owner;
	private Long loggedUserId;
	private Long loggedEventId;

	@FXML
	private RadioButton fiveRadioButton;

	@FXML
	private RadioButton fourRadioButton;

	@FXML
	private RadioButton oneRadioButton;

	@FXML
	private TextArea reviewTextArea;

	@FXML
	private Button saveRatingButton;

	@FXML
	private Button saveReviewButton;

	@FXML
	private RadioButton threeRadioButton;

	@FXML
	private RadioButton twoRadioButton;

	@FXML
	private RadioButton zeroRadioButton;

	@FXML
	void initialize() {
		userDao = DaoFactory.INSTANCE.getUserDao();
		eventDao = DaoFactory.INSTANCE.getEventDao();
		loggedUserId = LoggedUser.INSTANCE.getLoggedUser().getId();
		loggedEventId = ShowTour.INSTANCE.getLoggedEvent().getId();
		rating = -1;

	}

	@FXML
	void zeroRadioButtonAction(ActionEvent event) {
		if (zeroRadioButton.isSelected()) {
			oneRadioButton.setDisable(true);
			twoRadioButton.setDisable(true);
			threeRadioButton.setDisable(true);
			fourRadioButton.setDisable(true);
			fiveRadioButton.setDisable(true);
			rating = 0;
			System.out.println(rating);
		}
		if (!zeroRadioButton.isSelected()) {
			oneRadioButton.setDisable(false);
			twoRadioButton.setDisable(false);
			threeRadioButton.setDisable(false);
			fourRadioButton.setDisable(false);
			fiveRadioButton.setDisable(false);
		}

	}

	@FXML
	void oneRadioButtonAction(ActionEvent event) {
		if (oneRadioButton.isSelected()) {
			zeroRadioButton.setDisable(true);
			twoRadioButton.setDisable(true);
			threeRadioButton.setDisable(true);
			fourRadioButton.setDisable(true);
			fiveRadioButton.setDisable(true);
			rating = 1;
			System.out.println(rating);

		}
		if (!oneRadioButton.isSelected()) {
			zeroRadioButton.setDisable(false);
			twoRadioButton.setDisable(false);
			threeRadioButton.setDisable(false);
			fourRadioButton.setDisable(false);
			fiveRadioButton.setDisable(false);
		}

	}

	@FXML
	void twoRadioButtonAction(ActionEvent event) {
		if (twoRadioButton.isSelected()) {
			zeroRadioButton.setDisable(true);
			oneRadioButton.setDisable(true);
			threeRadioButton.setDisable(true);
			fourRadioButton.setDisable(true);
			fiveRadioButton.setDisable(true);
			rating = 2;
			System.out.println(rating);
		}
		if (!twoRadioButton.isSelected()) {
			zeroRadioButton.setDisable(false);
			oneRadioButton.setDisable(false);
			threeRadioButton.setDisable(false);
			fourRadioButton.setDisable(false);
			fiveRadioButton.setDisable(false);
		}
	}

	@FXML
	void threeRadioButtonAction(ActionEvent event) {
		if (threeRadioButton.isSelected()) {
			zeroRadioButton.setDisable(true);
			oneRadioButton.setDisable(true);
			twoRadioButton.setDisable(true);
			fourRadioButton.setDisable(true);
			fiveRadioButton.setDisable(true);
			rating = 3;
			System.out.println(rating);
		}
		if (!threeRadioButton.isSelected()) {
			zeroRadioButton.setDisable(false);
			oneRadioButton.setDisable(false);
			twoRadioButton.setDisable(false);
			fourRadioButton.setDisable(false);
			fiveRadioButton.setDisable(false);
		}
	}

	@FXML
	void fourRadioButtonAction(ActionEvent event) {
		if (fourRadioButton.isSelected()) {
			zeroRadioButton.setDisable(true);
			oneRadioButton.setDisable(true);
			twoRadioButton.setDisable(true);
			threeRadioButton.setDisable(true);
			fiveRadioButton.setDisable(true);
			rating = 4;
			System.out.println(rating);
		}
		if (!fourRadioButton.isSelected()) {
			zeroRadioButton.setDisable(false);
			oneRadioButton.setDisable(false);
			twoRadioButton.setDisable(false);
			threeRadioButton.setDisable(false);
			fiveRadioButton.setDisable(false);
		}
	}

	@FXML
	void fiveRadioButtonAction(ActionEvent event) {
		if (fiveRadioButton.isSelected()) {
			zeroRadioButton.setDisable(true);
			oneRadioButton.setDisable(true);
			twoRadioButton.setDisable(true);
			threeRadioButton.setDisable(true);
			fourRadioButton.setDisable(true);
			rating = 5;
			System.out.println(rating);
		}
		if (!fiveRadioButton.isSelected()) {
			zeroRadioButton.setDisable(false);
			oneRadioButton.setDisable(false);
			twoRadioButton.setDisable(false);
			threeRadioButton.setDisable(false);
			fourRadioButton.setDisable(false);
		}
	}

	@FXML
	void saveRatingButtonAction(ActionEvent event) {
		if (!zeroRadioButton.isSelected() && !oneRadioButton.isSelected() && !twoRadioButton.isSelected()
				&& !threeRadioButton.isSelected() && !fourRadioButton.isSelected() && !fiveRadioButton.isSelected()) {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !",
					"Cannot save rating, because you have not selecting one of ratings !");
			return;
		}

		if (rating != -1) {
			List<Integer> ratingFromDB = eventDao.getRating(loggedUserId, loggedEventId);
			System.out.println("rating from db   " + ratingFromDB.toString());
			if (ratingFromDB.get(0) != 0) {
				showAlert(Alert.AlertType.WARNING, owner, "Warning !",
						"Cannot save rating, because you have already rated this tour !\nYour rating is : "
								+ ratingFromDB.get(0));
			}
			if (ratingFromDB.get(0) == 0) {
				userDao.saveRating(loggedUserId, loggedEventId, rating);
				showAlert(Alert.AlertType.INFORMATION, owner, "Success !",
						"Rating was saved ! Your rating is : " + rating);

			}
		}
	}

	@FXML
	void saveReviewButtonAction(ActionEvent event) {
		review = reviewTextArea.getText();
		System.out.println(review);

		if (reviewTextArea.getText().equals("")) {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !",
					"Cannot save review, because the text area is empty !");
			return;
		}

		if (!review.equals("")) {
			List<String> reviewFromDB = eventDao.getReview(loggedUserId, loggedEventId);
			System.out.println("review form db   " + reviewFromDB.toString());
			if (reviewFromDB.get(0) != null) {
				showAlert(Alert.AlertType.WARNING, owner, "Warning !",
						"Cannot save review, because you have already reviewed this tour !\nYour review : "
								+ reviewFromDB.get(0));
			}
			if (reviewFromDB.get(0) == null) {
				userDao.saveReview(loggedUserId, loggedEventId, review);
				showAlert(Alert.AlertType.INFORMATION, owner, "Success !", "Review was saved !");
			}
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
