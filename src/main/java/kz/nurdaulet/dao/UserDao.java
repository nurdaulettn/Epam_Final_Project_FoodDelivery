package kz.nurdaulet.dao;

import kz.nurdaulet.entity.User;

import java.util.List;

public interface UserDao {
    List<User> findAll();

    User findById(Long id);

    User findByEmail(String email);

    User findByUsername(String username);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void save(User user);

    void deleteById(Long id);

    void updateStatus(Long id, boolean status);

    void updateRole(Long id, kz.nurdaulet.entity.enums.Role role);
}
