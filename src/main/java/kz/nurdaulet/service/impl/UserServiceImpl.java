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
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    public static final String USER_NOT_FOUND = "User not found";
    public static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final String LOG_USER_CREATED = "User {} created";
    public static final String LOG_USER_DELETE = "User {} deleted";
    private static final String SELF_MANAGEMENT_IS_NOT_ALLOWED = "You can not manage your own admin account";
    private static final String LOG_ADMIN_UPDATED_USER_STATUS = "Admin {} updated user {} status to {}";
    private static final String LOG_ADMIN_UPDATED_USER_ROLE = "Admin {} updated user {} role to {}";
    private static final String LOG_ADMIN_SELF_MANAGEMENT_REJECTED = "Admin {} tried to manage their own account";
    private final UserDao userDao;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.encoder = passwordEncoder;
    }

    public User create(UserCreateDto dto) {
        User user = createUser(dto);

        userDao.save(user);
        log.info(LOG_USER_CREATED, dto.getUsername());

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User getById(Long id) {
        User user = userDao.findById(id);

        if (user != null) {
            return user;
        }

        throw new UserNotFoundException(USER_NOT_FOUND);
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

    @Override
    public void deleteByAdmin(Long adminId, Long targetUserId) {
        validateTargetUser(adminId, targetUserId);
        delete(targetUserId);
    }

    @Override
    public void updateStatus(Long adminId, Long targetUserId, boolean status) {
        validateTargetUser(adminId, targetUserId);
        userDao.updateStatus(targetUserId, status);
        log.info(LOG_ADMIN_UPDATED_USER_STATUS, adminId, targetUserId, status);
    }

    @Override
    public void updateRole(Long adminId, Long targetUserId, Role role) {
        validateTargetUser(adminId, targetUserId);
        userDao.updateRole(targetUserId, role);
        log.info(LOG_ADMIN_UPDATED_USER_ROLE, adminId, targetUserId, role);
    }

    private void validateTargetUser(Long adminId, Long targetUserId) {
        if (adminId.equals(targetUserId)) {
            log.warn(LOG_ADMIN_SELF_MANAGEMENT_REJECTED, adminId);
            throw new UserCreatingException(SELF_MANAGEMENT_IS_NOT_ALLOWED);
        }

        getById(targetUserId);
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
