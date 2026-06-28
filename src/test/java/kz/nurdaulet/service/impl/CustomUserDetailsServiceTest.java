package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private CustomUserDetailsService testingInstance;

    @Test
    void shouldLoadUserByUsername() {
        when(userDao.findByUsername("john")).thenReturn(createUser());

        CustomUserDetails result = (CustomUserDetails) testingInstance.loadUserByUsername("john");

        assertEquals(1L, result.getId());
        assertEquals("john", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER")));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userDao.findByUsername("missing")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> testingInstance.loadUserByUsername("missing"));
    }

    private User createUser() {
        return new User(
                1L,
                "John",
                "Doe",
                "john",
                "john@example.com",
                "encoded",
                Role.MANAGER,
                true,
                LocalDateTime.now()
        );
    }
}
