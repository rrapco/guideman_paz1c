package sk.upjs.paz1c.guideman.storage;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class MysqlUserDao implements UserDao {

	private JdbcTemplate jdbcTemplate;

	public MysqlUserDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<User> getAll() {
		String sql = "SELECT id, name, surname, email, tel_number, birthdate, login, password, image FROM user";
		return jdbcTemplate.query(sql, new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				long id = rs.getLong("id");
				String name = rs.getString("name");
				String surname = rs.getString("surname");
				String email = rs.getString("email");
				String telNumber = rs.getString("tel_number");
				LocalDate birthdate = rs.getTimestamp("birthdate").toLocalDateTime().toLocalDate();
				String login = rs.getString("login");
				String password = rs.getString("password");
				// https://stackoverflow.com/questions/14610011/reading-a-blob-from-mysql-with-java
				Blob image = rs.getBlob("image");

				return new User(id, name, surname, email, telNumber, birthdate, login, password, image);
			}
		});
	}

	@Override
	public User getById(long id) throws EntityNotFoundException {
		String sql = "SELECT id, name, surname, email, tel_number, birthdate, login, password, image FROM user WHERE id = "
				+ id;
		try {
			return jdbcTemplate.queryForObject(sql, new UserRowMapper());
		} catch (EmptyResultDataAccessException e) {
			throw new EntityNotFoundException("User with id " + id + " not found");
		}
	}

	@Override
	public List<User> getAllTourists(long eventId) {
		String sql = "SELECT id, name, surname, email, tel_number, birthdate, login, password, image FROM user "
				+ "LEFT JOIN user_has_event uhe ON user.id = uhe.user_id " + "WHERE uhe.event_id = " + eventId;
		return jdbcTemplate.query(sql, new UserRowMapper());
	}

	@Override
	public List<User> getAllGuidemans() {
		String sql = "SELECT DISTINCT u.id, u.name, u.surname, u.email, u.tel_number, u.birthdate, u.login, u.password, u.image FROM user u "
				+ "JOIN tour t ON t.user_id = u.id";
		return jdbcTemplate.query(sql, new UserRowMapper());
	}

	@Override
	public void saveUserEvent(Long userId, Long eventId) {
		if (userId == null || eventId == null) {
			throw new NullPointerException("User id and event id cannot be null");
		}
		String sql = "INSERT INTO user_has_event VALUES(?,?," + null + "," + null + ")";
		jdbcTemplate.update(sql, userId, eventId);

	}

	@Override
	public User getUserByUsername(String username) throws EntityNotFoundException {
		String sql = "SELECT id, name, surname, email, tel_number, birthdate, login, password, image FROM user "
				+ "WHERE login =? ";
		try {
			return jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
		} catch (EmptyResultDataAccessException e) {
			throw new EntityNotFoundException("User with username " + username + " not found");
		}
	}

	@Override
	public User save(User user) throws NullPointerException, EntityNotFoundException {
		if (user == null) {
			throw new NullPointerException("Cannot save null");
		}
		if (user.getName() == null) {
			throw new NullPointerException("Name cannot be null");
		}
		if (user.getSurname() == null) {
			throw new NullPointerException("Surname cannot be null");
		}
		if (user.getEmail() == null) {
			throw new NullPointerException("Email address cannot be null");
		}
		if (user.getBirthdate() == null) {
			throw new NullPointerException("Birthdate cannot be null");
		}
		if (user.getLogin() == null) {
			throw new NullPointerException("Login cannot be null");
		}
		if (user.getPassword() == null) {
			throw new NullPointerException("Password cannot be null");
		}

		if (user.getId() == null) { // INSERT
			String salt = BCrypt.gensalt();
			user.setPassword(BCrypt.hashpw(user.getPassword(), salt));

			SimpleJdbcInsert sjdbInsert = new SimpleJdbcInsert(jdbcTemplate);
			sjdbInsert.withTableName("user");
			sjdbInsert.usingGeneratedKeyColumns("id");

			sjdbInsert.usingColumns("name", "surname", "email", "tel_number", "birthdate", "login", "password",
					"image");

			Map<String, Object> values = new HashMap<>();
			values.put("name", user.getName());
			values.put("surname", user.getSurname());
			values.put("email", user.getEmail());
			values.put("name", user.getName());
			values.put("tel_number", user.getTelNumber());
			values.put("birthdate", user.getBirthdate());
			values.put("login", user.getLogin());
			values.put("password", user.getPassword());
			values.put("image", user.getImage());

			long id = sjdbInsert.executeAndReturnKey(values).longValue();
			return new User(id, user.getName(), user.getSurname(), user.getEmail(), user.getTelNumber(),
					user.getBirthdate(), user.getLogin(), user.getPassword(), user.getImage());

		} else { // UPDATE
			String sql = "UPDATE user SET name=?, surname=?, email=?, tel_number=?, birthdate=?, login=?, password=?, image=? WHERE id = "
					+ user.getId();
			int changed = jdbcTemplate.update(sql, user.getName(), user.getSurname(), user.getEmail(),
					user.getTelNumber(), user.getBirthdate(), user.getLogin(), user.getPassword(), user.getImage());
			if (changed == 1) {
				return user;
			}
			throw new EntityNotFoundException("User with id " + user.getId() + " not found");
		}
	}

	@Override
	public boolean delete(long id) throws EntityNotFoundException {
		int changed = jdbcTemplate.update("DELETE FROM user WHERE id = " + id);
		return changed == 1;
	}

	private class UserRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("id"));
			user.setName(rs.getString("name"));
			user.setSurname(rs.getString("surname"));
			user.setEmail(rs.getString("email"));
			user.setTelNumber(rs.getString("tel_number"));
			user.setBirthdate(rs.getTimestamp("birthdate").toLocalDateTime().toLocalDate());
			user.setLogin(rs.getString("login"));
			user.setPassword(rs.getString("password"));
			user.setImage(rs.getBlob("image"));

			return user;
		}
	}

	@Override
	public void saveRating(Long userId, Long eventId, Integer rating) throws NullPointerException {
		if (userId == null || eventId == null) {
			throw new NullPointerException("User id and event id cannot be null");
		}
		if (rating == 0 || rating == 1 || rating == 2 || rating == 3 || rating == 4 || rating == 5) {
			String sql = "UPDATE user_has_event uhe " + "SET rating = " + rating
					+ " WHERE rating IS NULL AND uhe.user_id = " + userId + " AND uhe.event_id = " + eventId;
			jdbcTemplate.update(sql);

		}
	}

	@Override
	public void saveReview(Long userId, Long eventId, String review) throws NullPointerException {
		if (userId == null || eventId == null) {
			throw new NullPointerException("User id and event id cannot be null");
		}
		if (review != "") {
			String sql = "UPDATE user_has_event uhe " + "SET review =? " + " WHERE review IS NULL AND uhe.user_id = "
					+ userId + " AND uhe.event_id = " + eventId;
			jdbcTemplate.update(sql, review);
		}
	}

	@Override
	public void deleteRating(Long userId, Long eventId) throws NullPointerException {
		if (userId == null || eventId == null) {
			throw new NullPointerException("User id and event id cannot be null");
		}
		String sql = "UPDATE user_has_event uhe " + "SET rating = null" + " WHERE rating IS NOT NULL AND uhe.user_id = "
				+ userId + " AND uhe.event_id = " + eventId;
		jdbcTemplate.update(sql);
	}

	@Override
	public void deleteReview(Long userId, Long eventId) throws NullPointerException {
		if (userId == null || eventId == null) {
			throw new NullPointerException("User id and event id cannot be null");
		}
		String sql = "UPDATE user_has_event uhe " + "SET review = null "
				+ " WHERE review IS NOT NULL AND uhe.user_id = " + userId + " AND uhe.event_id = " + eventId;
		jdbcTemplate.update(sql);
	}

}
