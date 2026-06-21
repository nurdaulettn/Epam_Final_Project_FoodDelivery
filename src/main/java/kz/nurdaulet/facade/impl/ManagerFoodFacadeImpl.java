package kz.nurdaulet.facade.impl;

import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import kz.nurdaulet.exception.IncorrectAddingFoodException;
import kz.nurdaulet.facade.ManagerFoodFacade;
import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.RestaurantService;
import org.springframework.stereotype.Service;

@Service
public class ManagerFoodFacadeImpl implements ManagerFoodFacade {
    public static final String DO_NOT_HAVE_PERMISSION = "You can not manage this restaurant";
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
    }

    @Override
    public void updateFood(Long managerId, Long restaurantId, Long foodId, FoodCreateDto foodDto) {
        checkManagerAndRestaurant(managerId, restaurantId);

        categoryService.getCategoryById(foodDto.getCategoryId());

        foodService.update(foodDto, restaurantId, foodId);
    }

    @Override
    public void deleteFood(Long managerId, Long restaurantId, Long foodId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        checkManagerAndRestaurant(managerId, restaurantId);

        foodService.delete(foodId);
    }

    private void checkManagerAndRestaurant(Long managerId, Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        if (!restaurant.getManagerId().equals(managerId)) {
            throw new IncorrectAddingFoodException(DO_NOT_HAVE_PERMISSION);
        }

        if (!restaurant.getStatus().equals(RestaurantStatus.ACTIVE)) {
            throw new IncorrectAddingFoodException(DO_NOT_HAVE_PERMISSION);
        }
    }
}
