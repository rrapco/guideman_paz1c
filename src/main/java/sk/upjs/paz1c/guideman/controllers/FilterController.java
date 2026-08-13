package sk.upjs.paz1c.guideman.controllers;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Window;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.Location;
import sk.upjs.paz1c.guideman.storage.LocationDao;
import sk.upjs.paz1c.guideman.storage.User;
import sk.upjs.paz1c.guideman.storage.UserDao;

public class FilterController {

	private LocationDao locationDao;
	private UserDao userDao;
	private Window owner;

	@FXML
	private ComboBox<String> filterByCountryComboBox;

	@FXML
	private ComboBox<String> filterByGuidemanComboBox;

	@FXML
	private ComboBox<String> filterByMonthComboBox;

	@FXML
	private Slider filterByPriceSlider;

	@FXML
	private Label priceLabel;

	@FXML
	private Button saveAndFilterButton;

	@FXML
	void initialize() {
		locationDao = DaoFactory.INSTANCE.getLocationDao();
		userDao = DaoFactory.INSTANCE.getUserDao();
		fillCountryComboBox();
		filterByCountryComboBox.getSelectionModel().selectFirst();
		fillMonthComboBox();
		filterByMonthComboBox.getSelectionModel().selectFirst();
		fillGuidemanComboBox();
		filterByGuidemanComboBox.getSelectionModel().selectFirst();

		filterByPriceSlider.setValue(100);
		priceLabel.setText("100");
		filterByPriceSlider.valueProperty()
				.addListener((ObservableValue<? extends Number> num, Number oldVal, Number newVal) -> {
					Integer roundedNewVal = (int) Math.round(Double.valueOf(newVal.toString()));
					priceLabel.setText(roundedNewVal.toString());
				});

		Filter.INSTANCE.setCountry("null");
		Filter.INSTANCE.setMonth("null");
		Filter.INSTANCE.setGuideman("null");
		Filter.INSTANCE.setPrice("0");
		Filter.INSTANCE.setNewFilters(false);
	}

	private void fillCountryComboBox() {
		List<Location> locationsFromDB = locationDao.getAll();
		List<String> locations = new ArrayList<>();
		locations.add("ALL");
		for (Location l : locationsFromDB) {
			if (!locations.contains(l.getCountry())) {
				locations.add(l.getCountry());
			}
		}
		filterByCountryComboBox.getSelectionModel().selectFirst();
		filterByCountryComboBox.setItems(FXCollections.observableArrayList(locations));

	}

	private void fillMonthComboBox() {
		List<String> months = new ArrayList<>();
		months.add("ALL");
		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
		filterByMonthComboBox.getSelectionModel().selectFirst();
		filterByMonthComboBox.setItems(FXCollections.observableArrayList(months));

	}

	private void fillGuidemanComboBox() {
		List<User> guidemans = userDao.getAllGuidemans();
		List<String> nameAndSurname = new ArrayList<>();
		nameAndSurname.add("ALL");
		for (User u : guidemans) {
			if (u.getId() != LoggedUser.INSTANCE.getLoggedUser().getId()) {
				nameAndSurname.add(u.getName() + " " + u.getSurname());
			}
		}
		filterByGuidemanComboBox.getSelectionModel().selectFirst();
		filterByGuidemanComboBox.setItems(FXCollections.observableArrayList(nameAndSurname));

	}

	@FXML
	void saveAndFilterButtonAction(ActionEvent event) {
		String country = filterByCountryComboBox.getSelectionModel().getSelectedItem();
		String month = filterByMonthComboBox.getSelectionModel().getSelectedItem();
		String guideman = filterByGuidemanComboBox.getSelectionModel().getSelectedItem();
		String price = priceLabel.getText();

		String countryLogged = Filter.INSTANCE.getCountry();
		String monthLogged = Filter.INSTANCE.getMonth();
		String guidemanLogged = Filter.INSTANCE.getGuideman();
		String priceLogged = Filter.INSTANCE.getPrice();

//		String countryNew = "";
//		String monthNew = "";
//		String guidemanNew = "";
//		String priceNew = "";

		boolean countryEquals = false;
		boolean monthEquals = false;
		boolean guidemanEquals = false;
		boolean priceEquals = false;

		if (countryLogged == null) {
			// countryNew = country;
		} else {
			if (countryLogged.equals(country)) {
				countryEquals = true;
			} else {
				countryLogged = country;
			}

		}

		/////////////

		if (monthLogged == null) {
			// monthNew = month;
		} else {
			if (monthLogged.equals(month)) {
				monthEquals = true;
			} else {
				monthLogged = month;
			}

		}

		////////////

		if (guidemanLogged == null) {
			// guidemanNew = guideman;
		} else {
			if (guidemanLogged.equals(guideman)) {
				guidemanEquals = true;
			} else {
				guidemanLogged = guideman;
			}

		}
		//////////////

		if (priceLogged == null) {
			// priceNew = price;
		} else {
			if (priceLogged.equals(price)) {
				priceEquals = true;
			} else {
				priceLogged = price;
			}

		}

		if (countryEquals && monthEquals && guidemanEquals && priceEquals) {
			showAlert(Alert.AlertType.WARNING, owner, "Warning !", "No change has been made !");
			System.out.println("ALERT - NO CHANGE HAS BEEN MADE");
		} else {
			Filter.INSTANCE.setCountry(countryLogged);
			Filter.INSTANCE.setMonth(monthLogged);
			Filter.INSTANCE.setGuideman(guidemanLogged);
			Filter.INSTANCE.setPrice(priceLogged);
			Filter.INSTANCE.setNewFilters(true);

			showAlert(Alert.AlertType.INFORMATION, owner, "Success !", "Filter have been saved successfully !");
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
