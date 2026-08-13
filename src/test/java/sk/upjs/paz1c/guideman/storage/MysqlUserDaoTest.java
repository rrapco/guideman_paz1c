package sk.upjs.paz1c.guideman.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

class MysqlUserDaoTest {

	private UserDao userDao;
	private User savedUser;
	private int size;
	private EventDao eventDao;

	public MysqlUserDaoTest() {
		DaoFactory.INSTANCE.testing();
		userDao = DaoFactory.INSTANCE.getUserDao();
		eventDao = DaoFactory.INSTANCE.getEventDao();
	}

	@BeforeEach
	void setUp() throws Exception {
		User user = new User();
		user.setName("Test Testing");
		user.setSurname("Test Testing");
		user.setEmail("Test Testing");
		user.setTelNumber(null);
		user.setBirthdate(LocalDate.parse("2022-02-02"));
		user.setLogin("test.testing");
		user.setPassword("testtesting");
		user.setImage(null);
		size = userDao.getAll().size();
		savedUser = userDao.save(user);
	}

	@AfterEach
	void tearDown() throws Exception {
		userDao.delete(savedUser.getId());
	}

	@Test
	void getAllTest() {
		List<User> users = userDao.getAll();
		assertTrue(users.size() > 0);
		assertNotNull(users.get(0).getId());
		assertNotNull(users.get(0).getName());
		assertNotNull(users.get(0).getSurname());
	}

	@Test
	void getByIdTest() {
		User fromDB = userDao.getById(savedUser.getId());
		assertEquals(savedUser.getId(), fromDB.getId());
		assertThrows(EntityNotFoundException.class, () -> userDao.getById(-1L));
	}

	@Test
	void getAllTouristsTest() {
		List<User> tourists1 = userDao.getAllTourists(2l);
		assertEquals(tourists1.size(), 1);
		assertNotNull(tourists1);
		List<User> tourists2 = userDao.getAllTourists(10l);
		assertTrue(tourists2.isEmpty());

	}

	@Test
	void getAllGuidemansTest() {
		List<User> guidemans = userDao.getAllGuidemans();
		assertEquals(guidemans.size(), 3);
		assertNotNull(guidemans);
		assertTrue(guidemans.size() > 0);
		
	}
	
	@Test
	void saveUserEventTest() {
		assertThrows(NullPointerException.class, () -> userDao.saveUserEvent(null, null));
		
		int sizeBefore = eventDao.getAllLetsGoEvents(savedUser.getId()).size();
		Event event = new Event();
		event.setDateOfTour(LocalDateTime.parse("2023-02-03T10:00:00"));
		event.setDuration(LocalTime.parse("04:00:00"));
		event.setPrice(30.0);
		event.setTourId(1l);
		Event savedEvent2 = eventDao.save(event);
		userDao.saveUserEvent(savedUser.getId(), savedEvent2.getId());
		int sizeAfter = eventDao.getAllLetsGoEvents(savedUser.getId()).size();
		assertEquals(sizeBefore + 1, sizeAfter);
		eventDao.deleteFromUHE(savedEvent2.getId());
		int sizeAfterDelete = eventDao.getAllLetsGoEvents(savedUser.getId()).size();
		assertEquals(sizeBefore, sizeAfterDelete);
		eventDao.delete(savedEvent2.getId());
		
	}
	
	@Test
	void getUserByUsernameTest() {
		assertThrows(EntityNotFoundException.class, () -> userDao.getUserByUsername(null));
		assertThrows(EntityNotFoundException.class, () -> userDao.getUserByUsername("abc"));
		User u = userDao.getUserByUsername("alex");
		assertEquals(u.getLogin(), "alex");
		assertNotNull(u);
	}
	
	@Test
	void insertTest() {
		assertThrows(NullPointerException.class, () -> userDao.save(null), "Cannot save null");

		User saved2 = userDao.getById(savedUser.getId());
		assertTrue(BCrypt.checkpw("testtesting", saved2.getPassword()));

		assertEquals(size + 1, userDao.getAll().size());
		assertNotNull(savedUser.getId());
		assertEquals(savedUser.getName(), saved2.getName());

		assertThrows(NullPointerException.class, () -> userDao.save(new User("testTesting", "password")),
				"This username is already used");

		assertThrows(NullPointerException.class,
				() -> userDao
						.save(new User(null, "surname", "email", "telnumber", LocalDate.parse("2022-02-02"), null)),
				"Name cannot be null");
		assertThrows(NullPointerException.class,
				() -> userDao.save(new User("name", null, "email", null, LocalDate.parse("2022-02-02"), null)),
				"Surname cannot be null");
		assertThrows(NullPointerException.class,
				() -> userDao.save(new User("name", "surname", null, null, LocalDate.parse("2022-02-02"), null)),
				"Email address cannot be null");
		assertThrows(NullPointerException.class,
				() -> userDao.save(new User("name", "surname", "email", "telnumber", null, null)),
				"Birthdate cannot be null");
		assertThrows(NullPointerException.class, () -> userDao.save(
				new User("name", "surname", "email", null, LocalDate.parse("2022-02-02"), null, "password", null)),
				"Login cannot be null");
		assertThrows(NullPointerException.class,
				() -> userDao.save(
						new User("name", "surname", "email", null, LocalDate.parse("2022-02-02"), "login", null, null)),
				"Password cannot be null");
	}

	@Test
	void updateTest() {
		User updated = new User(savedUser.getId(), "Changed Name", "surname", "email", "telnumber",
				LocalDate.parse("2022-02-02"), null);
		int sizeUpdate = userDao.getAll().size();
		assertEquals(sizeUpdate, userDao.getAll().size());
		User fromDB = userDao.getById(updated.getId());
		assertEquals(updated.getId(), fromDB.getId());
		assertThrows(NullPointerException.class, () -> userDao.save(
				new User(-1L, "Changed Name", "surname", "email", "telnumber", LocalDate.parse("2022-02-02"), null)));

	}

	@Test
	void deleteTest() {
		User userToDelete = new User();
		userToDelete.setName("Test Delete");
		userToDelete.setSurname("Delete");
		userToDelete.setEmail("testdelete");
		userToDelete.setTelNumber("0987654321");
		userToDelete.setBirthdate(LocalDate.parse("2022-10-19"));
		userToDelete.setLogin("test.delete");
		userToDelete.setPassword("testingdelete");
		String salt = BCrypt.gensalt();
		userToDelete.setPassword(BCrypt.hashpw("testDelete", salt));
		userToDelete.setImage(null);
		User saved = userDao.save(userToDelete);
		int sizeDelete = userDao.getAll().size();
		userDao.delete(saved.getId());
		assertEquals(sizeDelete - 1, userDao.getAll().size());
	}

	@Test
	void saveAndDeleteRatingTest() {
		assertThrows(NullPointerException.class, () -> userDao.saveRating(null, null, 0));
		assertThrows(NullPointerException.class, () -> userDao.deleteRating(null, null));

		int sizeBefore = eventDao.getAllLetsGoEvents(savedUser.getId()).size();
		Event event = new Event();
		event.setDateOfTour(LocalDateTime.parse("2023-02-03T10:00:00"));
		event.setDuration(LocalTime.parse("04:00:00"));
		event.setPrice(30.0);
		event.setTourId(1l);
		Event savedEvent2 = eventDao.save(event);
		userDao.saveUserEvent(savedUser.getId(), savedEvent2.getId());
		int ratingSizeBefore = eventDao.getRatings(savedEvent2.getTourId()).size();
		userDao.saveRating(savedUser.getId(), savedEvent2.getId(), 5);
		int ratingSizeAfter = eventDao.getRatings(savedEvent2.getTourId()).size();
		assertEquals(ratingSizeBefore + 1, ratingSizeAfter);
		userDao.deleteRating(savedUser.getId(), savedEvent2.getId());
		int ratingSizeAfterDelete = eventDao.getRatings(savedEvent2.getTourId()).size();
		assertEquals(ratingSizeAfterDelete, ratingSizeBefore);
		eventDao.deleteFromUHE(savedEvent2.getId());
		int sizeAfterDelete = eventDao.getAllLetsGoEvents(savedUser.getId()).size();
		assertEquals(sizeBefore, sizeAfterDelete);
		eventDao.delete(savedEvent2.getId());
	}

	@Test
	void saveAndDeleteReviewsTest() {
		assertThrows(NullPointerException.class, () -> userDao.saveReview(null, null, "bla"));
		assertThrows(NullPointerException.class, () -> userDao.deleteReview(null, null));
		
		int sizeBefore = eventDao.getAllLetsGoEvents(savedUser.getId()).size();
		Event event = new Event();
		event.setDateOfTour(LocalDateTime.parse("2023-02-03T10:00:00"));
		event.setDuration(LocalTime.parse("04:00:00"));
		event.setPrice(30.0);
		event.setTourId(1l);
		Event savedEvent2 = eventDao.save(event);
		userDao.saveUserEvent(savedUser.getId(), savedEvent2.getId());
		int reviewSizeBefore = eventDao.getReviews(savedEvent2.getTourId()).size();
		userDao.saveReview(savedUser.getId(), savedEvent2.getId(), "nice");
		int reviewSizeAfter = eventDao.getReviews(savedEvent2.getTourId()).size();
		assertEquals(reviewSizeBefore + 1, reviewSizeAfter);
		userDao.deleteReview(savedUser.getId(), savedEvent2.getId());
		int reviewSizeAfterDelete = eventDao.getReviews(savedEvent2.getTourId()).size();
		assertEquals(reviewSizeAfterDelete, reviewSizeBefore);
		eventDao.deleteFromUHE(savedEvent2.getId());
		int sizeAfterDelete = eventDao.getAllLetsGoEvents(savedUser.getId()).size();
		assertEquals(sizeBefore, sizeAfterDelete);
		eventDao.delete(savedEvent2.getId());

	}

}
