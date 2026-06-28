package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Food;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Test
    void shouldGetAllFoodsAndMapRows() throws Exception {
        FoodDaoImpl dao = new FoodDaoImpl(jdbcTemplate);
        ArgumentCaptor<RowMapper<Food>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(eq(FoodDaoImpl.FIND_ALL), mapperCaptor.capture()))
                .thenReturn(List.of(createFood()));

        List<Food> result = dao.getAllFoods();

        assertEquals(1, result.size());

        when(resultSet.getLong("id")).thenReturn(10L);
        when(resultSet.getString("name")).thenReturn("Burger");
        when(resultSet.getString("description")).thenReturn("Beef");
        when(resultSet.getDouble("price")).thenReturn(2500D);
        when(resultSet.getBoolean("is_available")).thenReturn(true);
        when(resultSet.getLong("restaurant_id")).thenReturn(2L);
        when(resultSet.getLong("category_id")).thenReturn(3L);

        Food mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(10L, mapped.getId());
        assertEquals("Burger", mapped.getName());
        assertEquals(2500D, mapped.getPrice());
        assertEquals(2L, mapped.getRestaurantId());
        assertEquals(3L, mapped.getCategoryId());
    }

    @Test
    void shouldGetFoodById() {
        FoodDaoImpl dao = new FoodDaoImpl(jdbcTemplate);
        Food food = createFood();

        when(jdbcTemplate.query(eq(FoodDaoImpl.FIND_BY_ID), any(RowMapper.class), eq(10L)))
                .thenReturn(List.of(food));

        assertEquals(food, dao.getFoodById(10L));
    }

    @Test
    void shouldReturnNullWhenFoodByNameNotFound() {
        FoodDaoImpl dao = new FoodDaoImpl(jdbcTemplate);

        when(jdbcTemplate.query(eq(FoodDaoImpl.FIND_BY_NAME), any(RowMapper.class), eq("Missing")))
                .thenReturn(List.of());

        assertNull(dao.getFoodByName("Missing"));
    }

    @Test
    void shouldQueryFoodFilters() {
        FoodDaoImpl dao = new FoodDaoImpl(jdbcTemplate);

        dao.getFoodsByCategory(1L);
        dao.getFoodsByRestaurant(2L);
        dao.getFoodsByRestaurantForManager(3L);
        dao.getFoodsBySimilarName("bur");

        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_BY_CATEGORY), any(RowMapper.class), eq(1L));
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_BY_RESTAURANT), any(RowMapper.class), eq(2L));
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_BY_RESTAURANT_FOR_MANAGER), any(RowMapper.class), eq(3L));
        verify(jdbcTemplate).query(eq(FoodDaoImpl.FIND_SIMILAR_NAME), any(RowMapper.class), eq("bur"));
    }

    @Test
    void shouldSaveUpdateDisableEnableAndDeleteFood() {
        FoodDaoImpl dao = new FoodDaoImpl(jdbcTemplate);
        Food food = createFood();

        dao.save(food);
        dao.update(food);
        dao.disableById(10L);
        dao.enableById(10L);
        dao.deleteById(10L);

        verify(jdbcTemplate).update(FoodDaoImpl.SAVE, "Burger", "Beef", 2500D, true, 2L, 3L);
        verify(jdbcTemplate).update(FoodDaoImpl.UPDATE, "Burger", "Beef", 2500D, true, 3L, 10L);
        verify(jdbcTemplate).update(FoodDaoImpl.DISABLE_BY_ID, 10L);
        verify(jdbcTemplate).update(FoodDaoImpl.ENABLE_BY_ID, 10L);
        verify(jdbcTemplate).update(FoodDaoImpl.DELETE, 10L);
    }

    private Food createFood() {
        return new Food(10L, "Burger", "Beef", 2500D, true, 2L, 3L);
    }
}
