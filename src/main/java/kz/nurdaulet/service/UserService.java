package kz.nurdaulet.service;

import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.User;

public interface UserService {
    User create(UserCreateDto user);
    void delete(Long id);

}
