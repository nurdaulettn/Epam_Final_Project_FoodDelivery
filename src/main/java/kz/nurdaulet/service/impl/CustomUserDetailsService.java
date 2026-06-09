package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.impl.UserDaoImpl;
import kz.nurdaulet.entity.User;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final String LOG_LOADING_USER = "Loading user with username={}";
    private static final String LOG_USER_NOT_FOUND = "User not found with username={}";
    private static final String USER_NOT_FOUND_TEMPLATE = "User with username %s not found";
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserDaoImpl userDao;

    public CustomUserDetailsService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);

        if (user != null) {
            log.info(LOG_LOADING_USER, username);
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .disabled(!user.getStatus())
                    .build();
        }else {
            log.warn(LOG_USER_NOT_FOUND, username);
            throw new UsernameNotFoundException(USER_NOT_FOUND_TEMPLATE.formatted(username));
        }
    }
}
