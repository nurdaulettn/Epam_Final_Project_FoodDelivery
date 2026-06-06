package kz.nurdaulet.dao;

import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserDao {
    private static final String FIND_ALL = "SELECT * FROM users";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_USERNAME = "SELECT * FROM users WHERE username = ?";
    private static final String SAVE_USER = "INSERT INTO users (first_name, last_name, username, email, password, role, status, created_at) VALUES (?, ?, ?, ?, ?, CAST(? AS user_role), ?, ?)";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

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


    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        return jdbcTemplate.query(FIND_ALL, userRowMapper);
    }

    public User findByEmail(String email) {
        return jdbcTemplate.query(FIND_BY_EMAIL, userRowMapper, email)
                .stream().findFirst().orElse(null);
    }

    public User findById(Long id) {
        return jdbcTemplate.query(FIND_BY_ID, userRowMapper, id)
                .stream().findFirst().orElse(null);
    }

    public User findByUsername(String username) {
        return jdbcTemplate.query(FIND_BY_USERNAME, userRowMapper, username)
                .stream().findFirst().orElse(null);
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }

    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    public void save(User user) {
        jdbcTemplate.update(SAVE_USER,
                user.getFirstName(), user.getLastName(),
                user.getUsername(), user.getEmail(),
                user.getPassword(), user.getRole().name(),
                user.getStatus(), user.getCreatedAt());
    }

    public void delete(Long id) {
        jdbcTemplate.update(DELETE_USER, id);
    }


}
