package kz.nurdaulet.validation;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dto.RestaurantCreateDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RestaurantValidator implements Validator {
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
            errors.rejectValue("name", "duplicated", "restaurant already exists");
        }
    }
}
