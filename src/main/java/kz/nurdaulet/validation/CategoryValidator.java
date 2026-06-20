package kz.nurdaulet.validation;

import kz.nurdaulet.dao.CategoryDao;
import kz.nurdaulet.dto.CategoryCreateDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CategoryValidator implements Validator {
    private final CategoryDao categoryDao;

    public CategoryValidator(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CategoryCreateDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CategoryCreateDto dto = (CategoryCreateDto) target;

        if (dto != null
                && categoryDao.findByName(dto.getName().trim()) != null) {
            errors.rejectValue("name", "duplicated", "category already exists");
        }
    }
}
