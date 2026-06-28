package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
    private static final Long USER_ID = 1L;
    private static final String SELECT_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String SELECT_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String SELECT_USER_BY_USERNAME_QUERY = "SELECT * FROM users WHERE username = ?";
    private static final String SAVE_USER_QUERY = "INSERT INTO users (first_name, last_name, username, email, password, role, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String ID_COLUMN = "id";
    private static final String FIRST_NAME_COLUMN = "first_name";
    private static final String LAST_NAME_COLUMN = "last_name";
    private static final String USERNAME_COLUMN = "username";
    private static final String EMAIL_COLUMN = "email";
    private static final String PASSWORD_COLUMN = "password";
    private static final String ROLE_COLUMN = "role";
    private static final String STATUS_COLUMN = "status";
    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USERNAME = "john";
    private static final String EMAIL = "john@example.com";
    private static final String ENCODED_PASSWORD = "encoded";
    private static final String CUSTOMER_ROLE = "CUSTOMER";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private UserDaoImpl testingInstance;

    @Test
    void shouldFindAllUsersAndMapRows() throws Exception {
        // given
        ArgumentCaptor<RowMapper<User>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(eq(SELECT_ALL_USERS_QUERY), mapperCaptor.capture()))
                .thenReturn(List.of(createUser()));

        // when
        List<User> result = testingInstance.findAll();

        // then
        assertEquals(1, result.size());
        verify(jdbcTemplate).query(eq(SELECT_ALL_USERS_QUERY), any(RowMapper.class));

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getLong(ID_COLUMN)).thenReturn(USER_ID);
        when(resultSet.getString(FIRST_NAME_COLUMN)).thenReturn(FIRST_NAME);
        when(resultSet.getString(LAST_NAME_COLUMN)).thenReturn(LAST_NAME);
        when(resultSet.getString(USERNAME_COLUMN)).thenReturn(USERNAME);
        when(resultSet.getString(EMAIL_COLUMN)).thenReturn(EMAIL);
        when(resultSet.getString(PASSWORD_COLUMN)).thenReturn(ENCODED_PASSWORD);
        when(resultSet.getString(ROLE_COLUMN)).thenReturn(CUSTOMER_ROLE);
        when(resultSet.getBoolean(STATUS_COLUMN)).thenReturn(true);
        when(resultSet.getObject(CREATED_AT_COLUMN, LocalDateTime.class)).thenReturn(now);

        User mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(USER_ID, mapped.getId());
        assertEquals(FIRST_NAME, mapped.getFirstName());
        assertEquals(Role.CUSTOMER, mapped.getRole());
    }

    @Test
    void shouldFindUserMethodsAndExists() {
        // given
        User user = createUser();

        when(jdbcTemplate.query(eq(SELECT_USER_BY_ID_QUERY), any(RowMapper.class), eq(USER_ID)))
                .thenReturn(List.of(user), List.of(user), List.of());
        when(jdbcTemplate.query(eq(SELECT_USER_BY_EMAIL_QUERY), any(RowMapper.class), eq(EMAIL)))
                .thenReturn(List.of(user), List.of(user));
        when(jdbcTemplate.query(eq(SELECT_USER_BY_USERNAME_QUERY), any(RowMapper.class), eq(USERNAME)))
                .thenReturn(List.of(user), List.of(user));

        // when
        User foundById = testingInstance.findById(USER_ID);
        boolean existsById = testingInstance.existsById(USER_ID);
        boolean missingById = testingInstance.existsById(USER_ID);
        User foundByEmail = testingInstance.findByEmail(EMAIL);
        boolean existsByEmail = testingInstance.existsByEmail(EMAIL);
        User foundByUsername = testingInstance.findByUsername(USERNAME);
        boolean existsByUsername = testingInstance.existsByUsername(USERNAME);

        // then
        assertEquals(user, foundById);
        assertTrue(existsById);
        assertFalse(missingById);
        assertEquals(user, foundByEmail);
        assertTrue(existsByEmail);
        assertEquals(user, foundByUsername);
        assertTrue(existsByUsername);
        verify(jdbcTemplate, org.mockito.Mockito.times(3))
                .query(eq(SELECT_USER_BY_ID_QUERY), any(RowMapper.class), eq(USER_ID));
        verify(jdbcTemplate, org.mockito.Mockito.times(2))
                .query(eq(SELECT_USER_BY_EMAIL_QUERY), any(RowMapper.class), eq(EMAIL));
        verify(jdbcTemplate, org.mockito.Mockito.times(2))
                .query(eq(SELECT_USER_BY_USERNAME_QUERY), any(RowMapper.class), eq(USERNAME));
    }

    @Test
    void shouldSaveAndDeleteUser() {
        // given
        User user = createUser();

        // when
        testingInstance.save(user);
        testingInstance.deleteById(USER_ID);

        // then
        verify(jdbcTemplate).update(
                SAVE_USER_QUERY,
                FIRST_NAME, LAST_NAME, USERNAME, EMAIL, ENCODED_PASSWORD, CUSTOMER_ROLE, true, user.getCreatedAt());
        verify(jdbcTemplate).update(DELETE_USER_QUERY, USER_ID);
    }

    private User createUser() {
        return new User(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                USERNAME,
                EMAIL,
                ENCODED_PASSWORD,
                Role.CUSTOMER,
                true,
                LocalDateTime.now()
        );
    }
}
