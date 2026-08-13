package sk.upjs.paz1c.guideman.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface EventDao {

	List<Integer> getRating(Long userId, Long eventId);

	List<String> getReview(Long userId, Long eventId);

	List<Integer> getRatings(Long tourId);

	List<String> getReviews(Long tourId);

	List<Event> getAll();

	List<Event> getAllByMonth(int month) throws NoSuchElementException;

	List<Event> getAllEventsWithPriceLowerThan(int price) throws NoSuchElementException;

	List<Event> getAllEventsByCountry(String country);
	
	List<Event> getAllEventsByGuideman(String name, String surname) throws NullPointerException;

	List<Event> getAllByTour(Long tourId);
	
	Event getById(Long eventId) throws EntityNotFoundException, NullPointerException;
	
	List<Event> getAllLetsGoEvents(Long userId) throws NullPointerException;
	
	List<Event> getAllEventsFromPast(Long userId) throws NullPointerException;
	
	List<Event> getAllEventsFromFuture(Long userId) throws NullPointerException;
	
	List<Event> getAllEventsWhereIAmGuideman(Long userId) throws NullPointerException;

	Event save(Event event) throws NullPointerException, NegativeNumberException, NoSuchElementException;

	boolean delete(Long eventId) throws EntityNotFoundException;

	boolean deleteFromUHE(Long eventId) throws EntityNotFoundException;

	

}
