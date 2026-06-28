package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Test
    void shouldGetRestaurantsAndMapRows() throws Exception {
        RestaurantDaoImpl dao = new RestaurantDaoImpl(jdbcTemplate);
        ArgumentCaptor<RowMapper<Restaurant>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(eq("SELECT * FROM restaurants WHERE status='ACTIVE'"), mapperCaptor.capture()))
                .thenReturn(List.of(createRestaurant()));

        List<Restaurant> result = dao.getRestaurants();

        assertEquals(1, result.size());

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Burger House");
        when(resultSet.getString("description")).thenReturn("Fast food");
        when(resultSet.getString("address")).thenReturn("Abay 1");
        when(resultSet.getString("phone")).thenReturn("+7 (777) 777 7777");
        when(resultSet.getDouble("rating_avg")).thenReturn(4.5D);
        when(resultSet.getInt("rating_count")).thenReturn(10);
        when(resultSet.getObject("opening_time", LocalTime.class)).thenReturn(LocalTime.of(9, 0));
        when(resultSet.getObject("closing_time", LocalTime.class)).thenReturn(LocalTime.of(22, 0));
        when(resultSet.getLong("manager_id")).thenReturn(5L);
        when(resultSet.getString("status")).thenReturn("ACTIVE");
        when(resultSet.getObject("created_at", LocalDateTime.class)).thenReturn(now);
        when(resultSet.getObject("updated_at", LocalDateTime.class)).thenReturn(now);

        Restaurant mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(1L, mapped.getId());
        assertEquals("Burger House", mapped.getName());
        assertEquals(RestaurantStatus.ACTIVE, mapped.getStatus());
        assertEquals(5L, mapped.getManagerId());
    }

    @Test
    void shouldFindRestaurantMethods() {
        RestaurantDaoImpl dao = new RestaurantDaoImpl(jdbcTemplate);
        Restaurant restaurant = createRestaurant();

        when(jdbcTemplate.query(eq("SELECT * FROM restaurants WHERE id = ?"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(restaurant), List.of(restaurant), List.of());
        when(jdbcTemplate.query(eq("SELECT * FROM restaurants WHERE name = ? AND (status='ACTIVE' OR status='INACTIVE')"),
                any(RowMapper.class), eq("Burger House"))).thenReturn(List.of(restaurant));

        assertEquals(restaurant, dao.findById(1L));
        assertTrue(dao.existsById(1L));
        assertFalse(dao.existsById(1L));
        assertEquals(restaurant, dao.findByName("Burger House"));
    }

    @Test
    void shouldQueryRestaurantLists() {
        RestaurantDaoImpl dao = new RestaurantDaoImpl(jdbcTemplate);

        dao.findBySimilarName("burger");
        dao.findByManagerId(5L);
        dao.findPendingRestaurants();

        verify(jdbcTemplate).query(
                eq("SELECT * FROM restaurants WHERE name ILIKE CONCAT('%', ?, '%') AND status='ACTIVE'"),
                any(RowMapper.class),
                eq("burger"));
        verify(jdbcTemplate).query(eq("SELECT * FROM restaurants WHERE manager_id = ?"), any(RowMapper.class), eq(5L));
        verify(jdbcTemplate).query(eq("SELECT * FROM restaurants WHERE status='PENDING'"), any(RowMapper.class));
    }

    @Test
    void shouldSaveUpdateDeleteAndChangeStatus() {
        RestaurantDaoImpl dao = new RestaurantDaoImpl(jdbcTemplate);
        Restaurant restaurant = createRestaurant();

        dao.save(restaurant);
        dao.update(restaurant);
        dao.deleteById(1L);
        dao.activateRestaurant(1L);
        dao.rejectRestaurant(1L);

        verify(jdbcTemplate).update(
                eq("INSERT INTO restaurants (name, description, address, phone, opening_time, closing_time, manager_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"),
                eq("Burger House"), eq("Fast food"), eq("Abay 1"), eq("+7 (777) 777 7777"),
                eq(LocalTime.of(9, 0)), eq(LocalTime.of(22, 0)), eq(5L), eq("ACTIVE"));
        verify(jdbcTemplate).update(
                eq("UPDATE restaurants SET name=?, description=?, address=?, phone=?, rating_avg=?, rating_count=?, opening_time=?, closing_time=?, updated_at=? WHERE id=?"),
                eq("Burger House"), eq("Fast food"), eq("Abay 1"), eq("+7 (777) 777 7777"),
                eq(4.5D), eq(10), eq(LocalTime.of(9, 0)), eq(LocalTime.of(22, 0)),
                eq(restaurant.getUpdatedAt()), eq(1L));
        verify(jdbcTemplate).update("DELETE FROM restaurants WHERE id=?", 1L);
        verify(jdbcTemplate).update("UPDATE restaurants SET status='ACTIVE' WHERE id = ?", 1L);
        verify(jdbcTemplate).update("UPDATE restaurants SET status='REJECTED' WHERE id = ?", 1L);
    }

    private Restaurant createRestaurant() {
        LocalDateTime now = LocalDateTime.now();
        return new Restaurant(
                1L,
                "Burger House",
                "Fast food",
                "Abay 1",
                "+7 (777) 777 7777",
                4.5D,
                10,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                5L,
                RestaurantStatus.ACTIVE,
                now,
                now
        );
    }
}
