package kz.nurdaulet.service;

import kz.nurdaulet.dto.CategoryCreateDto;
import kz.nurdaulet.entity.Category;

import java.util.List;

public interface CategoryService {
    /**
     * Returns all available food categories.
     */
    List<Category> getAllCategories();

    /**
     * Finds categories whose names match the given search text.
     */
    List<Category> searchCategoryByName(String name);

    /**
     * Returns a category by id or throws an exception when it does not exist.
     */
    Category getCategoryById(Long id);

    /**
     * Creates a new food category.
     */
    void createCategory(CategoryCreateDto categoryDto);
}
