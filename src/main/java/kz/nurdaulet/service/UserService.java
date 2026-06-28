package kz.nurdaulet.service;

import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.User;

public interface UserService {
    User create(UserCreateDto user);
    User getById(Long id);
    void delete(Long id);

}
