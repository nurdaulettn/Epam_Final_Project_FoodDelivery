package kz.nurdaulet.service;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import kz.nurdaulet.exception.UserCreatingException;
import kz.nurdaulet.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User create(UserCreateDto dto) {
        boolean emailExists = userDao.existsByEmail(dto.getEmail());
        boolean usernameExists = userDao.existsByUsername(dto.getUsername());

        if (!emailExists && !usernameExists) {
            User user = new User();

            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            // TODO: there should be a encrypting a password when add security
            user.setPassword(dto.getPassword());
            user.setRole(getRole(dto.getRole()));
            user.setStatus(true);
            user.setCreatedAt(LocalDateTime.now());

            userDao.save(user);

            return user;
        } else if (emailExists) {
            throw new UserCreatingException("Email already exists");
        } else {
            throw new UserCreatingException("Username already exists");
        }
    }

    public void delete(Long id) {
        if (userDao.existsById(id)) {
            userDao.delete(id);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    private Role getRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.CUSTOMER;
        }
    }
}
