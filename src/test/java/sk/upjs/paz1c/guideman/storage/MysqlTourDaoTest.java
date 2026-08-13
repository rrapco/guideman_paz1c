package sk.upjs.paz1c.guideman.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MysqlTourDaoTest {

	private TourDao tourDao = DaoFactory.INSTANCE.getTourDao();
	private Tour tour;
	private Tour savedTour;
	private int size;

	public MysqlTourDaoTest() {
		DaoFactory.INSTANCE.testing();
		tourDao = DaoFactory.INSTANCE.getTourDao();
	}

	@BeforeEach
	void setUp() throws Exception {

		tour = new Tour();
		tour.setTitle("Nejaky title");
		tour.setBio("Nejake bio");
		tour.setMaxSlots(20L);
		tour.setLocationId(1L);
		tour.setGuidemanId(3L);
		tour.setImage(null);

		savedTour = tourDao.save(tour);
		size = tourDao.getAll().size();

	}

	@AfterEach
	void tearDown() throws Exception {
		tourDao.delete(savedTour.getId());

	}

	@Test
	void getByIdTest() {
		Tour tourById = tourDao.getById(savedTour.getId());
		assertEquals(tourById, tourDao.getById(savedTour.getId()));
		assertNotNull(tourById);

		assertThrows(NoSuchElementException.class, () -> tourDao.getById((long) 20));
	}

	@Test
	void testGetAll() {
		List<Tour> tours = tourDao.getAll();
		assertNotNull(tours);
		assertTrue(tours.size() > 0);
		assertNotNull(tours.get(0));

	}

	@Test
	void getAllToursByGuidemanTest() {
		List<Tour> toursByGuideman = tourDao.getAllToursByGuideman(2L);
		assertTrue(toursByGuideman.size() >= 1);

		assertNotNull(toursByGuideman);

		List<Tour> toursByGuideman2 = tourDao.getAllToursByGuideman(30L);
		assertTrue(toursByGuideman2.size() == 0);
		assertTrue(toursByGuideman2.isEmpty());

		assertThrows(NullPointerException.class, () -> tourDao.getAllToursByGuideman(null));

	}

	@Test
	void getAllToursFromPastTestTest() {
		List<Tour> toursFromPast = tourDao.getAllToursFromPast(1L);
		assertTrue(toursFromPast.size() > 0);

		assertNotNull(toursFromPast);
		assertNotNull(toursFromPast.get(0));

		List<Tour> toursFromPast2 = tourDao.getAllToursFromPast(20L);
		assertTrue(toursFromPast2.size() == 0);
		assertNotNull(toursFromPast2);
		assertTrue(toursFromPast2.isEmpty());

		assertThrows(NullPointerException.class, () -> tourDao.getAllToursFromPast(null));

	}

	@Test
	void getAllToursFromFutureTest() {
		List<Tour> toursFromFuture = tourDao.getAllToursFromFuture(4l);
		assertTrue(toursFromFuture.size() >= 1);
		assertNotNull(toursFromFuture);
		assertNotNull(toursFromFuture.get(0));

		List<Tour> toursFromFuture2 = tourDao.getAllToursFromFuture(50l);
		assertTrue(toursFromFuture2.size() == 0);

		assertNotNull(toursFromFuture2);
		assertTrue(toursFromFuture2.isEmpty());

		assertThrows(NullPointerException.class, () -> tourDao.getAllToursFromFuture(null));

	}

	@Test
	void getAllLetsGoToursTest() {
		List<Tour> getAllLetsGoTours = tourDao.getAllLetsGoTours(1l);
		assertTrue(getAllLetsGoTours.size() > 0);
		assertNotNull(getAllLetsGoTours);
		assertNotNull(getAllLetsGoTours.get(0));

		List<Tour> getAllLetsGoTours2 = tourDao.getAllLetsGoTours(6l);
		assertTrue(getAllLetsGoTours2.size() == 0);
		assertTrue(getAllLetsGoTours2.isEmpty());

		assertThrows(NullPointerException.class, () -> tourDao.getAllLetsGoTours(null));

	}

	@Test
	void getAllToursByLocationTest() {
		List<Tour> getAllToursByLocation = tourDao.getAllToursByLocation(1l);
		assertTrue(getAllToursByLocation.size() >= 2);
		assertNotNull(getAllToursByLocation);
		assertNotNull(getAllToursByLocation.get(0));

		List<Tour> getAllToursByLocation2 = tourDao.getAllToursByLocation(6l);
		assertTrue(getAllToursByLocation2.size() == 0);
		assertTrue(getAllToursByLocation2.isEmpty());

		assertThrows(NullPointerException.class, () -> tourDao.getAllToursByLocation(null));

	}

	@Test
	void getAllToursWhereIAmGuidemanTest() {
		List<Tour> getAllToursWhereIAmGuideman = tourDao.getAllToursWhereIAmGuideman(3l);
		assertTrue(getAllToursWhereIAmGuideman.size() > 0);
		assertNotNull(getAllToursWhereIAmGuideman);
		assertNotNull(getAllToursWhereIAmGuideman.get(0));

		List<Tour> getAllToursWhereIAmGuideman2 = tourDao.getAllToursWhereIAmGuideman(6l);
		assertTrue(getAllToursWhereIAmGuideman2.size() == 0);
		assertTrue(getAllToursWhereIAmGuideman2.isEmpty());

		assertThrows(NullPointerException.class, () -> tourDao.getAllToursWhereIAmGuideman(null));

	}

	@Test
	void insertTest() {
		assertThrows(NullPointerException.class, () -> tourDao.save(null), "Cannot save null");

		Tour newLocation = tourDao.getById(savedTour.getId());
		assertEquals(size, tourDao.getAll().size());
		assertEquals(savedTour.getTitle(), newLocation.getTitle());

		assertThrows(NullPointerException.class, () -> tourDao.save(new Tour("Title", "bio", 50L, null, 1L, null)),
				"Location_id cannot be null");

		assertThrows(NullPointerException.class, () -> tourDao.save(new Tour("Title", "bio", 50L, 1L, null, null)),
				"Guideman (user_id) cannot be null");

		assertThrows(NullPointerException.class, () -> tourDao.save(new Tour(null, "bio", 50L, 1L, 1L, null)),
				"Title cannot be null");

		assertThrows(NullPointerException.class, () -> tourDao.save(new Tour("Title", "bio", null, 1L, 1L, null)),
				"Max slots cannot be null");

	}

	@Test
	void updateTest() {
		Tour updated = new Tour(savedTour.getId(), "Changed title", "bio", 2L, 1L, 1L, null);
		int sizeUpdate = tourDao.getAll().size();
		assertEquals(sizeUpdate, tourDao.getAll().size());
		Tour fromDB = tourDao.getById(updated.getId());
		assertEquals(updated.getId(), fromDB.getId());
		assertThrows(EntityNotFoundException.class,
				() -> tourDao.save(new Tour(-1L, "Changed title", "bio", 2L, 1L, 1L, null)));

	}

	@Test
	void deleteTest() {
		Tour tourToDelete = new Tour();
		tourToDelete.setTitle("title");
		tourToDelete.setBio("bio");
		tourToDelete.setMaxSlots(3L);
		tourToDelete.setLocationId(1L);
		tourToDelete.setGuidemanId(2L);
		tourToDelete.setImage(null);

		Tour saved = tourDao.save(tourToDelete);
		int sizeDelete = tourDao.getAll().size();
		tourDao.delete(saved.getId());
		assertEquals(sizeDelete - 1, tourDao.getAll().size());

		assertThrows(NullPointerException.class, () -> tourDao.delete(tourToDelete.getId()));

	}

}
