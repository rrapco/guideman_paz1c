package sk.upjs.paz1c.guideman.controllers;

import sk.upjs.paz1c.guideman.storage.Event;
import sk.upjs.paz1c.guideman.storage.Tour;

public enum ShowTour {

	INSTANCE;

	private Tour loggedTour;
	private Event loggedEvent;

	public Tour getLoggedTour() {
		return loggedTour;
	}

	public Event getLoggedEvent() {
		return loggedEvent;
	}

	public void setLoggedTour(Tour loggedTour) {
		System.out.println("Setting tour");
		this.loggedTour = loggedTour;
	}

	public void setLoggedEvent(Event loggedEvent) {
		System.out.println("Setting event");
		this.loggedEvent = loggedEvent;
	}

}
