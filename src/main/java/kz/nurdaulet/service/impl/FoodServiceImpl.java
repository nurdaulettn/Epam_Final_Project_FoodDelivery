package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.FoodDao;
import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.service.FoodService;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    private final FoodDao foodDao;

    public FoodServiceImpl(FoodDao foodDao) {
        this.foodDao = foodDao;
    }

    @Override
    public List<Food> getFoods(String search, Long categoryId, Long restaurantId) {
        List<Food> foods = foodDao.getAllFoods();

        if (search != null && !search.isBlank()) {
            foods = foodDao.getFoodsBySimilarName(search);
        }

        if (categoryId != null) {
            selectByCategory(categoryId, foods);
        }

        if (restaurantId != null) {
            selectByRestaurant(restaurantId, foods);
        }

        return foods;
    }

    @Override
    public List<Food> getAllFood() {
        return foodDao.getAllFoods();
    }

    @Override
    public List<Food> getFoodByCategoryId(Long categoryId) {
        return foodDao.getFoodsByCategory(categoryId);
    }

    @Override
    public List<Food> getFoodBySimilarName(String name) {
        return foodDao.getFoodsBySimilarName(name);
    }

    @Override
    public Food getFoodById(Long id) {
        return foodDao.getFoodById(id);
    }

    @Override
    public void save(FoodCreateDto foodCreateDto, Long restaurantId) {
        Food food = new Food();

        food.setName(foodCreateDto.getName().trim());
        food.setDescription(foodCreateDto.getDescription());
        food.setPrice(foodCreateDto.getPrice());
        food.setCategoryId(foodCreateDto.getCategoryId());
        food.setAvailable(true);
        food.setRestaurantId(restaurantId);

        foodDao.save(food);
    }


    private static void selectByRestaurant(Long restaurantId, List<Food> foods) {
        Iterator<Food> iterator = foods.iterator();
        while (iterator.hasNext()) {
            Food food = iterator.next();

            if (!food.getRestaurantId().equals(restaurantId)) {
                iterator.remove();
            }
        }
    }

    private static void selectByCategory(Long categoryId, List<Food> foods) {
        Iterator<Food> iterator = foods.iterator();
        while (iterator.hasNext()) {
            Food food = iterator.next();

            if (!food.getCategoryId().equals(categoryId)) {
                iterator.remove();
            }
        }
    }
}
