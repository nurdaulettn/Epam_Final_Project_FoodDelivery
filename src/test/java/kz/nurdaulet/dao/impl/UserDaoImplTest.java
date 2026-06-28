package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Test
    void shouldFindAllUsersAndMapRows() throws Exception {
        UserDaoImpl dao = new UserDaoImpl(jdbcTemplate);
        ArgumentCaptor<RowMapper<User>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(eq("SELECT * FROM users"), mapperCaptor.capture()))
                .thenReturn(List.of(createUser()));

        List<User> result = dao.findAll();

        assertEquals(1, result.size());

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("first_name")).thenReturn("John");
        when(resultSet.getString("last_name")).thenReturn("Doe");
        when(resultSet.getString("username")).thenReturn("john");
        when(resultSet.getString("email")).thenReturn("john@example.com");
        when(resultSet.getString("password")).thenReturn("encoded");
        when(resultSet.getString("role")).thenReturn("CUSTOMER");
        when(resultSet.getBoolean("status")).thenReturn(true);
        when(resultSet.getObject("created_at", LocalDateTime.class)).thenReturn(now);

        User mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(1L, mapped.getId());
        assertEquals("John", mapped.getFirstName());
        assertEquals(Role.CUSTOMER, mapped.getRole());
    }

    @Test
    void shouldFindUserMethodsAndExists() {
        UserDaoImpl dao = new UserDaoImpl(jdbcTemplate);
        User user = createUser();

        when(jdbcTemplate.query(eq("SELECT * FROM users WHERE id = ?"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(user), List.of(user), List.of());
        when(jdbcTemplate.query(eq("SELECT * FROM users WHERE email = ?"), any(RowMapper.class), eq("john@example.com")))
                .thenReturn(List.of(user), List.of(user));
        when(jdbcTemplate.query(eq("SELECT * FROM users WHERE username = ?"), any(RowMapper.class), eq("john")))
                .thenReturn(List.of(user), List.of(user));

        assertEquals(user, dao.findById(1L));
        assertTrue(dao.existsById(1L));
        assertFalse(dao.existsById(1L));
        assertEquals(user, dao.findByEmail("john@example.com"));
        assertTrue(dao.existsByEmail("john@example.com"));
        assertEquals(user, dao.findByUsername("john"));
        assertTrue(dao.existsByUsername("john"));
    }

    @Test
    void shouldSaveAndDeleteUser() {
        UserDaoImpl dao = new UserDaoImpl(jdbcTemplate);
        User user = createUser();

        dao.save(user);
        dao.deleteById(1L);

        verify(jdbcTemplate).update(
                "INSERT INTO users (first_name, last_name, username, email, password, role, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "John", "Doe", "john", "john@example.com", "encoded", "CUSTOMER", true, user.getCreatedAt());
        verify(jdbcTemplate).update("DELETE FROM users WHERE id = ?", 1L);
    }

    private User createUser() {
        return new User(
                1L,
                "John",
                "Doe",
                "john",
                "john@example.com",
                "encoded",
                Role.CUSTOMER,
                true,
                LocalDateTime.now()
        );
    }
}
