package kz.nurdaulet.facade.impl;

import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import kz.nurdaulet.exception.IncorrectAddingFoodException;
import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagerFoodFacadeImplTest {
    private static final Long MANAGER_ID = 1L;
    private static final Long ANOTHER_MANAGER_ID = 2L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long ANOTHER_RESTAURANT_ID = 11L;
    private static final Long FOOD_ID = 100L;
    private static final Long CATEGORY_ID = 200L;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private FoodService foodService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ManagerFoodFacadeImpl testingInstance;

    @Test
    void shouldCreateFoodWhenManagerOwnsActiveRestaurant() {
        FoodCreateDto dto = createFoodDto();
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));

        testingInstance.createFood(MANAGER_ID, RESTAURANT_ID, dto);

        verify(categoryService).getCategoryById(CATEGORY_ID);
        verify(foodService).save(dto, RESTAURANT_ID);
    }

    @Test
    void shouldUpdateFoodWhenManagerOwnsRestaurantAndFoodBelongsToRestaurant() {
        FoodCreateDto dto = createFoodDto();
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));
        when(foodService.getFoodById(FOOD_ID))
                .thenReturn(createFood(RESTAURANT_ID));

        testingInstance.updateFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID, dto);

        verify(categoryService).getCategoryById(CATEGORY_ID);
        verify(foodService).update(dto, RESTAURANT_ID, FOOD_ID);
    }

    @Test
    void shouldDeleteFood() {
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));
        when(foodService.getFoodById(FOOD_ID))
                .thenReturn(createFood(RESTAURANT_ID));

        testingInstance.deleteFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID);

        verify(foodService).delete(FOOD_ID);
    }

    @Test
    void shouldDisableAndEnableFood() {
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));
        when(foodService.getFoodById(FOOD_ID))
                .thenReturn(createFood(RESTAURANT_ID));

        testingInstance.disableFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID);
        testingInstance.enableFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID);

        verify(foodService).disableFood(FOOD_ID);
        verify(foodService).enableFood(FOOD_ID);
    }

    @Test
    void shouldRejectWhenManagerDoesNotOwnRestaurant() {
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(ANOTHER_MANAGER_ID, RestaurantStatus.ACTIVE));

        assertThrows(IncorrectAddingFoodException.class,
                () -> testingInstance.createFood(MANAGER_ID, RESTAURANT_ID, createFoodDto()));

        verifyNoInteractions(categoryService);
        verifyNoInteractions(foodService);
    }

    @Test
    void shouldRejectWhenRestaurantIsPending() {
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.PENDING));

        assertThrows(IncorrectAddingFoodException.class,
                () -> testingInstance.createFood(MANAGER_ID, RESTAURANT_ID, createFoodDto()));

        verifyNoInteractions(categoryService);
        verifyNoInteractions(foodService);
    }

    @Test
    void shouldRejectWhenFoodBelongsToAnotherRestaurant() {
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));
        when(foodService.getFoodById(FOOD_ID))
                .thenReturn(createFood(ANOTHER_RESTAURANT_ID));

        assertThrows(IncorrectAddingFoodException.class,
                () -> testingInstance.updateFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID, createFoodDto()));
    }

    private FoodCreateDto createFoodDto() {
        return new FoodCreateDto("Burger", "Beef", 2500D, CATEGORY_ID);
    }

    private Restaurant createRestaurant(Long managerId, RestaurantStatus status) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(RESTAURANT_ID);
        restaurant.setManagerId(managerId);
        restaurant.setStatus(status);

        return restaurant;
    }

    private Food createFood(Long restaurantId) {
        return new Food(FOOD_ID, "Burger", "Beef", 2500D, true, restaurantId, CATEGORY_ID);
    }
}
