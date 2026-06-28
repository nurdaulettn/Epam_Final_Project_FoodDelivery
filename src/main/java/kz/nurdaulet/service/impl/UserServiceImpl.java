package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import kz.nurdaulet.exception.UserCreatingException;
import kz.nurdaulet.exception.UserNotFoundException;
import kz.nurdaulet.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String USER_NOT_FOUND = "User not found";
    public static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final String LOG_USER_CREATED = "User {} created";
    public static final String LOG_USER_DELETE = "User {} deleted";
    private final UserDao userDao;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.encoder = passwordEncoder;
    }

    public User create(UserCreateDto dto) {
//        boolean emailExists = userDao.existsByEmail(dto.getEmail());
//        boolean usernameExists = userDao.existsByUsername(dto.getUsername());
//
//        if (!emailExists && !usernameExists) {

        User user = createUser(dto);

        userDao.save(user);
        log.info(LOG_USER_CREATED, dto.getUsername());

        return user;

//        } else if (emailExists) {
//            log.warn(EMAIL_ALREADY_EXISTS);
//
//            throw new UserCreatingException(EMAIL_ALREADY_EXISTS);
//        } else {
//            log.warn(USERNAME_ALREADY_EXISTS);
//
//            throw new UserCreatingException(USERNAME_ALREADY_EXISTS);
//        }
    }

    @Override
    public User getById(Long id) {
        User user = userDao.findById(id);

        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND);
        }

        return user;
    }

    @Override
    public void delete(Long id) {
        if (userDao.existsById(id)) {
            userDao.deleteById(id);

            log.info(LOG_USER_DELETE, id);
        } else {
            log.warn(USER_NOT_FOUND);

            throw new UserNotFoundException(USER_NOT_FOUND);
        }
    }

    private User createUser(UserCreateDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(getRole(dto.getRole()));
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    private Role getRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.CUSTOMER;
        }
    }
}
