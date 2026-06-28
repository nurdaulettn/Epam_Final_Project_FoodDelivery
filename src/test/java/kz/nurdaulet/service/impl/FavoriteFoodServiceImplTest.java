package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.FavoriteFoodDao;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.service.FoodService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteFoodServiceImplTest {
    private static final Long USER_ID = 1L;
    private static final Long FOOD_ID = 10L;
    private static final Long SECOND_FOOD_ID = 11L;

    @Mock
    private FavoriteFoodDao favoriteFoodDao;

    @Mock
    private FoodService foodService;

    @InjectMocks
    private FavoriteFoodServiceImpl testingInstance;

    @Test
    void shouldGetFavoriteFoods() {
        // given
        Food food = createFood(FOOD_ID);
        Food secondFood = createFood(SECOND_FOOD_ID);
        when(favoriteFoodDao.findFoodIdsByUserId(USER_ID)).thenReturn(List.of(FOOD_ID, SECOND_FOOD_ID));
        when(foodService.getFoodById(FOOD_ID)).thenReturn(food);
        when(foodService.getFoodById(SECOND_FOOD_ID)).thenReturn(secondFood);

        // when
        List<Food> result = testingInstance.getFavoriteFoods(USER_ID);

        // then
        assertEquals(List.of(food, secondFood), result);
        verify(favoriteFoodDao).findFoodIdsByUserId(USER_ID);
        verify(foodService).getFoodById(FOOD_ID);
        verify(foodService).getFoodById(SECOND_FOOD_ID);
    }

    @Test
    void shouldAddFavoriteWhenItDoesNotExist() {
        // given
        when(favoriteFoodDao.existsByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(false);

        // when
        testingInstance.addFavorite(USER_ID, FOOD_ID);

        // then
        verify(foodService).getFoodById(FOOD_ID);
        verify(favoriteFoodDao).save(USER_ID, FOOD_ID);
    }

    @Test
    void shouldNotAddDuplicateFavorite() {
        // given
        when(favoriteFoodDao.existsByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(true);

        // when
        testingInstance.addFavorite(USER_ID, FOOD_ID);

        // then
        verify(foodService).getFoodById(FOOD_ID);
        verify(favoriteFoodDao, never()).save(USER_ID, FOOD_ID);
    }

    @Test
    void shouldRemoveFavoriteWhenItExists() {
        // given
        when(favoriteFoodDao.existsByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(true);

        // when
        testingInstance.removeFavorite(USER_ID, FOOD_ID);

        // then
        verify(favoriteFoodDao).delete(USER_ID, FOOD_ID);
    }

    @Test
    void shouldCheckFavorite() {
        // given
        when(favoriteFoodDao.existsByUserIdAndFoodId(USER_ID, FOOD_ID)).thenReturn(true);

        // when
        boolean result = testingInstance.isFavorite(USER_ID, FOOD_ID);

        // then
        assertTrue(result);
        verify(favoriteFoodDao).existsByUserIdAndFoodId(USER_ID, FOOD_ID);
    }

    private Food createFood(Long foodId) {
        return new Food(foodId, "Food", "Description", 2500D, true, 1L, 1L);
    }
}
