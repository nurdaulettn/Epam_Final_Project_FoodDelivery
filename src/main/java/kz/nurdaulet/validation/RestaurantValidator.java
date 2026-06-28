package kz.nurdaulet.validation;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dto.RestaurantCreateDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RestaurantValidator implements Validator {
    private static final String FIELD = "name";
    private static final String ERROR_CODE = "duplicated";
    private static final String DEFAULT_MESSAGE = "restaurant already exists";

    private final RestaurantDao restaurantDao;

    public RestaurantValidator(RestaurantDao restaurantDao) {
        this.restaurantDao = restaurantDao;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RestaurantCreateDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RestaurantCreateDto dto = (RestaurantCreateDto) target;

        if (restaurantDao.findByName(dto.getName().trim()) != null) {
            errors.rejectValue(FIELD, ERROR_CODE, DEFAULT_MESSAGE);
        }
    }
}
