package sk.upjs.paz1c.guideman.storage;

import java.util.List;
import java.util.NoSuchElementException;

public interface TourDao {

	List<Tour> getAllToursByGuideman(Long guidemanId) throws NoSuchElementException;

	List<Tour> getAll();

	Tour getById(Long tourId) throws NoSuchElementException;

	Tour save(Tour tour) throws NullPointerException, EntityNotFoundException;

	boolean delete(long tourId) throws NullPointerException, EntityNotFoundException;

	List<Tour> getAllToursByLocation(Long locationId) throws NoSuchElementException;

	List<Tour> getAllToursFromPast(Long userId) throws NoSuchElementException;

	List<Tour> getAllToursFromFuture(Long userId) throws NoSuchElementException;

	List<Tour> getAllToursWhereIAmGuideman(Long userId) throws NoSuchElementException;

	List<Tour> getAllLetsGoTours(Long userId) throws NoSuchElementException;

}
