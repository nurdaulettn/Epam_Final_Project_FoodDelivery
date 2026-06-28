package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.CategoryDao;
import kz.nurdaulet.dto.CategoryCreateDto;
import kz.nurdaulet.entity.Category;
import kz.nurdaulet.exception.CategoryNotFoundException;
import kz.nurdaulet.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final String NOT_FOUND_MESSAGE = "Category with id %d does not exist";

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

        throw new CategoryNotFoundException(String.format(NOT_FOUND_MESSAGE, id));
    }

    @Override
    public void createCategory(CategoryCreateDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName().trim());

        categoryDao.save(category);
    }
}
