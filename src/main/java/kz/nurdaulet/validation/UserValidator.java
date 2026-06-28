package kz.nurdaulet.validation;

import kz.nurdaulet.dao.UserDao;
import kz.nurdaulet.dto.UserCreateDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    private static final String FIELD_USERNAME = "username";
    private static final String ERROR_CODE_USERNAME = "error.username.exists";
    private static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    private static final String FIELD_EMAIL = "email";
    private static final String ERROR_CODE_EMAIL = "error.email.exists";
    private static final String EMAIL_ALREADY_EXISTS = "Email already exists";

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
            errors.rejectValue(FIELD_USERNAME, ERROR_CODE_USERNAME, USERNAME_ALREADY_EXISTS);
        }

        if (userDao.existsByEmail(dto.getEmail())) {
            errors.rejectValue(FIELD_EMAIL, ERROR_CODE_EMAIL, EMAIL_ALREADY_EXISTS);
        }
    }
}
