package sk.upjs.paz1c.guideman.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface LocationDao {

	List<Location> getAll();

	Location getById(long id) throws NoSuchElementException;

	Location save(Location location) throws NullPointerException, EntityNotFoundException;

	boolean delete(Long locationId) throws NullPointerException, NoSuchElementException;

	List<Location> getAllByCountry(String country) throws NullPointerException;
}
