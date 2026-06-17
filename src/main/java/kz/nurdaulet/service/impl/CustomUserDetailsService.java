package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.User;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final String ROLE_FORMAT = "ROLE_%s";
    private static final String LOG_LOADING_USER = "Loading user with login={}";
    private static final String LOG_USER_NOT_FOUND = "User not found with login={}";
    private static final String USER_NOT_FOUND_TEMPLATE = "User with login %s not found";
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserDao userDao;

    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userDao.findByUsername(login);

        if (user != null) {
            log.info(LOG_LOADING_USER, login);

            return new CustomUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority(ROLE_FORMAT.formatted(user.getRole().name())))
            );
        } else {
            log.warn(LOG_USER_NOT_FOUND, login);

            throw new UsernameNotFoundException(USER_NOT_FOUND_TEMPLATE.formatted(login));
        }
    }
}
