package kz.nurdaulet.service;

import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;

import java.util.List;

public interface UserService {
    /**
     * Creates a new user account.
     */
    User create(UserCreateDto user);

    /**
     * Returns all registered users.
     */
    List<User> getAllUsers();

    /**
     * Returns a user by id or throws an exception when it does not exist.
     */
    User getById(Long id);

    /**
     * Deletes a user by id.
     */
    void delete(Long id);

    /**
     * Deletes a user account through the admin panel.
     */
    void deleteByAdmin(Long adminId, Long targetUserId);

    /**
     * Updates a user's active or blocked status through the admin panel.
     */
    void updateStatus(Long adminId, Long targetUserId, boolean status);

    /**
     * Updates a user's role through the admin panel.
     */
    void updateRole(Long adminId, Long targetUserId, Role role);
}
