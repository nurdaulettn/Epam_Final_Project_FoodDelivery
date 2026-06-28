package kz.nurdaulet.service;

import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;

import java.util.List;

public interface UserService {
    User create(UserCreateDto user);

    List<User> getAllUsers();

    User getById(Long id);

    void delete(Long id);

    void deleteByAdmin(Long adminId, Long targetUserId);

    void updateStatus(Long adminId, Long targetUserId, boolean status);

    void updateRole(Long adminId, Long targetUserId, Role role);
}
