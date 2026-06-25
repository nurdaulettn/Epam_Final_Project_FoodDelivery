package kz.nurdaulet.dao;

import kz.nurdaulet.entity.Food;

import java.util.List;

public interface FoodDao {

    List<Food> getAllFoods();

    List<Food> getFoodsByCategory(Long categoryId);

    List<Food> getFoodsByRestaurant(Long restaurantId);

    List<Food> getFoodsByRestaurantForManager(Long restaurantId);

    List<Food> getFoodsBySimilarName(String name);

    Food getFoodById(Long id);

    Food getFoodByName(String name);

    void update(Food food);

    void disableById(Long id);

    void enableById(Long id);

    void save(Food food);

    void deleteById(Long id);
}
