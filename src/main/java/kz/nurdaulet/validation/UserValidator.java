package kz.nurdaulet.validation;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.dto.UserCreateDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    private final UserDao userDao;

    public UserValidator(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserCreateDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserCreateDto dto = (UserCreateDto) target;

        if (userDao.existsByUsername(dto.getUsername())) {
            errors.rejectValue("username", "error.username.exists", "Username already exists");
        }

        if (userDao.existsByEmail(dto.getEmail())) {
            errors.rejectValue("email", "error.email.exists", "Email already exists");
        }
    }
}
