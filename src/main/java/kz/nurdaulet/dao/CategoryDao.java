package kz.nurdaulet.dao;

import kz.nurdaulet.dto.CategoryCreateDto;
import kz.nurdaulet.entity.Category;

import java.util.List;

public interface CategoryDao {
    List<Category> findAll();
    List<Category> findBySimilarName(String name);
    Category findById(Long id);
    Category findByName(String name);
    void save(Category category);
}
