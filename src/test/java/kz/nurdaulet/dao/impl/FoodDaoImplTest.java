package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Food;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodDaoImplTest {
    private static final Long FOOD_ID = 10L;
    private static final Long CATEGORY_ID = 3L;
    private static final Long FILTER_CATEGORY_ID = 1L;
    private static final Long RESTAURANT_ID = 2L;
    private static final String FOOD_NAME = "Burger";
    private static final String FOOD_DESCRIPTION = "Beef";
    private static final String MISSING_FOOD = "Missing";
    private static final String SEARCH_TEXT = "bur";
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final String PRICE_COLUMN = "price";
    private static final String IS_AVAILABLE_COLUMN = "is_available";
    private static final String RESTAURANT_ID_COLUMN = "restaurant_id";
    private static final String CATEGORY_ID_COLUMN = "category_id";
    private static final Double FOOD_PRICE = 2500D;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Captor
    ArgumentCaptor<RowMapper<Food>> foodCaptor;

    @InjectMocks
    FoodDaoImpl testingInstance;

    @Test
    void shouldGetAllFoodsAndMapRows() throws Exception {
        // given
        when(jdbcTemplate.query(eq(FoodDaoImpl.FIND_ALL), any(RowMapper.class)))
                .thenReturn(List.of(createFood()));
        when(resultSet.getLong(ID_COLUMN)).thenReturn(FOOD_ID);
        when(resultSet.getString(NAME_COLUMN)).thenReturn(FOOD_NAME);
        when(resultSet.getString(DESCRIPTION_COLUMN)).thenReturn(FOOD_DESCRIPTION);
        when(resultSet.getDouble(PRICE_COLUMN)).thenReturn(FOOD_PRICE);
        when(resultSet.getBoolean(IS_AVAILABLE_COLUMN)).thenReturn(true);
        when(resultSet.getLong(RESTAURANT_ID_COLUMN)).thenReturn(RESTAURANT_ID);
        when(resultSet.getLong(CATEGORY_ID_COLUMN)).thenReturn(CATEGORY_ID);

        // when
        List<Food> result = testingInstance.getAllFoods();

        // then
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_ALL), foodCaptor.capture());
        assertEquals(1, result.size());

        Food mappedFood = foodCaptor.getValue().mapRow(resultSet, 0);
        assertEquals(FOOD_ID, mappedFood.getId());
        assertEquals(FOOD_NAME, mappedFood.getName());
        assertEquals(FOOD_PRICE, mappedFood.getPrice());
        assertEquals(RESTAURANT_ID, mappedFood.getRestaurantId());
        assertEquals(CATEGORY_ID, mappedFood.getCategoryId());
    }

    @Test
    void shouldGetFoodById() {
        // given
        Food food = createFood();

        when(jdbcTemplate.query(eq(FoodDaoImpl.FIND_BY_ID), any(RowMapper.class), eq(FOOD_ID)))
                .thenReturn(List.of(food));

        // when
        Food result = testingInstance.getFoodById(FOOD_ID);

        // then
        assertEquals(food, result);
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_BY_ID), any(RowMapper.class), eq(FOOD_ID));
    }

    @Test
    void shouldReturnNullWhenFoodByNameNotFound() {
        // given
        when(jdbcTemplate.query(eq(FoodDaoImpl.FIND_BY_NAME), any(RowMapper.class), eq(MISSING_FOOD)))
                .thenReturn(List.of());

        // when
        Food result = testingInstance.getFoodByName(MISSING_FOOD);

        // then
        assertNull(result);
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_BY_NAME), any(RowMapper.class), eq(MISSING_FOOD));
    }

    @Test
    void shouldQueryFoodFilters() {
        // when
        testingInstance.getFoodsByCategory(FILTER_CATEGORY_ID);
        testingInstance.getFoodsByRestaurant(RESTAURANT_ID);
        testingInstance.getFoodsByRestaurantForManager(CATEGORY_ID);
        testingInstance.getFoodsBySimilarName(SEARCH_TEXT);

        // then
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_BY_CATEGORY), any(RowMapper.class), eq(FILTER_CATEGORY_ID));
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_BY_RESTAURANT), any(RowMapper.class), eq(RESTAURANT_ID));
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_BY_RESTAURANT_FOR_MANAGER), any(RowMapper.class), eq(CATEGORY_ID));
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_SIMILAR_NAME), any(RowMapper.class), eq(SEARCH_TEXT));
    }

    @Test
    void shouldSaveUpdateDisableEnableAndDeleteFood() {
        // given
        Food food = createFood();

        // when
        testingInstance.save(food);
        testingInstance.update(food);
        testingInstance.disableById(FOOD_ID);
        testingInstance.enableById(FOOD_ID);
        testingInstance.deleteById(FOOD_ID);

        // then
        verify(jdbcTemplate).update(FoodDaoImpl.SAVE, FOOD_NAME, FOOD_DESCRIPTION, FOOD_PRICE, true, RESTAURANT_ID, CATEGORY_ID);
        verify(jdbcTemplate).update(FoodDaoImpl.UPDATE, FOOD_NAME, FOOD_DESCRIPTION, FOOD_PRICE, true, CATEGORY_ID, FOOD_ID);
        verify(jdbcTemplate).update(FoodDaoImpl.DISABLE_BY_ID, FOOD_ID);
        verify(jdbcTemplate).update(FoodDaoImpl.ENABLE_BY_ID, FOOD_ID);
        verify(jdbcTemplate).update(FoodDaoImpl.DELETE, FOOD_ID);
    }

    private Food createFood() {
        return new Food(FOOD_ID, FOOD_NAME, FOOD_DESCRIPTION, FOOD_PRICE, true, RESTAURANT_ID, CATEGORY_ID);
    }
}
