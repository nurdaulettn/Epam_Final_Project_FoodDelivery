package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.FoodDao;
import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.exception.DeletingActiveFoodException;
import kz.nurdaulet.exception.FoodNotFoundException;
import kz.nurdaulet.service.FoodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {
    private static final Logger log = LoggerFactory.getLogger(FoodServiceImpl.class);
    public static final String CAN_NOT_DELETE_FOOD = "Can't delete active food";
    public static final String FOOD_NOT_FOUND = "Food with id %d found";
    private static final String LOG_FOOD_CREATED = "Food created: name={}, restaurantId={}, categoryId={}";
    private static final String LOG_FOOD_UPDATED = "Food {} updated for restaurant {}";
    private static final String LOG_FOOD_DISABLED = "Food {} disabled";
    private static final String LOG_FOOD_ENABLED = "Food {} enabled";
    private static final String LOG_FOOD_DELETED = "Food {} deleted";
    private static final String LOG_ACTIVE_FOOD_DELETE_REJECTED = "Rejected deletion of active food {}";
    private static final String LOG_FOOD_NOT_FOUND = "Food {} was not found";
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
        log.info(LOG_FOOD_CREATED,
                food.getName(),
                restaurantId,
                food.getCategoryId());
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
        log.info(LOG_FOOD_UPDATED, foodId, restaurantId);
    }

    @Override
    public void disableFood(Long foodId) {
        checkFoodById(foodId);

        foodDao.disableById(foodId);
        log.info(LOG_FOOD_DISABLED, foodId);
    }

    @Override
    public void enableFood(Long foodId) {
        checkFoodById(foodId);

        foodDao.enableById(foodId);
        log.info(LOG_FOOD_ENABLED, foodId);
    }

    @Override
    public void delete(Long id) {
        checkFoodById(id);

        Food food = foodDao.getFoodById(id);

        if (!food.getAvailable()) {
            foodDao.deleteById(id);
            log.info(LOG_FOOD_DELETED, id);
        } else {
            log.warn(LOG_ACTIVE_FOOD_DELETE_REJECTED, id);
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
            log.warn(LOG_FOOD_NOT_FOUND, foodId);
            throw new FoodNotFoundException(FOOD_NOT_FOUND.formatted(foodId));
        }
    }
}
