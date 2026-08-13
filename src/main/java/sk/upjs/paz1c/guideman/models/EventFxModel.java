package sk.upjs.paz1c.guideman.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sk.upjs.paz1c.guideman.controllers.LoggedUser;
import sk.upjs.paz1c.guideman.storage.DaoFactory;
import sk.upjs.paz1c.guideman.storage.Event;
import sk.upjs.paz1c.guideman.storage.Tour;

public class EventFxModel {

	private Long id;
	private ObjectProperty<LocalDateTime> dateOfTour;
	private ObjectProperty<LocalTime> duration;
	private Double price;
	private Long tourId;

	private ObservableList<Event> myEvents;
	private ObservableList<Event> pastEvents;
	private ObservableList<Event> futureEvents;
	private ObservableList<Event> eventsWhereIAmGuideman;

	public EventFxModel(Event event) {
		this.id = event.getId();
		dateOfTour.set(event.getDateOfTour());
		duration.set(event.getDuration());
		this.price = event.getPrice();
		this.tourId = event.getTourId();

		Long loggedUserId = LoggedUser.INSTANCE.getLoggedUser().getId();

		List<Event> list1 = DaoFactory.INSTANCE.getEventDao().getAllLetsGoEvents(loggedUserId);
		myEvents = FXCollections.observableArrayList(list1);

		List<Event> list2 = DaoFactory.INSTANCE.getEventDao().getAllEventsFromPast(loggedUserId);
		pastEvents = FXCollections.observableArrayList(list2);

		List<Event> list3 = DaoFactory.INSTANCE.getEventDao().getAllEventsFromFuture(loggedUserId);
		futureEvents = FXCollections.observableArrayList(list3);

		List<Event> list4 = DaoFactory.INSTANCE.getEventDao().getAllEventsWhereIAmGuideman(loggedUserId);
		eventsWhereIAmGuideman = FXCollections.observableArrayList(list4);

	}

	public EventFxModel() {
	}

	public ObjectProperty<LocalDateTime> dateOfTourOP() {
		return dateOfTour;
	}

	public LocalDateTime getDateOfTour() {
		return dateOfTour.get();
	}

	public void setDate(LocalDateTime datetime) {
		dateOfTour.set(datetime);
	}

	public ObjectProperty<LocalTime> duration() {
		return duration;

	}

	public LocalTime getDuration() {
		return duration.get();
	}

	public void setDuration(LocalTime duration1) {
		this.duration.set(duration1);
	}

	public ObservableList<Event> getMyEventModel() {
		return myEvents;
	}

	public List<Event> getMyEvents() {
		return new ArrayList<>(myEvents);
	}

	public ObservableList<Event> getPastEventModel() {
		return pastEvents;
	}

	public List<Event> getPastEvents() {
		return new ArrayList<>(pastEvents);
	}

	public ObservableList<Event> getFutureEventsModel() {
		return futureEvents;
	}

	public List<Event> getFutureEvents() {
		return new ArrayList<>(futureEvents);
	}

	public ObservableList<Event> getEventsWhereIAmGuidemanModel() {
		return eventsWhereIAmGuideman;
	}

	public List<Event> getToursWhereIAmGuideman() {
		return new ArrayList<>(eventsWhereIAmGuideman);
	}

	public Event getEvent() {
		return new Event(id, dateOfTour.get(), duration.get(), price, tourId);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Long getTourId() {
		return tourId;
	}

	public void setTourId(Long tourId) {
		this.tourId = tourId;
	}

}
