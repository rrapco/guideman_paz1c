package sk.upjs.paz1c.guideman.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.Event;
import sk.upjs.paz1c.guideman.storage.EventDao;
import sk.upjs.paz1c.guideman.storage.Tour;
import sk.upjs.paz1c.guideman.storage.TourDao;
import sk.upjs.paz1c.guideman.storage.User;
import sk.upjs.paz1c.guideman.storage.UserDao;

public class SearchTourController {

	private String country;
	private String month;
	private String guideman;
	private String price;

	private EventDao eventDao;
	private TourDao tourDao;
	private UserDao userDao;
	private Long loggedUserId;
	private Window owner;

	@FXML
	private Label countryLabel;

	@FXML
	private Button createTourButton;

	@FXML
	private ListView<String> filteredToursListView;

	@FXML
	private Label guidemanLabel;

	@FXML
	private Button logOutButton;

	@FXML
	private Label monthLabel;

	@FXML
	private Button myProfileButton;

	@FXML
	private Button myToursButton;

	@FXML
	private Label priceLabel;

	@FXML
	private Button searchTourButton;

	@FXML
	private Button showFilterTableButton;

	@FXML
	private Button showTourButton;

	@FXML
	void initialize() {
		eventDao = DaoFactory.INSTANCE.getEventDao();
		tourDao = DaoFactory.INSTANCE.getTourDao();
		userDao = DaoFactory.INSTANCE.getUserDao();
		loggedUserId = LoggedUser.INSTANCE.getLoggedUser().getId();

		countryLabel.setText("All");
		monthLabel.setText("All");
		guidemanLabel.setText("All");
		priceLabel.setText("100");
		country = "All";
		month = "All";
		guideman = "All";
		price = "100";

		List<String> displayed = new ArrayList<>();
		if (eventDao.getAll().size() > 0) {
			List<Event> eventsAfterCheck = new ArrayList<>();
			eventsAfterCheck = check(eventDao.getAll());
			for (Event e : eventsAfterCheck) {
				prepareTourAndEventForListView(displayed, e);
			}
		} else {
			displayed.add("No tours found");
			filteredToursListView.setMouseTransparent(true);
			showTourButton.setDisable(true);
		}
		filteredToursListView.setItems(FXCollections.observableArrayList(displayed));
	}

	@FXML
	void showFilterTableButtonAction(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("filter.fxml"));
			fxmlLoader.setController(new FilterController());
			Parent parent = fxmlLoader.load();
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Guideman");
			stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png"));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (Filter.INSTANCE.getNewFilters()) {
			fillLabels();
			country = Filter.INSTANCE.getCountry();
			month = Filter.INSTANCE.getMonth();
			guideman = Filter.INSTANCE.getGuideman();
			price = Filter.INSTANCE.getPrice();

			System.out.println(country);
			System.out.println(month);
			System.out.println(guideman);
			System.out.println(price);

			fillListView();
		}
	}

	private void fillLabels() {
		countryLabel.setText(Filter.INSTANCE.getCountry());
		monthLabel.setText(Filter.INSTANCE.getMonth());
		guidemanLabel.setText(Filter.INSTANCE.getGuideman());
		priceLabel.setText(Filter.INSTANCE.getPrice());
	}

	private int parseMonthToInt(String monthStr) {
		if (monthStr.equals("January")) {
			return 1;
		}
		if (monthStr.equals("February")) {
			return 2;
		}
		if (monthStr.equals("March")) {
			return 3;
		}
		if (monthStr.equals("April")) {
			return 4;
		}
		if (monthStr.equals("May")) {
			return 5;
		}
		if (monthStr.equals("June")) {
			return 6;
		}
		if (monthStr.equals("July")) {
			return 7;
		}
		if (monthStr.equals("August")) {
			return 8;
		}
		if (monthStr.equals("September")) {
			return 9;
		}
		if (monthStr.equals("October")) {
			return 10;
		}
		if (monthStr.equals("November")) {
			return 11;
		}
		if (monthStr.equals("December")) {
			return 12;
		}
		return 0;
	}

	private List<Event> check(List<Event> notCheckEvents) {
		List<Event> allEvents = new ArrayList<>();
		for (Event event : notCheckEvents) {
			String[] dtE = event.getDateOfTour().toString().split("T");
			LocalDate dateEvent = LocalDate.parse(dtE[0]);
			LocalDate dateNow = LocalDate.now();
			if (dateEvent.isAfter(dateNow)) {
				//
				Tour tour = tourDao.getById(event.getTourId());
				if (!Objects.equals(tour.getGuidemanId(), loggedUserId)) {
					List<User> tourists = userDao.getAllTourists(event.getId());
					boolean signed = false;
					for (User u : tourists) {
						if (Objects.equals(u.getId(), loggedUserId)) {
							signed = true;
						}
					}
					if (!signed) {
						allEvents.add(event);
					}
				}
			}

		}
		return allEvents;
	}

	private void fillListView() {
		List<String> displayed = new ArrayList<>();
		List<Event> temp = new ArrayList<>();

		// country
		if (country.equals("ALL")) {
			if (eventDao.getAll().size() > 0) {
				temp = eventDao.getAll();
			}
		} else {
			List<Event> eventsByCountry = eventDao.getAllEventsByCountry(country);
			temp = eventsByCountry;
		}

		// month
		List<Event> eventsByMonth = new ArrayList<>();
		if (!month.equals("ALL")) {
			eventsByMonth = eventDao.getAllByMonth(parseMonthToInt(month));
			if (temp.size() > 0) {
				temp.retainAll(eventsByMonth);
			}
		}

		// guideman
		List<Event> eventsByGuideman = new ArrayList<>();
		if (!guideman.equals("ALL")) {
			String[] nAs = guideman.split(" ");
			eventsByGuideman = eventDao.getAllEventsByGuideman(nAs[0], nAs[1]);
			if (temp.size() > 0) {
				temp.retainAll(eventsByGuideman);
			}
		}

		// price
		List<Event> eventsByPrice = new ArrayList<>();
		eventsByPrice = eventDao.getAllEventsWithPriceLowerThan(Integer.parseInt(price));
		if (temp.size() > 0) {
			temp.retainAll(eventsByPrice);
		}

		///////
		System.out.println("TEMP BEFORE CHECK : " + temp);

		if (temp.size() > 0) {
			List<Event> eventsAfterCheck = new ArrayList<>();
			eventsAfterCheck = check(temp);
			System.out.println("TEMP AFTER CHECK : " + eventsAfterCheck);

			if (eventsAfterCheck.size() > 0) {
				for (Event e : eventsAfterCheck) {
					prepareTourAndEventForListView(displayed, e);
				}
			} else {
				displayed.add("No tours found");
				filteredToursListView.setMouseTransparent(true);
				showTourButton.setDisable(true);
			}
			
		} else {
			displayed.add("No tours found");
			filteredToursListView.setMouseTransparent(true);
			showTourButton.setDisable(true);
		}
		filteredToursListView.setItems(FXCollections.observableArrayList(displayed));
	}

	private void prepareTourAndEventForListView(List<String> displayed, Event e) {
		Tour t = tourDao.getById(e.getTourId());
		String[] dt = e.getDateOfTour().toString().split("T");
		String[] date = dt[0].split("-");
		String time = dt[1];
		StringBuilder sbDate = new StringBuilder();
		sbDate.append(date[2]);
		sbDate.append(".");
		sbDate.append(date[1]);
		sbDate.append(".");
		sbDate.append(date[0]);
		String s = "Title : " + t.getTitle() + ",        date of tour : " + sbDate + ",        time of tour : " + time
				+ ",        price : " + e.getPrice() + ",        event id : " + e.getId();
		displayed.add(s);
		filteredToursListView.setMouseTransparent(false);
		showTourButton.setDisable(false);

	}

	private Event getEventFromListView() {
		String s;
		s = filteredToursListView.getSelectionModel().getSelectedItem();
		String[] temp1 = s.split(" ");
		String eventIdString = temp1[temp1.length - 1];
		Long eventIdLong = Long.parseLong(eventIdString);
		return eventDao.getById(eventIdLong);
	}

	@FXML
	void showTourButtonAction(ActionEvent event) {
		Event e1 = new Event();
		try {
			e1 = getEventFromListView();
		} catch (NullPointerException ex) {
			showAlert(Alert.AlertType.WARNING, owner, "Warning!", "Please select row from list !");
			return;
		}
		Tour t1 = tourDao.getById(e1.getTourId());
		ShowTour.INSTANCE.setLoggedEvent(e1);
		ShowTour.INSTANCE.setLoggedTour(t1);
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(ShowTourController.class.getResource("showTour.fxml"));
			Stage stage = new Stage();
			fxmlLoader.setController(new ShowTourController());
			Scene scene = new Scene(fxmlLoader.load());
			stage.setTitle("Guideman");
			stage.getIcons().add(new Image("sk/upjs/paz1c/guideman/controllers/G-logo light.png"));
			stage.setScene(scene);
			stage.show();
			showFilterTableButton.getScene().getWindow().hide();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void myProfileButtonAction(ActionEvent event) {
		Menu.INSTANCE.openMyProfile(showFilterTableButton);
	}

	@FXML
	void myToursButtonAction(ActionEvent event) {
		Menu.INSTANCE.openMyTours(showFilterTableButton);
	}

	@FXML
	void searchTourButtonAction(ActionEvent event) {
		System.out.println("search");
	}

	@FXML
	void createTourButtonAction(ActionEvent event) {
		Menu.INSTANCE.openCreateTour(showFilterTableButton);
	}

	@FXML
	void logOutButtonAction(ActionEvent event) {
		Menu.INSTANCE.logOut(showFilterTableButton);
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
