package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.CategoryDao;
import kz.nurdaulet.dto.CategoryCreateDto;
import kz.nurdaulet.entity.Category;
import kz.nurdaulet.exception.CategoryNotFoundException;
import kz.nurdaulet.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private static final String NOT_FOUND_MESSAGE = "Category with id %d does not exist";
    private static final String LOG_CATEGORY_NOT_FOUND = "Category {} was not found";
    private static final String LOG_CATEGORY_CREATED = "Category created: name={}";

    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public List<Category> searchCategoryByName(String name) {
        return categoryDao.findBySimilarName(name);
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryDao.findById(id);

        if (category != null) {
            return category;
        }

        log.warn(LOG_CATEGORY_NOT_FOUND, id);
        throw new CategoryNotFoundException(String.format(NOT_FOUND_MESSAGE, id));
    }

    @Override
    public void createCategory(CategoryCreateDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName().trim());

        categoryDao.save(category);
        log.info(LOG_CATEGORY_CREATED, category.getName());
    }
}
