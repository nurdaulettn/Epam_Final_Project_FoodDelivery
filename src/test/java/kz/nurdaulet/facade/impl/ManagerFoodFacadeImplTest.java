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
    private static final String FOOD_NAME = "Burger";
    private static final String FOOD_DESCRIPTION = "Beef";
    private static final Double FOOD_PRICE = 2500D;

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
        // given
        FoodCreateDto dto = createFoodDto();
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));

        // when
        testingInstance.createFood(MANAGER_ID, RESTAURANT_ID, dto);

        // then
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(categoryService).getCategoryById(CATEGORY_ID);
        verify(foodService).save(dto, RESTAURANT_ID);
    }

    @Test
    void shouldUpdateFoodWhenManagerOwnsRestaurantAndFoodBelongsToRestaurant() {
        // given
        FoodCreateDto dto = createFoodDto();
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));
        when(foodService.getFoodById(FOOD_ID))
                .thenReturn(createFood(RESTAURANT_ID));

        // when
        testingInstance.updateFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID, dto);

        // then
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(foodService).getFoodById(FOOD_ID);
        verify(categoryService).getCategoryById(CATEGORY_ID);
        verify(foodService).update(dto, RESTAURANT_ID, FOOD_ID);
    }

    @Test
    void shouldDeleteFood() {
        // given
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));
        when(foodService.getFoodById(FOOD_ID))
                .thenReturn(createFood(RESTAURANT_ID));

        // when
        testingInstance.deleteFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID);

        // then
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(foodService).getFoodById(FOOD_ID);
        verify(foodService).delete(FOOD_ID);
    }

    @Test
    void shouldDisableAndEnableFood() {
        // given
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));
        when(foodService.getFoodById(FOOD_ID))
                .thenReturn(createFood(RESTAURANT_ID));

        // when
        testingInstance.disableFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID);
        testingInstance.enableFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID);

        // then
        verify(restaurantService, org.mockito.Mockito.times(2)).getRestaurantById(RESTAURANT_ID);
        verify(foodService, org.mockito.Mockito.times(2)).getFoodById(FOOD_ID);
        verify(foodService).disableFood(FOOD_ID);
        verify(foodService).enableFood(FOOD_ID);
    }

    @Test
    void shouldRejectWhenManagerDoesNotOwnRestaurant() {
        // given
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(ANOTHER_MANAGER_ID, RestaurantStatus.ACTIVE));

        // when / then
        assertThrows(IncorrectAddingFoodException.class,
                () -> testingInstance.createFood(MANAGER_ID, RESTAURANT_ID, createFoodDto()));

        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verifyNoInteractions(categoryService);
        verifyNoInteractions(foodService);
    }

    @Test
    void shouldRejectWhenRestaurantIsPending() {
        // given
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.PENDING));

        // when / then
        assertThrows(IncorrectAddingFoodException.class,
                () -> testingInstance.createFood(MANAGER_ID, RESTAURANT_ID, createFoodDto()));

        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verifyNoInteractions(categoryService);
        verifyNoInteractions(foodService);
    }

    @Test
    void shouldRejectWhenFoodBelongsToAnotherRestaurant() {
        // given
        when(restaurantService.getRestaurantById(RESTAURANT_ID))
                .thenReturn(createRestaurant(MANAGER_ID, RestaurantStatus.ACTIVE));
        when(foodService.getFoodById(FOOD_ID))
                .thenReturn(createFood(ANOTHER_RESTAURANT_ID));

        // when / then
        assertThrows(IncorrectAddingFoodException.class,
                () -> testingInstance.updateFood(MANAGER_ID, RESTAURANT_ID, FOOD_ID, createFoodDto()));
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(foodService).getFoodById(FOOD_ID);
        verifyNoInteractions(categoryService);
    }

    private FoodCreateDto createFoodDto() {
        return new FoodCreateDto(FOOD_NAME, FOOD_DESCRIPTION, FOOD_PRICE, CATEGORY_ID);
    }

    private Restaurant createRestaurant(Long managerId, RestaurantStatus status) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(RESTAURANT_ID);
        restaurant.setManagerId(managerId);
        restaurant.setStatus(status);

        return restaurant;
    }

    private Food createFood(Long restaurantId) {
        return new Food(FOOD_ID, FOOD_NAME, FOOD_DESCRIPTION, FOOD_PRICE, true, restaurantId, CATEGORY_ID);
    }
}
