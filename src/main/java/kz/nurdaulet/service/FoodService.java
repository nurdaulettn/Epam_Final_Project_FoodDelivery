package kz.nurdaulet.service;

import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;

import java.util.List;

public interface FoodService {
    /**
     * Returns foods filtered by search text, category, and restaurant when filters are provided.
     */
    List<Food> getFoods(String search, Long categoryId, Long restaurantId);

    /**
     * Returns all active foods visible to customers.
     */
    List<Food> getAllFood();

    /**
     * Returns foods that belong to the given category.
     */
    List<Food> getFoodByCategoryId(Long categoryId);

    /**
     * Returns active foods that belong to the given restaurant.
     */
    List<Food> getFoodByRestaurantId(Long restaurantId);

    /**
     * Returns all foods for manager views, including unavailable foods.
     */
    List<Food> getFoodByRestaurantIdForManager(Long restaurantId);

    /**
     * Finds foods whose names match the given search text.
     */
    List<Food> getFoodBySimilarName(String name);

    /**
     * Returns a food item by id or throws an exception when it does not exist.
     */
    Food getFoodById(Long id);

    /**
     * Creates a new food item for the given restaurant.
     */
    void save(FoodCreateDto foodCreateDto, Long restaurantId);

    /**
     * Updates an existing food item in the given restaurant.
     */
    void update(FoodCreateDto foodCreateDto, Long restaurantId, Long foodId);

    /**
     * Marks a food item as unavailable.
     */
    void disableFood(Long foodId);

    /**
     * Marks a food item as available.
     */
    void enableFood(Long foodId);

    /**
     * Deletes an unavailable food item.
     */
    void delete(Long id);

    /**
     * Builds a food creation DTO from an existing food item for edit forms.
     */
    FoodCreateDto getFoodCreateDtoById(Long id);
}
