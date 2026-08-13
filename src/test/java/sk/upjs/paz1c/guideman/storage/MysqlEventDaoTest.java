package sk.upjs.paz1c.guideman.storage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlEventDaoTest {

	private EventDao eventDao;
	private Event savedEvent;
	private int sizeBeforeSave;

	public MysqlEventDaoTest() {
		DaoFactory.INSTANCE.testing();
		eventDao = DaoFactory.INSTANCE.getEventDao();
	}

	@BeforeEach
	void setUp() throws Exception {
		Event event = new Event();
		event.setDateOfTour(LocalDateTime.parse("2024-02-03T10:00:00"));
		event.setDuration(LocalTime.parse("04:45:00"));
		event.setPrice(30.50);
		event.setTourId(1l); // exploring london
		sizeBeforeSave = eventDao.getAll().size();
		savedEvent = eventDao.save(event);
	}

	@AfterEach
	void tearDown() throws Exception {
		eventDao.delete(savedEvent.getId());
	}

	//// TESTY

	@Test
	void getRatingTest() throws NullPointerException {
		List<Integer> rating1 = eventDao.getRating(1l, 2l);
		assertEquals(rating1.size(), 1);
		List<Integer> rating2 = eventDao.getRating(4l, 3l);
		assertEquals(rating2.size(), 1);
		List<Integer> rating3 = eventDao.getRating(10l, 3l);
		assertTrue(rating3.isEmpty());
	}

	@Test
	void getReviewTest() {
		List<String> review1 = eventDao.getReview(1l, 2l);
		assertEquals(review1.size(), 1);
		List<String> review2 = eventDao.getReview(4l, 3l);
		assertEquals(review2.size(), 1);
		List<String> review3 = eventDao.getReview(10l, 3l);
		assertTrue(review3.isEmpty());

	}

	@Test
	void getRatingsTest() {
		List<Integer> ratings1 = eventDao.getRatings(1l);
		assertEquals(ratings1.size(), 1);
		List<Integer> ratings2 = eventDao.getRatings(10l);
		assertTrue(ratings2.isEmpty());
	}

	@Test
	void getReviewsTest() {
		List<String> reviews1 = eventDao.getReviews(1l);
		assertEquals(reviews1.size(), 1);
		List<String> reviews2 = eventDao.getReviews(10l);
		assertTrue(reviews2.isEmpty());
	}

	@Test
	void getAllTest() {
		List<Event> allEvents = eventDao.getAll();
		int sizeAfter = allEvents.size();
		assertEquals(sizeBeforeSave + 1, sizeAfter);
		assertNotNull(allEvents);
		assertNotNull(allEvents.get(0));
		assertTrue(allEvents.size() > 0);
	}

	@Test
	void getAllByMonthTest() {
		assertThrows(NoSuchElementException.class, () -> eventDao.getAllByMonth(0),
				"Month have to be int between 1 and 12");
		assertThrows(NoSuchElementException.class, () -> eventDao.getAllByMonth(13),
				"Month have to be int between 1 and 12");
		List<Event> events1 = eventDao.getAllByMonth(1);
		assertEquals(events1.size(), 2);
		List<Event> events2 = eventDao.getAllByMonth(10);
		assertTrue(events2.isEmpty());
	}

	@Test
	void getAllEventsWithPriceLowerThanTest() {
		int size = eventDao.getAll().size();
		List<Event> events1 = eventDao.getAllEventsWithPriceLowerThan(100);
		assertEquals(events1.size(), size);
		assertThrows(NoSuchElementException.class, () -> eventDao.getAllEventsWithPriceLowerThan(-1),
				"Price cannot be a negative integer");
		List<Event> events2 = eventDao.getAllEventsWithPriceLowerThan(30);
		assertEquals(events2.size(), 2);
		List<Event> events3 = eventDao.getAllEventsWithPriceLowerThan(0);
		assertTrue(events3.isEmpty());
	}

	@Test
	void getAllEventsByCountryTest() {
		List<Event> events1 = eventDao.getAllEventsByCountry("United Kingdom");
		assertEquals(events1.size(), 4);
		assertNotNull(events1);
		List<Event> events2 = eventDao.getAllEventsByCountry("abc");
		assertTrue(events2.isEmpty());
	}

	@Test
	void getAllEventsByGuidemanTest() {
		List<Event> events1 = eventDao.getAllEventsByGuideman("Alexandra", "Parrow");
		assertEquals(events1.size(), 1);
		List<Event> events2 = eventDao.getAllEventsByGuideman("abc", "abc");
		assertTrue(events2.isEmpty());
		assertThrows(NullPointerException.class, () -> eventDao.getAllEventsByGuideman(null, null),
				"Name and surname cannot be null");
	}

	@Test
	void getAllByTourTest() {
		assertThrows(NullPointerException.class, () -> eventDao.getAllByTour(null), "Tour id cannot be null");
		List<Event> events1 = eventDao.getAllByTour(1l);
		assertEquals(events1.size(), 3);
		List<Event> events2 = eventDao.getAllByTour(10l);
		assertTrue(events2.isEmpty());
	}

	@Test
	void getByIdTest() {
		assertThrows(EntityNotFoundException.class, () -> eventDao.getById(10l), "Event id not in db");
		assertThrows(EntityNotFoundException.class, () -> eventDao.getById(-1l));
		assertThrows(NullPointerException.class, () -> eventDao.getById(null), "Event id cannot be null");
		Event e = eventDao.getById(savedEvent.getId());
		assertEquals(e.getDateOfTour(), savedEvent.getDateOfTour());
		assertNotNull(e);
	}

	@Test
	void getAllLetsGoEventsTest() {
		assertThrows(NullPointerException.class, () -> eventDao.getAllLetsGoEvents(null), "User id cannot be null");
		List<Event> events1 = eventDao.getAllLetsGoEvents(1l);
		assertEquals(events1.size(), 1);
		List<Event> events2 = eventDao.getAllLetsGoEvents(10l);
		assertTrue(events2.isEmpty());
	}

	@Test
	void getAllEventsFromPastTest() {
		assertThrows(NullPointerException.class, () -> eventDao.getAllEventsFromPast(null), "User id cannot be null");
		List<Event> events1 = eventDao.getAllEventsFromPast(1l);
		assertEquals(events1.size(), 1);
		List<Event> events2 = eventDao.getAllEventsFromPast(10l);
		assertTrue(events2.isEmpty());
	}

	@Test
	void getAllEventsFromFutureTest() {
		assertThrows(NullPointerException.class, () -> eventDao.getAllEventsFromFuture(null), "User id cannot be null");
		List<Event> events1 = eventDao.getAllEventsFromFuture(4l);
		assertEquals(events1.size(), 1);
		List<Event> events2 = eventDao.getAllEventsFromFuture(10l);
		assertTrue(events2.isEmpty());
	}

	@Test
	void getAllEventsWhereIAmGuidemanTest() {
		assertThrows(NullPointerException.class, () -> eventDao.getAllEventsWhereIAmGuideman(null),
				"User id cannot be null");
		List<Event> events1 = eventDao.getAllEventsWhereIAmGuideman(1l);
		assertEquals(events1.size(), 1);
		List<Event> events2 = eventDao.getAllEventsFromFuture(10l);
		assertTrue(events2.isEmpty());
	}

	@Test
	void insertTest() {
		assertThrows(NullPointerException.class, () -> eventDao.save(null), "Cannot save null");

		assertEquals(sizeBeforeSave + 1, eventDao.getAll().size());
		assertNotNull(savedEvent.getId());

		assertThrows(NullPointerException.class,
				() -> eventDao.save(new Event(null, LocalTime.parse("08:00:00"), 10.00, 1l)),
				"Cannot save event without date and time of tour");
		assertThrows(NullPointerException.class,
				() -> eventDao.save(new Event(LocalDateTime.parse("2000-02-02T19:00:00"), null, 10.00, 1l)),
				"Cannot save event without duration of tour");
		assertThrows(NullPointerException.class,
				() -> eventDao.save(
						new Event(LocalDateTime.parse("2000-02-02T19:00:00"), LocalTime.parse("08:00:00"), null, 1l)),
				"Cannot save event without price of tour");
		assertThrows(NullPointerException.class, () -> eventDao
				.save(new Event(LocalDateTime.parse("2000-02-02T19:00:00"), LocalTime.parse("08:00:00"), 100.00, null)),
				"Cannot save event without id of tour");

	}

	@Test
	void updateTest() {
		Event updatedEvent = new Event(savedEvent.getId(), LocalDateTime.parse("2022-12-14T09:30:30"),
				LocalTime.parse("06:50:00"), 79.00, 1l);
		Event savedUpdatedEvent = eventDao.save(updatedEvent);
		int sizeUpdate = eventDao.getAll().size();
		assertEquals(savedEvent.getId(), savedUpdatedEvent.getId());
		assertEquals(sizeUpdate, sizeBeforeSave + 1);
		assertThrows(NoSuchElementException.class, () -> eventDao.save(
				new Event(-1l, LocalDateTime.parse("2000-02-02T19:00:00"), LocalTime.parse("08:00:00"), 100.00, 1l)));
	}

	@Test
	void deleteTest() {
		Event eventToDelete = new Event();
		eventToDelete.setDateOfTour(LocalDateTime.parse("2020-03-27T12:20:21"));
		eventToDelete.setDuration(LocalTime.parse("02:00:00"));
		eventToDelete.setPrice(30.00);
		eventToDelete.setTourId(1l);
		Event savedEventToDelete = eventDao.save(eventToDelete);
		int size = eventDao.getAllByTour(1l).size();
		eventDao.delete(savedEventToDelete.getId());
		assertEquals(size - 1, eventDao.getAllByTour(1l).size());
	}

}
