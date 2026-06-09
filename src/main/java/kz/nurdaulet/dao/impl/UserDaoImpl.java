package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private static final String FIND_ALL = "SELECT * FROM users";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private static final String SAVE_USER = "INSERT INTO users (first_name, last_name, username, email, password, role, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        return new User(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                Role.valueOf(rs.getString("role")),
                rs.getBoolean("status"),
                rs.getObject("created_at", LocalDateTime.class)
        );
    };


    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        log.info("Fetching all users");

        return jdbcTemplate.query(FIND_ALL, userRowMapper);
    }

    @Override
    public User findById(Long id) {
        log.info("Fetching user by id: {}", id);

        return jdbcTemplate.query(FIND_BY_ID, userRowMapper, id)
                .stream().findFirst().orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        log.info("Fetching user by email {}", email);

        return jdbcTemplate.query(FIND_BY_EMAIL, userRowMapper, email)
                .stream().findFirst().orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        log.info("Fetching user by username {}", username);

        return jdbcTemplate.query(FIND_BY_USERNAME, userRowMapper, username)
                .stream().findFirst().orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    @Override
    public void save(User user) {
        jdbcTemplate.update(SAVE_USER,
                user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getEmail(),
                user.getPassword(), user.getRole().name(),
                user.getStatus(), user.getCreatedAt());

        log.info("User {} saved", user.getUsername());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE_USER, id);

        log.info("User {} deleted", id);
    }
}
