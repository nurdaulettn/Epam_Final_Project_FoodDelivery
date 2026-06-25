package kz.nurdaulet.service;

import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;

import java.util.List;

public interface FoodService {
    List<Food> getFoods(String search, Long categoryId, Long restaurantId);

    List<Food> getAllFood();

    List<Food> getFoodByCategoryId(Long categoryId);

    List<Food> getFoodByRestaurantId(Long restaurantId);

    List<Food> getFoodByRestaurantIdForManager(Long restaurantId);

    List<Food> getFoodBySimilarName(String name);

    Food getFoodById(Long id);

    void save(FoodCreateDto foodCreateDto, Long restaurantId);

    void update(FoodCreateDto foodCreateDto, Long restaurantId, Long foodId);

    void disableFood(Long foodId);

    void enableFood(Long foodId);

    void delete(Long id);

    FoodCreateDto getFoodCreateDtoById(Long id);
}
