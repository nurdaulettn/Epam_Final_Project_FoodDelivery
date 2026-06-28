package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import kz.nurdaulet.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USERNAME = "john";
    private static final String EMAIL = "john@example.com";
    private static final String RAW_PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encoded";
    private static final String MANAGER_ROLE = "MANAGER";
    private static final String INVALID_ROLE = "invalid";

    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl testingInstance;

    @Test
    void shouldCreateUser() {
        // given
        UserCreateDto dto = new UserCreateDto(
                FIRST_NAME,
                LAST_NAME,
                USERNAME,
                EMAIL,
                RAW_PASSWORD,
                MANAGER_ROLE
        );
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        // when
        User result = testingInstance.create(dto);

        // then
        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(userDao).save(captor.capture());
        User saved = captor.getValue();
        assertEquals(FIRST_NAME, saved.getFirstName());
        assertEquals(LAST_NAME, saved.getLastName());
        assertEquals(USERNAME, saved.getUsername());
        assertEquals(EMAIL, saved.getEmail());
        assertEquals(ENCODED_PASSWORD, saved.getPassword());
        assertEquals(Role.MANAGER, saved.getRole());
        assertEquals(true, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        assertEquals(saved, result);
    }

    @Test
    void shouldUseCustomerRoleWhenRoleIsInvalid() {
        // given
        UserCreateDto dto = new UserCreateDto(
                FIRST_NAME,
                LAST_NAME,
                USERNAME,
                EMAIL,
                RAW_PASSWORD,
                INVALID_ROLE
        );
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        // when
        testingInstance.create(dto);

        // then
        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(userDao).save(captor.capture());
        assertEquals(Role.CUSTOMER, captor.getValue().getRole());
    }

    @Test
    void shouldGetUserById() {
        // given
        User user = createUser();
        when(userDao.findById(USER_ID)).thenReturn(user);

        // when
        User result = testingInstance.getById(USER_ID);

        // then
        assertEquals(user, result);
        verify(userDao).findById(USER_ID);
    }

    @Test
    void shouldThrowWhenUserByIdNotFound() {
        // given
        when(userDao.findById(USER_ID)).thenReturn(null);

        // when / then
        assertThrows(UserNotFoundException.class, () -> testingInstance.getById(USER_ID));
        verify(userDao).findById(USER_ID);
    }

    @Test
    void shouldDeleteExistingUser() {
        // given
        when(userDao.existsById(USER_ID)).thenReturn(true);

        // when
        testingInstance.delete(USER_ID);

        // then
        verify(userDao).existsById(USER_ID);
        verify(userDao).deleteById(USER_ID);
    }

    @Test
    void shouldThrowWhenDeletingMissingUser() {
        // given
        when(userDao.existsById(USER_ID)).thenReturn(false);

        // when / then
        assertThrows(UserNotFoundException.class, () -> testingInstance.delete(USER_ID));
        verify(userDao).existsById(USER_ID);
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
