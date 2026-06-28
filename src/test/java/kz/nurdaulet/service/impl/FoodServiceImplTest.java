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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodServiceImplTest {
    private static final Long FOOD_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long ANOTHER_RESTAURANT_ID = 11L;
    private static final Long CATEGORY_ID = 20L;
    private static final Long ANOTHER_CATEGORY_ID = 21L;
    private static final Long SECOND_FOOD_ID = 2L;
    private static final Long THIRD_FOOD_ID = 3L;
    private static final Long EXTRA_FOOD_ID = 99L;
    private static final String FOOD_NAME = "Burger";
    private static final String FOOD_NAME_WITH_SPACES = " Burger ";
    private static final String UPDATED_FOOD_NAME = "New burger";
    private static final String UPDATED_FOOD_NAME_WITH_SPACES = " New burger ";
    private static final String FOOD_DESCRIPTION = "Beef";
    private static final String UPDATED_FOOD_DESCRIPTION = "Chicken";
    private static final String SEARCH_TEXT = "burger";
    private static final Double FOOD_PRICE = 2500D;
    private static final Double UPDATED_FOOD_PRICE = 3000D;

    @Mock
    private FoodDao foodDao;

    @InjectMocks
    private FoodServiceImpl testingInstance;

    @Test
    void shouldGetFoodsWithoutFilters() {
        // given
        List<Food> foods = List.of(createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true));
        when(foodDao.getAllFoods()).thenReturn(foods);

        // when
        List<Food> result = testingInstance.getFoods(null, null, null);

        // then
        assertEquals(foods, result);
        verify(foodDao).getAllFoods();
    }

    @Test
    void shouldGetFoodsBySearchAndApplyCategoryAndRestaurantFilters() {
        // given
        List<Food> foods = new ArrayList<>();
        foods.add(createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true));
        foods.add(createFood(SECOND_FOOD_ID, ANOTHER_CATEGORY_ID, RESTAURANT_ID, true));
        foods.add(createFood(THIRD_FOOD_ID, CATEGORY_ID, ANOTHER_RESTAURANT_ID, true));
        when(foodDao.getAllFoods()).thenReturn(List.of(createFood(EXTRA_FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true)));
        when(foodDao.getFoodsBySimilarName(SEARCH_TEXT)).thenReturn(foods);

        // when
        List<Food> result = testingInstance.getFoods(SEARCH_TEXT, CATEGORY_ID, RESTAURANT_ID);

        // then
        assertEquals(1, result.size());
        assertEquals(FOOD_ID, result.get(0).getId());
        verify(foodDao).getAllFoods();
        verify(foodDao).getFoodsBySimilarName(SEARCH_TEXT);
    }

    @Test
    void shouldDelegateSimpleQueries() {
        // when
        testingInstance.getAllFood();
        testingInstance.getFoodByCategoryId(CATEGORY_ID);
        testingInstance.getFoodByRestaurantId(RESTAURANT_ID);
        testingInstance.getFoodByRestaurantIdForManager(RESTAURANT_ID);
        testingInstance.getFoodBySimilarName(SEARCH_TEXT);

        // then
        verify(foodDao).getAllFoods();
        verify(foodDao).getFoodsByCategory(CATEGORY_ID);
        verify(foodDao).getFoodsByRestaurant(RESTAURANT_ID);
        verify(foodDao).getFoodsByRestaurantForManager(RESTAURANT_ID);
        verify(foodDao).getFoodsBySimilarName(SEARCH_TEXT);
    }

    @Test
    void shouldGetFoodById() {
        // given
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        // when
        Food result = testingInstance.getFoodById(FOOD_ID);

        // then
        verify(foodDao).getFoodById(FOOD_ID);
        assertEquals(food, result);
    }

    @Test
    void shouldThrowWhenFoodNotFound() {
        // given
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(null);

        // when / then
        assertThrows(FoodNotFoundException.class, () -> testingInstance.getFoodById(FOOD_ID));
        verify(foodDao).getFoodById(FOOD_ID);
    }

    @Test
    void shouldSaveFood() {
        // given
        FoodCreateDto dto = new FoodCreateDto(FOOD_NAME_WITH_SPACES, FOOD_DESCRIPTION, FOOD_PRICE, CATEGORY_ID);
        ArgumentCaptor<Food> captor = ArgumentCaptor.forClass(Food.class);

        // when
        testingInstance.save(dto, RESTAURANT_ID);

        // then
        verify(foodDao).save(captor.capture());
        Food saved = captor.getValue();
        assertEquals(FOOD_NAME, saved.getName());
        assertEquals(FOOD_DESCRIPTION, saved.getDescription());
        assertEquals(FOOD_PRICE, saved.getPrice());
        assertEquals(CATEGORY_ID, saved.getCategoryId());
        assertEquals(RESTAURANT_ID, saved.getRestaurantId());
        assertEquals(true, saved.getAvailable());
    }

    @Test
    void shouldUpdateFood() {
        // given
        Food existingFood = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        FoodCreateDto dto = new FoodCreateDto(
                UPDATED_FOOD_NAME_WITH_SPACES,
                UPDATED_FOOD_DESCRIPTION,
                UPDATED_FOOD_PRICE,
                ANOTHER_CATEGORY_ID
        );
        ArgumentCaptor<Food> captor = ArgumentCaptor.forClass(Food.class);

        when(foodDao.getFoodById(FOOD_ID)).thenReturn(existingFood);

        // when
        testingInstance.update(dto, ANOTHER_RESTAURANT_ID, FOOD_ID);

        // then
        verify(foodDao, times(2)).getFoodById(FOOD_ID);
        verify(foodDao).update(captor.capture());
        Food updated = captor.getValue();
        assertEquals(FOOD_ID, updated.getId());
        assertEquals(UPDATED_FOOD_NAME, updated.getName());
        assertEquals(UPDATED_FOOD_DESCRIPTION, updated.getDescription());
        assertEquals(UPDATED_FOOD_PRICE, updated.getPrice());
        assertEquals(ANOTHER_CATEGORY_ID, updated.getCategoryId());
        assertEquals(ANOTHER_RESTAURANT_ID, updated.getRestaurantId());
    }

    @Test
    void shouldDisableAndEnableFood() {
        // given
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        // when
        testingInstance.disableFood(FOOD_ID);
        testingInstance.enableFood(FOOD_ID);

        // then
        verify(foodDao, times(2)).getFoodById(FOOD_ID);
        verify(foodDao).disableById(FOOD_ID);
        verify(foodDao).enableById(FOOD_ID);
    }

    @Test
    void shouldDeleteInactiveFood() {
        // given
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, false);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        // when
        testingInstance.delete(FOOD_ID);

        // then
        verify(foodDao, times(2)).getFoodById(FOOD_ID);
        verify(foodDao).deleteById(FOOD_ID);
    }

    @Test
    void shouldNotDeleteActiveFood() {
        // given
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        // when / then
        assertThrows(DeletingActiveFoodException.class, () -> testingInstance.delete(FOOD_ID));
        verify(foodDao, times(2)).getFoodById(FOOD_ID);
    }

    @Test
    void shouldGetFoodCreateDtoById() {
        // given
        Food food = createFood(FOOD_ID, CATEGORY_ID, RESTAURANT_ID, true);
        when(foodDao.getFoodById(FOOD_ID)).thenReturn(food);

        // when
        FoodCreateDto result = testingInstance.getFoodCreateDtoById(FOOD_ID);

        // then
        assertEquals(food.getName(), result.getName());
        assertEquals(food.getDescription(), result.getDescription());
        assertEquals(food.getPrice(), result.getPrice());
        assertEquals(food.getCategoryId(), result.getCategoryId());
        verify(foodDao, times(2)).getFoodById(FOOD_ID);
    }

    private Food createFood(Long id, Long categoryId, Long restaurantId, Boolean available) {
        return new Food(id, FOOD_NAME, FOOD_DESCRIPTION, FOOD_PRICE, available, restaurantId, categoryId);
    }
}
