package kz.nurdaulet.service;

import kz.nurdaulet.entity.Food;

import java.util.List;

public interface FoodService {
    List<Food> getAllFood();
    List<Food> getFoodByCategoryId(Long categoryId);
    List<Food> getFoodBySimilarName(String name);
    Food getFoodById(Long id);
}
