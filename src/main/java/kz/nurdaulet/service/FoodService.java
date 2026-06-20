package kz.nurdaulet.service;

import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;

import java.util.List;

public interface FoodService {
    List<Food> getFoods(String search, Long categoryId, Long restaurantId);
    List<Food> getAllFood();
    List<Food> getFoodByCategoryId(Long categoryId);
    List<Food> getFoodBySimilarName(String name);
    Food getFoodById(Long id);
    void save(FoodCreateDto foodCreateDto, Long restaurantId);
}
