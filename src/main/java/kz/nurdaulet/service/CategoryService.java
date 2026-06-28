package kz.nurdaulet.service;

import kz.nurdaulet.dto.CategoryCreateDto;
import kz.nurdaulet.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();

    List<Category> searchCategoryByName(String name);

    Category getCategoryById(Long id);

    void createCategory(CategoryCreateDto categoryDto);
}
