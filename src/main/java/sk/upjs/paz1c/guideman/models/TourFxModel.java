package sk.upjs.paz1c.guideman.models;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sk.upjs.paz1c.guideman.controllers.LoggedUser;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.Tour;

public class TourFxModel {

	private Long id;
	private StringProperty title = new SimpleStringProperty();
	private StringProperty bio = new SimpleStringProperty();
	private Long maxSlots;
	private Long locationId;
	private Long guidemanId;
	private Blob image;

	private ObservableList<Tour> myTours;
	private ObservableList<Tour> pastTours;
	private ObservableList<Tour> futureTours;
	private ObservableList<Tour> toursWhereIAmGuideman;

	public TourFxModel() {
	}

	public TourFxModel(Tour tour) {
		this.id = tour.getId();
		title.set(tour.getTitle());
		bio.set(tour.getBio());

		Long loggedUserId = LoggedUser.INSTANCE.getLoggedUser().getId();

		List<Tour> list1 = DaoFactory.INSTANCE.getTourDao().getAllLetsGoTours(loggedUserId);
		myTours = FXCollections.observableArrayList(list1);

		List<Tour> list2 = DaoFactory.INSTANCE.getTourDao().getAllToursFromPast(loggedUserId);
		pastTours = FXCollections.observableArrayList(list2);

		List<Tour> list3 = DaoFactory.INSTANCE.getTourDao().getAllToursFromFuture(loggedUserId);
		futureTours = FXCollections.observableArrayList(list3);

		List<Tour> list4 = DaoFactory.INSTANCE.getTourDao().getAllToursWhereIAmGuideman(loggedUserId);
		System.out.println(list4.toString());
		toursWhereIAmGuideman = FXCollections.observableArrayList(list4);

	}

	public StringProperty titleProperty() {
		return title;
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public StringProperty bioProperty() {
		return bio;
	}

	public String getBio() {
		return bio.get();
	}

	public void setBio(String bio) {
		this.bio.set(bio);
	}

	public Long getMaxSlots() {
		return maxSlots;
	}

	public ObservableList<Tour> getMyToursModel() {
		return myTours;
	}

	public List<Tour> getMyTours() {
		return new ArrayList<>(myTours);
	}

	public ObservableList<Tour> getPastToursModel() {
		return pastTours;
	}

	public List<Tour> getPastTours() {
		return new ArrayList<>(pastTours);
	}

	public ObservableList<Tour> getFutureToursModel() {
		return futureTours;
	}

	public List<Tour> getFutureTours() {
		return new ArrayList<>(futureTours);
	}

	public ObservableList<Tour> getToursWhereIAmGuidemanModel() {
		return toursWhereIAmGuideman;
	}

	public List<Tour> getToursWhereIAmGuideman() {
		return new ArrayList<>(toursWhereIAmGuideman);
	}

	public Tour getTour() {
		return new Tour(id, getTitle(), getBio(), maxSlots, locationId, guidemanId, image);
	}
}
