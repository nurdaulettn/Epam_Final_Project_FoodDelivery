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
    @Mock
    private UserDao userDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl testingInstance;

    @Test
    void shouldCreateUser() {
        UserCreateDto dto = new UserCreateDto(
                "John",
                "Doe",
                "john",
                "john@example.com",
                "password",
                "MANAGER"
        );
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        User result = testingInstance.create(dto);

        verify(userDao).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("john", saved.getUsername());
        assertEquals("john@example.com", saved.getEmail());
        assertEquals("encoded", saved.getPassword());
        assertEquals(Role.MANAGER, saved.getRole());
        assertEquals(true, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        assertEquals(saved, result);
    }

    @Test
    void shouldUseCustomerRoleWhenRoleIsInvalid() {
        UserCreateDto dto = new UserCreateDto(
                "John",
                "Doe",
                "john",
                "john@example.com",
                "password",
                "invalid"
        );
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        testingInstance.create(dto);

        verify(userDao).save(captor.capture());
        assertEquals(Role.CUSTOMER, captor.getValue().getRole());
    }

    @Test
    void shouldGetUserById() {
        User user = createUser();
        when(userDao.findById(1L)).thenReturn(user);

        assertEquals(user, testingInstance.getById(1L));
    }

    @Test
    void shouldThrowWhenUserByIdNotFound() {
        when(userDao.findById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> testingInstance.getById(1L));
    }

    @Test
    void shouldDeleteExistingUser() {
        when(userDao.existsById(1L)).thenReturn(true);

        testingInstance.delete(1L);

        verify(userDao).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingMissingUser() {
        when(userDao.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> testingInstance.delete(1L));
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
