package sk.upjs.paz1c.guideman.storage;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Event {

	public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	private Long id;
	private LocalDateTime dateOfTour;
	private LocalTime duration;
	private Double price;
	private Long tourId;
	private List<User> tourists;

	private List<Integer> ratings;
	private List<String> reviews;

	public Event() {
	}

	public Event(Long tourId) {
		this.tourId = tourId;
	}

	public Event(LocalDateTime dateOfTour, LocalTime duration, Double price, Long tourId) {
		this.dateOfTour = dateOfTour;
		this.duration = duration;
		this.price = price;
		this.tourId = tourId;
	}

	public Event(Long id, LocalDateTime dateOfTour, LocalTime duration, Double price, Long tourId) {
		this.id = id;
		this.dateOfTour = dateOfTour;
		this.duration = duration;
		this.price = price;
		this.tourId = tourId;
	}

	public Event(Long id, LocalDateTime dateOfTour, LocalTime duration, Double price, Long tourId,
			List<User> tourists) {
		this.id = id;
		this.dateOfTour = dateOfTour;
		this.duration = duration;
		this.price = price;
		this.tourId = tourId;
		if (tourists != null) {
			this.tourists = tourists;
		}
	}

	public Event(Long id, LocalDateTime dateOfTour, LocalTime duration, Double price, Long tourId, List<User> tourists,
			List<Integer> ratings, List<String> reviews) {
		this.id = id;
		this.dateOfTour = dateOfTour;
		this.duration = duration;
		this.price = price;
		this.tourId = tourId;
		this.tourists = tourists;
		this.ratings = ratings;
		this.reviews = reviews;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDateOfTour() {
		return dateOfTour;
	}

	public void setDateOfTour(LocalDateTime dateOfTour) {
		this.dateOfTour = dateOfTour;
	}

	public LocalTime getDuration() {
		return duration;
	}

	public void setDuration(LocalTime duration) {
		this.duration = duration;
	}

	public double getPrice() {
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

	public List<User> getTourists() {
		return tourists;
	}

	public void setTourists(List<User> tourists) {
		this.tourists = tourists;
	}

	public List<Integer> getRatings() {
		return ratings;
	}

	public void setRatings(List<Integer> ratings) {
		this.ratings = ratings;
	}

	public List<String> getReviews() {
		return reviews;
	}

	public void setReviews(List<String> reviews) {
		this.reviews = reviews;
	}

	@Override
	public String toString() {
		return dateOfTour.format(formatter);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateOfTour, duration, id, price, ratings, reviews, tourId, tourists);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		return Objects.equals(dateOfTour, other.dateOfTour) && Objects.equals(duration, other.duration)
				&& Objects.equals(id, other.id) && Objects.equals(price, other.price)
				&& Objects.equals(ratings, other.ratings) && Objects.equals(reviews, other.reviews)
				&& Objects.equals(tourId, other.tourId) && Objects.equals(tourists, other.tourists);
	}
}
