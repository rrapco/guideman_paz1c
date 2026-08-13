package sk.upjs.paz1c.guideman.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface UserDao {

	List<User> getAll();

	User getById(long id) throws EntityNotFoundException;

	List<User> getAllTourists(long eventId);

	List<User> getAllGuidemans();
	
	void saveUserEvent(Long userId, Long eventId) throws NullPointerException;
	
	User getUserByUsername(String username) throws EntityNotFoundException;

	User save(User user) throws NullPointerException, EntityNotFoundException;

	boolean delete(long id) throws EntityNotFoundException;

	void saveRating(Long userId, Long eventId, Integer rating) throws NullPointerException;

	void saveReview(Long userId, Long eventId, String review) throws NullPointerException;

	void deleteRating(Long userId, Long eventId) throws NullPointerException;

	void deleteReview(Long userId, Long eventId) throws NullPointerException;



}
