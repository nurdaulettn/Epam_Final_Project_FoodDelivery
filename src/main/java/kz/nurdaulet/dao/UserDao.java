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
}
