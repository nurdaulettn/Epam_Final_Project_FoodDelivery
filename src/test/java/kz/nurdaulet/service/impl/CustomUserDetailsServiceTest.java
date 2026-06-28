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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USERNAME = "john";
    private static final String MISSING_USERNAME = "missing";
    private static final String EMAIL = "john@example.com";
    private static final String ENCODED_PASSWORD = "encoded";
    private static final String MANAGER_AUTHORITY = "ROLE_MANAGER";

    @Mock
    private UserDao userDao;

    @InjectMocks
    private CustomUserDetailsService testingInstance;

    @Test
    void shouldLoadUserByUsername() {
        // given
        when(userDao.findByUsername(USERNAME)).thenReturn(createUser());

        // when
        CustomUserDetails result = (CustomUserDetails) testingInstance.loadUserByUsername(USERNAME);

        // then
        assertEquals(USER_ID, result.getId());
        assertEquals(USERNAME, result.getUsername());
        assertEquals(ENCODED_PASSWORD, result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(MANAGER_AUTHORITY)));
        verify(userDao).findByUsername(USERNAME);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // given
        when(userDao.findByUsername(MISSING_USERNAME)).thenReturn(null);

        // when / then
        assertThrows(UsernameNotFoundException.class,
                () -> testingInstance.loadUserByUsername(MISSING_USERNAME));
        verify(userDao).findByUsername(MISSING_USERNAME);
    }

    private User createUser() {
        return new User(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                USERNAME,
                EMAIL,
                ENCODED_PASSWORD,
                Role.MANAGER,
                true,
                LocalDateTime.now()
        );
    }
}
