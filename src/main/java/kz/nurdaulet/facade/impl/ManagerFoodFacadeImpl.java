package kz.nurdaulet.facade.impl;

import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import kz.nurdaulet.exception.IncorrectAddingFoodException;
import kz.nurdaulet.facade.ManagerFoodFacade;
import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ManagerFoodFacadeImpl implements ManagerFoodFacade {
    private static final Logger log = LoggerFactory.getLogger(ManagerFoodFacadeImpl.class);
    private static final String DO_NOT_HAVE_PERMISSION = "You can not manage this restaurant";
    private static final String LOG_MANAGER_CREATED_FOOD = "Manager {} created food in restaurant {}";
    private static final String LOG_MANAGER_UPDATED_FOOD = "Manager {} updated food {} in restaurant {}";
    private static final String LOG_MANAGER_DELETED_FOOD = "Manager {} deleted food {} in restaurant {}";
    private static final String LOG_MANAGER_DISABLED_FOOD = "Manager {} disabled food {} in restaurant {}";
    private static final String LOG_MANAGER_ENABLED_FOOD = "Manager {} enabled food {} in restaurant {}";
    private static final String LOG_MANAGER_RESTAURANT_OWNER_MISMATCH =
            "Manager {} tried to manage restaurant {} owned by manager {}";
    private static final String LOG_MANAGER_RESTAURANT_INVALID_STATUS =
            "Manager {} tried to manage restaurant {} with status {}";
    private static final String LOG_MANAGER_FOOD_RESTAURANT_MISMATCH =
            "Manager {} tried to manage food {} from restaurant {} through restaurant {}";

    private final RestaurantService restaurantService;
    private final FoodService foodService;
    private final CategoryService categoryService;

    public ManagerFoodFacadeImpl(RestaurantService restaurantService,
                                 FoodService foodService,
                                 CategoryService categoryService) {
        this.restaurantService = restaurantService;
        this.foodService = foodService;
        this.categoryService = categoryService;
    }

    @Override
    public void createFood(Long managerId, Long restaurantId, FoodCreateDto foodDto) {
        checkManagerAndRestaurant(managerId, restaurantId);

        categoryService.getCategoryById(foodDto.getCategoryId());

        foodService.save(foodDto, restaurantId);
        log.info(LOG_MANAGER_CREATED_FOOD, managerId, restaurantId);
    }

    @Override
    public void updateFood(Long managerId, Long restaurantId, Long foodId, FoodCreateDto foodDto) {
        checkManagerAndRestaurantAndFood(managerId, restaurantId, foodId);

        categoryService.getCategoryById(foodDto.getCategoryId());

        foodService.update(foodDto, restaurantId, foodId);
        log.info(LOG_MANAGER_UPDATED_FOOD, managerId, foodId, restaurantId);
    }

    @Override
    public void deleteFood(Long managerId, Long restaurantId, Long foodId) {
        checkManagerAndRestaurantAndFood(managerId, restaurantId, foodId);

        foodService.delete(foodId);
        log.info(LOG_MANAGER_DELETED_FOOD, managerId, foodId, restaurantId);
    }

    @Override
    public void disableFood(Long managerId, Long restaurantId, Long foodId) {
        checkManagerAndRestaurantAndFood(managerId, restaurantId, foodId);

        foodService.disableFood(foodId);
        log.info(LOG_MANAGER_DISABLED_FOOD, managerId, foodId, restaurantId);
    }

    @Override
    public void enableFood(Long managerId, Long restaurantId, Long foodId) {
        checkManagerAndRestaurantAndFood(managerId, restaurantId, foodId);

        foodService.enableFood(foodId);
        log.info(LOG_MANAGER_ENABLED_FOOD, managerId, foodId, restaurantId);
    }

    public void checkManagerAndRestaurant(Long managerId, Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        if (!restaurant.getManagerId().equals(managerId)) {
            log.warn(LOG_MANAGER_RESTAURANT_OWNER_MISMATCH,
                    managerId,
                    restaurantId,
                    restaurant.getManagerId());
            throw new IncorrectAddingFoodException(DO_NOT_HAVE_PERMISSION);
        }

        if (!(restaurant.getStatus().equals(RestaurantStatus.ACTIVE)
        || restaurant.getStatus().equals(RestaurantStatus.INACTIVE))) {
            log.warn(LOG_MANAGER_RESTAURANT_INVALID_STATUS,
                    managerId,
                    restaurantId,
                    restaurant.getStatus());
            throw new IncorrectAddingFoodException(DO_NOT_HAVE_PERMISSION);
        }
    }

    private void checkManagerAndRestaurantAndFood(Long managerId, Long restaurantId, Long foodId) {
        checkManagerAndRestaurant(managerId, restaurantId);

        Food food = foodService.getFoodById(foodId);

        if (!food.getRestaurantId().equals(restaurantId)) {
            log.warn(LOG_MANAGER_FOOD_RESTAURANT_MISMATCH,
                    managerId,
                    foodId,
                    food.getRestaurantId(),
                    restaurantId);
            throw new IncorrectAddingFoodException(DO_NOT_HAVE_PERMISSION);
        }
    }
}
