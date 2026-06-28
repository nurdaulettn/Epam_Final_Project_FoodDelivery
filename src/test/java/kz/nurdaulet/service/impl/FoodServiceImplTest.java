package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.FoodDao;
import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.exception.DeletingActiveFoodException;
import kz.nurdaulet.exception.FoodNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceImplTest {
    private static final Long FOOD_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long ANOTHER_RESTAURANT_ID = 11L;
    private static final Long CATEGORY_ID = 20L;
    private static final Long ANOTHER_CATEGORY_ID = 21L;

    @Mock
    private FoodDao foodDao;

    @InjectMocks
    private FoodServiceImpl testingInstance;

    @Test
    void shouldGetFoodsWithoutFilters() {
        List<Food> foods = List.of(createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true));
        when(foodDao.getAllFoods()).thenReturn(foods);

        assertEquals(foods, testingInstance.getFoods(null, null, null));
    }

    @Test
    void shouldGetFoodsBySearchAndApplyCategoryAndRestaurantFilters() {
        List<Food> foods = new ArrayList<>();
        foods.add(createFood(1L, CATEGORY_ID, RESTAURANT_ID, true));
        foods.add(createFood(2L, ANOTHER_CATEGORY_ID, RESTAURANT_ID, true));
        foods.add(createFood(3L, CATEGORY_ID, ANOTHER_RESTAURANT_ID, true));

        when(foodDao.getAllFoods()).thenReturn(List.of(createFood(99L, CATEGORY_ID, RESTAURANT_ID, true)));
        when(foodDao.getFoodsBySimilarName("burger")).thenReturn(foods);

        List<Food> result = testingInstance.getFoods("burger", CATEGORY_ID, RESTAURANT_ID);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void shouldDelegateSimpleQueries() {
        testingInstance.getAllFood();
        testingInstance.getFoodByCategoryId(CATEGORY_ID);
        testingInstance.getFoodByRestaurantId(RESTAURANT_ID);
        testingInstance.getFoodByRestaurantIdForManager(RESTAURANT_ID);
        testingInstance.getFoodBySimilarName("burger");

        verify(foodDao).getAllFoods();
        verify(foodDao).getFoodsByCategory(CATEGORY_ID);
        verify(foodDao).getFoodsByRestaurant(RESTAURANT_ID);
        verify(foodDao).getFoodsByRestaurantForManager(RESTAURANT_ID);
        verify(foodDao).getFoodsBySimilarName("burger");
    }

    @Test
    void shouldGetFoodById() {
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        assertEquals(food, testingInstance.getFoodById(FOOD_ID));
    }

    @Test
    void shouldThrowWhenFoodNotFound() {
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(null);

        assertThrows(FoodNotFoundException.class, () -> testingInstance.getFoodById(FOOD_ID));
    }

    @Test
    void shouldSaveFood() {
        FoodCreateDto dto = new FoodCreateDto(" Burger ", "Beef", 2500D, CATEGORY_ID);
        ArgumentCaptor<Food> captor = ArgumentCaptor.forClass(Food.class);

        testingInstance.save(dto, RESTAURANT_ID);

        verify(foodDao).save(captor.capture());
        Food saved = captor.getValue();
        assertEquals("Burger", saved.getName());
        assertEquals("Beef", saved.getDescription());
        assertEquals(2500D, saved.getPrice());
        assertEquals(CATEGORY_ID, saved.getCategoryId());
        assertEquals(RESTAURANT_ID, saved.getRestaurantId());
        assertEquals(true, saved.getAvailable());
    }

    @Test
    void shouldUpdateFood() {
        Food existingFood = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        FoodCreateDto dto = new FoodCreateDto(" New burger ", "Chicken", 3000D, ANOTHER_CATEGORY_ID);
        ArgumentCaptor<Food> captor = ArgumentCaptor.forClass(Food.class);

        when(foodDao.getFoodById(FOOD_ID)).thenReturn(existingFood);

        testingInstance.update(dto, ANOTHER_RESTAURANT_ID, FOOD_ID);

        verify(foodDao).update(captor.capture());
        Food updated = captor.getValue();
        assertEquals(FOOD_ID, updated.getId());
        assertEquals("New burger", updated.getName());
        assertEquals("Chicken", updated.getDescription());
        assertEquals(3000D, updated.getPrice());
        assertEquals(ANOTHER_CATEGORY_ID, updated.getCategoryId());
        assertEquals(ANOTHER_RESTAURANT_ID, updated.getRestaurantId());
    }

    @Test
    void shouldDisableAndEnableFood() {
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        testingInstance.disableFood(FOOD_ID);
        testingInstance.enableFood(FOOD_ID);

        verify(foodDao).disableById(FOOD_ID);
        verify(foodDao).enableById(FOOD_ID);
    }

    @Test
    void shouldDeleteInactiveFood() {
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, false);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        testingInstance.delete(FOOD_ID);

        verify(foodDao).deleteById(FOOD_ID);
    }

    @Test
    void shouldNotDeleteActiveFood() {
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        assertThrows(DeletingActiveFoodException.class, () -> testingInstance.delete(FOOD_ID));
    }

    @Test
    void shouldGetFoodCreateDtoById() {
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        FoodCreateDto result = testingInstance.getFoodCreateDtoById(FOOD_ID);

        assertEquals(food.getName(), result.getName());
        assertEquals(food.getDescription(), result.getDescription());
        assertEquals(food.getPrice(), result.getPrice());
        assertEquals(food.getCategoryId(), result.getCategoryId());
    }

    private Food createFood(Long id, Long categoryId, Long restaurantId, Boolean available) {
        return new Food(id, "Burger", "Beef", 2500D, available, restaurantId, categoryId);
    }
}
