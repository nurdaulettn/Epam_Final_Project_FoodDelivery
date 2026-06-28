package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.FoodDao;
import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.exception.DeletingActiveFoodException;
import kz.nurdaulet.exception.FoodNotFoundException;
import kz.nurdaulet.service.FoodService;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    public static final String CAN_NOT_DELETE_FOOD = "Can't delete active food";
    public static final String FOOD_NOT_FOUND = "Food with id %d found";
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
    public List<Food> getFoodByRestaurantId(Long restaurantId) {
        return foodDao.getFoodsByRestaurant(restaurantId);
    }

    @Override
    public List<Food> getFoodByRestaurantIdForManager(Long restaurantId) {
        return foodDao.getFoodsByRestaurantForManager(restaurantId);
    }

    @Override
    public List<Food> getFoodBySimilarName(String name) {
        return foodDao.getFoodsBySimilarName(name);
    }

    @Override
    public Food getFoodById(Long id) {
        Food food = foodDao.getFoodById(id);
        if (food != null) {
            return food;
        }

        throw new FoodNotFoundException(FOOD_NOT_FOUND.formatted(id));
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

    @Override
    public void update(FoodCreateDto foodCreateDto, Long restaurantId, Long foodId) {
        checkFoodById(foodId);

        Food food = foodDao.getFoodById(foodId);

        food.setId(foodId);
        food.setName(foodCreateDto.getName().trim());
        food.setDescription(foodCreateDto.getDescription());
        food.setPrice(foodCreateDto.getPrice());
        food.setCategoryId(foodCreateDto.getCategoryId());
        food.setRestaurantId(restaurantId);

        foodDao.update(food);
    }

    @Override
    public void disableFood(Long foodId) {
        checkFoodById(foodId);

        foodDao.disableById(foodId);
    }

    @Override
    public void enableFood(Long foodId) {
        checkFoodById(foodId);

        foodDao.enableById(foodId);
    }

    @Override
    public void delete(Long id) {
        checkFoodById(id);

        Food food = foodDao.getFoodById(id);

        if (!food.getAvailable()) {
            foodDao.deleteById(id);
        } else {
            throw new DeletingActiveFoodException(CAN_NOT_DELETE_FOOD);
        }
    }

    @Override
    public FoodCreateDto getFoodCreateDtoById(Long id) {
        checkFoodById(id);

        Food food = foodDao.getFoodById(id);
        FoodCreateDto foodCreateDto = new FoodCreateDto();

        foodCreateDto.setName(food.getName());
        foodCreateDto.setDescription(food.getDescription());
        foodCreateDto.setPrice(food.getPrice());
        foodCreateDto.setCategoryId(food.getCategoryId());

        return foodCreateDto;
    }


    private static void selectByRestaurant(Long restaurantId, List<Food> foods) {
        foods.removeIf(food -> !food.getRestaurantId().equals(restaurantId));
    }

    private static void selectByCategory(Long categoryId, List<Food> foods) {
        foods.removeIf(food -> !food.getCategoryId().equals(categoryId));
    }

    private void checkFoodById(Long foodId) {
        if (foodDao.getFoodById(foodId) == null) {
            throw new FoodNotFoundException(FOOD_NOT_FOUND.formatted(foodId));
        }
    }
}
