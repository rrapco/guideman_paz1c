package sk.upjs.paz1c.guideman.controllers;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.Event;
import sk.upjs.paz1c.guideman.storage.EventDao;

public class ShowReviewsController {

	private EventDao eventDao = DaoFactory.INSTANCE.getEventDao();

	@FXML
	private Label reviewsLabel;

	@FXML
	private ListView<String> reviewsListView;

	@FXML
	void initialize() {

		Event e = ShowTour.INSTANCE.getLoggedEvent();

		List<String> listReviews = eventDao.getReviews(e.getTourId());

		if (listReviews.isEmpty()) {
			listReviews.add("No reviews");
		}

		reviewsListView.setItems(FXCollections.observableArrayList(listReviews));
		reviewsListView.setMouseTransparent(true);
		reviewsListView.getSelectionModel().clearSelection();

	}

}
