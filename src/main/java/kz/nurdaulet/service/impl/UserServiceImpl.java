package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import kz.nurdaulet.exception.UserCreatingException;
import kz.nurdaulet.exception.UserNotFoundException;
import kz.nurdaulet.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String USER_NOT_FOUND = "User not found";
    private final UserDao userDao;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.encoder = passwordEncoder;
    }

    public User create(UserCreateDto dto) {
        boolean emailExists = userDao.existsByEmail(dto.getEmail());
        boolean usernameExists = userDao.existsByUsername(dto.getUsername());

        if (!emailExists && !usernameExists) {
            User user = createUser(dto);

            userDao.save(user);

            return user;
        } else if (emailExists) {
            throw new UserCreatingException(EMAIL_ALREADY_EXISTS);
        } else {
            throw new UserCreatingException(USERNAME_ALREADY_EXISTS);
        }
    }

    public void delete(Long id) {
        if (userDao.existsById(id)) {
            userDao.deleteById(id);
        } else {
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
