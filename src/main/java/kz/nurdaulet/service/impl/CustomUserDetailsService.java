package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.impl.UserDaoImpl;
import kz.nurdaulet.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final String USER_NOT_FOUND_TEMPLATE = "User with username %s not found";

    private final UserDaoImpl userDao;

    public CustomUserDetailsService(UserDaoImpl userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);

        if (user != null) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .disabled(!user.getStatus())
                    .build();
        }else {
            throw new UsernameNotFoundException(USER_NOT_FOUND_TEMPLATE.formatted(username));
        }
    }
}
