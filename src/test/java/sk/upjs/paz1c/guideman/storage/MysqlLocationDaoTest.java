package sk.upjs.paz1c.guideman.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class MysqlLocationDaoTest {

	private LocationDao locationDao;
	private Location location;
	private Location savedLocation;
	private Location saved1Location;
	private Location saved2Location;
	private Location saved3Location;
	private int size;

	public MysqlLocationDaoTest() {
		DaoFactory.INSTANCE.testing();
		locationDao = DaoFactory.INSTANCE.getLocationDao();
	}

	@BeforeEach
	void setUp() throws Exception {
		location = new Location();
		location.setCountry("Amerika");
		location.setCity("Los Angeles");
		location.setStreet("Main street");
		location.setStreet_number((int) 1);

		size = locationDao.getAll().size(); // pocet userov pred pridanim noveho
		savedLocation = locationDao.save(location);
		size = locationDao.getAll().size();
	}

	@AfterEach
	void tearDown() throws Exception {
		locationDao.delete(savedLocation.getId());
	}

	@Test
	void testGetById() {
		Location fromDB = locationDao.getById(savedLocation.getId());
		assertEquals(savedLocation.getId(), fromDB.getId());
		assertThrows(NoSuchElementException.class, new Executable() {

			@Override
			public void execute() throws Throwable {
				locationDao.getById(-1L);
			}
		});
		// assertThrows(NoSuchElementException.class, () -> locationDao.getById(-1));
	}

	// TODO insert, update a delete

	@Test
	void insertTest() {
		assertThrows(NullPointerException.class, () -> locationDao.save(null), "Cannot save null");

		Location newLocation = locationDao.getById(savedLocation.getId());
		assertEquals(size, locationDao.getAll().size());
		assertEquals(savedLocation.getCountry(), newLocation.getCountry());

		assertThrows(NullPointerException.class, () -> locationDao.save(new Location(null, "City", "Street", (int) 2)),
				"Country cannot be null");

		assertThrows(NullPointerException.class,
				() -> locationDao.save(new Location("Country", null, "Street", (int) 2)), "City cannot be null");

		assertThrows(NullPointerException.class, () -> locationDao.save(new Location("Country", "City", null, (int) 2)),
				"Street cannot be null");
	}

	@Test
	void updateTest() {
		Location updated = new Location(savedLocation.getId(), "Changed Country", "city", "street", (int) 1);
		int sizeUpdate = locationDao.getAll().size();
		locationDao.save(updated);
		assertEquals(sizeUpdate, locationDao.getAll().size());
		Location fromDB = locationDao.getById(updated.getId());
		assertEquals(updated.getId(), fromDB.getId());
		assertThrows(EntityNotFoundException.class,
				() -> locationDao.save(new Location(-1L, "Changed Country", "city", "street", (int) 1)));
	}

	@Test
	void deleteTest() {
		Location locationToDelete = new Location();
		locationToDelete.setCountry("Delete Country");
		locationToDelete.setCity("Delete City");
		locationToDelete.setStreet("Delete Street");
		locationToDelete.setStreet_number((int) 1);

		// without id, id == null
		Location nullLocation = new Location();
		nullLocation.setCountry("Delete Country");
		nullLocation.setCity("Delete City");
		nullLocation.setStreet("Delete Street");
		nullLocation.setStreet_number((int) 1);

		Location saved = locationDao.save(locationToDelete);
		int sizeDelete = locationDao.getAll().size();
		assertEquals(sizeDelete, locationDao.getAll().size());
		locationDao.delete(saved.getId());
		assertEquals(sizeDelete - 1, locationDao.getAll().size());

		assertThrows(EntityNotFoundException.class, () -> locationDao.delete(saved.getId()));
		assertThrows(NullPointerException.class, () -> locationDao.delete(null));

	}

	@Test
	void testGetAll() {
		List<Location> locations = locationDao.getAll();
		assertTrue(locations.size() > 0);
		assertNotNull(locations);
		assertNotNull(locations.get(0));
	}

	@Test
	void getAllByCountryTest() {
		Location location1 = new Location();
		location1.setCountry("Random");
		location1.setCity("Košice");
		location1.setStreet("Hlavná");
		location1.setStreet_number((int) 1);

		Location location2 = new Location();
		location2.setCountry("Random");
		location2.setCity("Budapeš");
		location2.setStreet("Main street");
		location2.setStreet_number((int) 1);

		Location location3 = new Location();
		location3.setCountry("NotRandom");
		location3.setCity("Bratislava");
		location3.setStreet("Rudolfovo námestie");
		location3.setStreet_number((int) 1);

		int sizeBeforeSave = locationDao.getAll().size();

		System.out.println(location1);
		System.out.println(location2);
		System.out.println(location3);

		saved1Location = locationDao.save(location1);
		saved2Location = locationDao.save(location2);
		saved3Location = locationDao.save(location3);

		int sizeAfterSave = locationDao.getAll().size();

		assertEquals(sizeBeforeSave + 3, sizeAfterSave);

		assertEquals(2, locationDao.getAllByCountry("Random").size());
		assertEquals(1, locationDao.getAllByCountry("Amerika").size());

		locationDao.delete(saved1Location.getId());
		locationDao.delete(saved2Location.getId());
		locationDao.delete(saved3Location.getId());

		assertNotEquals(sizeAfterSave, locationDao.getAll().size());
		assertEquals(sizeAfterSave - 3, locationDao.getAll().size());

		assertThrows(NullPointerException.class, () -> locationDao.getAllByCountry(null));

	}

}
