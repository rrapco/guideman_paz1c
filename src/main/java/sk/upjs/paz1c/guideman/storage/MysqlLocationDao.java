package sk.upjs.paz1c.guideman.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class MysqlLocationDao implements LocationDao {

	private JdbcTemplate jdbcTemplate;

	public MysqlLocationDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Location> getAllByCountry(String searchedCountry) throws NullPointerException {
		String sql = "SELECT id, country, city, street, street_number FROM location WHERE country like " + "'"
				+ searchedCountry + "'";
		if (searchedCountry == null) {
			throw new NullPointerException("Country with id " + searchedCountry + " not found");
		}

		return jdbcTemplate.query(sql, new LocationRowMapper());

	}

	@Override
	public List<Location> getAll() {
		return jdbcTemplate.query("SELECT id, country, city, street, street_number FROM location",
				new LocationRowMapper());

	}

	@Override
	public Location getById(long id) {
		String sql = "SELECT id, country, city, street, street_number FROM location " + "WHERE id = " + id;
		try {
			return jdbcTemplate.queryForObject(sql, new LocationRowMapper());
		} catch (EmptyResultDataAccessException e) {
			throw new NoSuchElementException("Location with id " + id + " not found");
		}
	}

	@Override
	public Location save(Location location) {
		if (location == null) { // INSERT
			throw new NullPointerException("Cannot save null");
		}
		if (location.getCountry() == null) {
			throw new NullPointerException("Country cannot be null");
		}
		if (location.getCity() == null) {
			throw new NullPointerException("City cannot be null");
		}
		if (location.getStreet() == null) {
			throw new NullPointerException("Street address cannot be null");
		}

		if (location.getId() == null) { // INSERT
			SimpleJdbcInsert sjdbInsert = new SimpleJdbcInsert(jdbcTemplate);
			sjdbInsert.withTableName("location");
			sjdbInsert.usingGeneratedKeyColumns("id");

			sjdbInsert.usingColumns("country", "city", "street", "street_number");

			Map<String, Object> values = new HashMap<>();
			values.put("country", location.getCountry());
			values.put("city", location.getCity());
			values.put("street", location.getStreet());
			values.put("street_number", location.getStreet_number());

			long id = sjdbInsert.executeAndReturnKey(values).longValue();
			return new Location(id, location.getCountry(), location.getCity(), location.getStreet(),
					location.getStreet_number());

		} else { // UPDATE
			String sql = "UPDATE location SET country=?, city=?, street=?, street_number=? WHERE id = "
					+ location.getId();
			int changed = jdbcTemplate.update(sql, location.getCountry(), location.getCity(), location.getStreet(),
					location.getStreet_number());
			if (changed == 1) {
				return location;
			}
			throw new EntityNotFoundException("Location with id " + location.getId() + " not found");
		}
	}

//	@Override
//	public boolean delete(Long locationId) {
//		List<Tour> allTours = DaoFactory.INSTANCE.getTourDao().getAllToursByLocation(locationId);
//		for (Tour t : allTours) {
//			// takto staci?
//			DaoFactory.INSTANCE.getTourDao().delete(t.getId());
//		}
//		int changed = jdbcTemplate.update("DELETE FROM location WHERE id = " + locationId);
//		return changed == 1;
//	}

	@Override
	public boolean delete(Long locationId) throws NullPointerException, EntityNotFoundException {
		if (locationId == null) {
			throw new NullPointerException("Location with id " + locationId + " not found");
		}
		int changed = 0;
		changed = jdbcTemplate.update("DELETE FROM location WHERE id = " + locationId);
		if (changed == 0) {
			throw new EntityNotFoundException("Location with id " + locationId + " not in DB");
		}

		return changed == 1;
	}

	private class LocationRowMapper implements RowMapper<Location> {

		@Override
		public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
			Location location = new Location();
			location.setId(rs.getLong("id"));
			location.setCountry(rs.getString("country"));
			location.setCity(rs.getString("city"));
			location.setStreet(rs.getString("street"));
			location.setStreet_number(rs.getInt("street_number"));

			return location;
		}
	}

}