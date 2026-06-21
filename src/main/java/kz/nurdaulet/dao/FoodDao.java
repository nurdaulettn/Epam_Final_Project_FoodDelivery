package kz.nurdaulet.dao;

import kz.nurdaulet.entity.Food;

import java.util.List;

public interface FoodDao {

    List<Food> getAllFoods();

    List<Food> getFoodsByCategory(Long categoryId);

    List<Food> getFoodsByRestaurant(Long restaurantId);

    List<Food> getFoodsBySimilarName(String name);

    Food getFoodById(Long id);

    Food getFoodByName(String name);

    void save(Food food);
}
