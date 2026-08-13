package sk.upjs.paz1c.guideman.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class MysqlEventDao implements EventDao {

	private JdbcTemplate jdbcTemplate;

	public MysqlEventDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Integer> getRating(Long userId, Long eventId) {
		String sql = "SELECT rating FROM user_has_event " + "WHERE user_id = " + userId + " AND event_id = " + eventId;
		return jdbcTemplate.query(sql, new ResultSetExtractor<List<Integer>>() {

			@Override
			public List<Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Integer> rating = new ArrayList<>();
				while (rs.next()) {
					rating.add(rs.getInt("rating"));
				}
				return rating;
			}
		});
	}

	@Override
	public List<String> getReview(Long userId, Long eventId) {
		String sql = "SELECT review FROM user_has_event " + "WHERE user_id = " + userId + " AND event_id = " + eventId;
		return jdbcTemplate.query(sql, new ResultSetExtractor<List<String>>() {

			@Override
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> review = new ArrayList<>();
				while (rs.next()) {
					review.add(rs.getString("review"));
				}
				return review;
			}
		});

	}

	@Override
	public List<Integer> getRatings(Long tourId) {
		String sql = "SELECT uhe.rating FROM user_has_event uhe " + "JOIN event e ON uhe.event_id = e.id "
				+ "JOIN tour t ON e.tour_id = t.id " + "WHERE t.id = " + tourId + " AND uhe.rating IS NOT NULL";
		return jdbcTemplate.query(sql, new ResultSetExtractor<List<Integer>>() {

			@Override
			public List<Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Integer> ratings = new ArrayList<>();
				while (rs.next()) {
					Integer rat = rs.getInt("rating");
					if (rat == 0 || rat == 1 || rat == 2 || rat == 3 || rat == 4 || rat == 5) {
						ratings.add(rat);
					}
				}
				return ratings;
			}
		});
	}

	@Override
	public List<String> getReviews(Long tourId) {
		String sql = "SELECT uhe.review FROM user_has_event uhe " + "JOIN event e ON uhe.event_id = e.id "
				+ "JOIN tour t ON e.tour_id = t.id " + "WHERE t.id = " + tourId + " AND uhe.review IS NOT NULL";
		return jdbcTemplate.query(sql, new ResultSetExtractor<List<String>>() {

			@Override
			public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<String> reviews = new ArrayList<>();
				while (rs.next()) {
					String rev = rs.getString("review");
					reviews.add(rev);
				}
				return reviews;
			}
		});
	}

	@Override
	public List<Event> getAll() {
		String sql = "SELECT id, date_of_tour, duration, price, tour_id FROM event";
		return jdbcTemplate.query(sql, new EventRowMapper());
	}

	@Override
	public List<Event> getAllByMonth(int month) throws NoSuchElementException {
		if (month <= 0 || month > 12) {
			throw new NoSuchElementException("Month have to be int between 1 and 12");
		}
		String sql = "SELECT id, date_of_tour, duration, price, tour_id FROM event " + "WHERE MONTH(date_of_tour) = "
				+ month;
		return jdbcTemplate.query(sql, new EventRowMapper());
	}

	@Override
	public List<Event> getAllEventsWithPriceLowerThan(int price) throws NoSuchElementException {
		if (price < 0) {
			throw new NoSuchElementException("Price cannot be a negative integer");
		}
		String sql = "";
		if (price == 100) {
			sql = "SELECT id, date_of_tour, duration, price, tour_id FROM event";
		} else {
			sql = "SELECT id, date_of_tour, duration, price, tour_id FROM event " + "WHERE price <= " + price;
		}
		return jdbcTemplate.query(sql, new EventRowMapper());
	}

	@Override
	public List<Event> getAllEventsByCountry(String country) {
		String sql = "SELECT e.id, e.date_of_tour, e.duration, e.price, e.tour_id FROM event e "
				+ "JOIN tour t ON e.tour_id = t.id " + "JOIN location l ON t.location_id = l.id "
				+ "WHERE l.country = ? ";
		return jdbcTemplate.query(sql, new EventRowMapper(), country);
	}

	@Override
	public List<Event> getAllEventsByGuideman(String name, String surname) throws NullPointerException {
		if (name == null || surname == null) {
			throw new NullPointerException("Name and surname cannot be null");
		}
		String sql = "SELECT e.id, e.date_of_tour, e.duration, e.price, e.tour_id FROM event e "
				+ "JOIN tour t ON e.tour_id = t.id " + "JOIN user u ON t.user_id = u.id "
				+ "WHERE u.name =? AND u.surname =?";
		return jdbcTemplate.query(sql, new EventRowMapper(), name, surname);
	}

	@Override
	public List<Event> getAllByTour(Long tourId) {
		if (tourId == null) {
			throw new NullPointerException("Tour id cannot be null");
		}
		String sql = "SELECT id, date_of_tour, duration, price, tour_id FROM event "
				+ "LEFT JOIN user_has_event uhe ON event.id = uhe.event_id " + "WHERE tour_id = " + tourId;

		return jdbcTemplate.query(sql, new ResultSetExtractor<List<Event>>() {

			@Override
			public List<Event> extractData(ResultSet rs) throws SQLException, DataAccessException {
				List<Event> events = new ArrayList<>();

				Event lastEvent = null;
				while (rs.next()) {
					long id = rs.getLong("id");
					if (lastEvent == null || lastEvent.getId() != id) {
						lastEvent = new Event();
						lastEvent.setId(id);
						lastEvent.setDateOfTour(rs.getTimestamp("date_of_tour").toLocalDateTime());
						lastEvent.setDuration(rs.getTimestamp("duration").toLocalDateTime().toLocalTime());
						lastEvent.setPrice(rs.getDouble("price"));
						lastEvent.setTourId(rs.getLong("tour_id"));
						lastEvent.setTourists(DaoFactory.INSTANCE.getUserDao().getAllTourists(id));
						lastEvent.setRatings(getRatings(id));
						lastEvent.setReviews(getReviews(id));
						events.add(lastEvent);
					}
				}
				return events;
			}

		});
	}

	@Override
	public Event getById(Long eventId) throws EntityNotFoundException, NullPointerException {
		if (eventId == null) {
			throw new NullPointerException("Event id cannot be null");
		}
		String sql = "SELECT id, date_of_tour, duration, price, tour_id FROM event " + " WHERE id = ?";
		try {
			return jdbcTemplate.queryForObject(sql, new EventRowMapper(), eventId);
		} catch (EmptyResultDataAccessException e) {
			throw new EntityNotFoundException("Event with id : " + eventId + " not found");
		}
	}

	@Override
	public List<Event> getAllLetsGoEvents(Long userId) throws NullPointerException {
		if (userId == null) {
			throw new NullPointerException("User id cannot be null");
		}
		String sql = "SELECT e.id, e.date_of_tour, e.duration, e.price, e.tour_id FROM event e "
				+ "JOIN tour t ON e.tour_id = t.id " + "JOIN user_has_event uhe ON e.id = uhe.event_id "
				+ "WHERE uhe.user_id = " + userId + " ORDER BY e.tour_id";
		return jdbcTemplate.query(sql, new EventRowMapper());
	}

	@Override
	public List<Event> getAllEventsFromPast(Long userId) throws NullPointerException {
		if (userId == null) {
			throw new NullPointerException("User id cannot be null");
		}
		String sql = "SELECT e.id, e.date_of_tour, e.duration, e.price, e.tour_id FROM event e "
				+ "JOIN tour t ON e.tour_id = t.id " + "JOIN user_has_event uhe ON e.id = uhe.event_id "
				+ "WHERE uhe.user_id = " + userId + " AND e.date_of_tour < CURRENT_DATE() " + "ORDER BY e.tour_id";
		return jdbcTemplate.query(sql, new EventRowMapper());

	}

	@Override
	public List<Event> getAllEventsFromFuture(Long userId) throws NullPointerException {
		if (userId == null) {
			throw new NullPointerException("User id cannot be null");
		}
		String sql = "SELECT e.id, e.date_of_tour, e.duration, e.price, e.tour_id FROM event e "
				+ "JOIN tour t ON e.tour_id = t.id " + "JOIN user_has_event uhe ON e.id = uhe.event_id "
				+ "WHERE uhe.user_id = " + userId + " AND e.date_of_tour > CURRENT_DATE() " + "ORDER BY e.tour_id";
		return jdbcTemplate.query(sql, new EventRowMapper());

	}

	@Override
	public List<Event> getAllEventsWhereIAmGuideman(Long userId) throws EntityNotFoundException {
		if (userId == null) {
			throw new NullPointerException("User id cannot be null");
		}
		String sql = "SELECT e.id, e.date_of_tour, e.duration, e.price, e.tour_id FROM tour t "
				+ "JOIN event e ON e.tour_id = t.id " + "WHERE t.user_id = " + userId + " ORDER BY e.tour_id";
		return jdbcTemplate.query(sql, new EventRowMapper());

	}

	@Override
	public Event save(Event event) throws NullPointerException, NegativeNumberException, NoSuchElementException {
		if (event == null) {
			throw new NullPointerException("Cannot save null");
		}
		if (event.getDateOfTour() == null) {
			throw new NullPointerException("Cannot save event without date and time of the tour");
		}
		if (event.getDuration() == null) {
			throw new NullPointerException("Cannot save event without duration");
		}
		if (event.getPrice() < 0) {
			throw new NegativeNumberException("Cannot save negative price");
		}
		if (event.getTourId() == null) {
			throw new NullPointerException("Cannot save event without a tour");
		}

		if (event.getId() == null) { // INSERT
			SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
			simpleJdbcInsert.withTableName("event");
			simpleJdbcInsert.usingColumns("date_of_tour", "duration", "price", "tour_id");
			simpleJdbcInsert.usingGeneratedKeyColumns("id");

			Map<String, Object> values = new HashMap<>();
			values.put("date_of_tour", event.getDateOfTour());
			values.put("duration", event.getDuration());
			values.put("price", event.getPrice());
			values.put("tour_id", event.getTourId());

			long id = simpleJdbcInsert.executeAndReturnKey(values).longValue();
			Event event2 = new Event(id, event.getDateOfTour(), event.getDuration(), event.getPrice(),
					event.getTourId());

			saveTourists(event2);
			return event2;

		} else { // UPDATE
			String sql = "UPDATE event SET date_of_tour=?, duration=?, price=?, tour_id=? " + "WHERE id=?";
			int changed = jdbcTemplate.update(sql, event.getDateOfTour(), event.getDuration(), event.getPrice(),
					event.getTourId(), event.getId());
			if (changed == 1) {
				String sqlDelete = "DELETE FROM user_has_event " + "WHERE event_id= " + event.getId();
				jdbcTemplate.update(sqlDelete);
				saveTourists(event);
				return event;
			} else {
				throw new NoSuchElementException("No event with id " + event.getId() + " in DB");
			}

		}
	}

	private void saveTourists(Event event) {
		if (event.getTourists() == null) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO user_has_event (user_id, event_id) VALUES ");
		for (User u : event.getTourists()) {
			if (u == null || u.getId() == null) {
				throw new NullPointerException("Event has null users or user with null id " + event.getTourists());
			}
			sb.append("(").append(u.getId());
			sb.append(", ").append(event.getId());
			sb.append("),");
		}
		String sql = sb.substring(0, sb.length() - 1);
		jdbcTemplate.update(sql);
	}

	// otestovat
	@Override
	public boolean deleteFromUHE(Long eventId) throws EntityNotFoundException {
		if (eventId == null) {
			throw new NullPointerException("Event id cannot be null");
		}
		String sqlUhe = "DELETE FROM user_has_event uhe WHERE uhe.event_id = " + eventId;
		int changedUhe;
		try {
			changedUhe = jdbcTemplate.update(sqlUhe);
		} catch (DataIntegrityViolationException e) {
			throw new EntityNotFoundException("Event with id: " + eventId + " not found in DB");
		}
		return (changedUhe == 1);

	}

	@Override
	public boolean delete(Long eventId) throws EntityNotFoundException {
		String sqlE = "DELETE FROM event WHERE event.id = " + eventId;
		int changedE;
		try {
			changedE = jdbcTemplate.update(sqlE);
		} catch (DataIntegrityViolationException e) {
			throw new EntityNotFoundException("Event with id: " + eventId + " not found in DB");
		}
		return (changedE == 1);
	}

	private class EventRowMapper implements RowMapper<Event> {

		@Override
		public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
			Event event = new Event();
			event.setId(rs.getLong("id"));
			event.setDateOfTour(rs.getTimestamp("date_of_tour").toLocalDateTime());
			event.setDuration(rs.getTimestamp("duration").toLocalDateTime().toLocalTime());
			event.setPrice(rs.getDouble("price"));
			event.setTourId(rs.getLong("tour_id"));
			return event;
		}
	}

}
