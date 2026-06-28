package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
    private static final Long RESTAURANT_ID = 1L;
    private static final Long MANAGER_ID = 5L;
    private static final String RESTAURANT_NAME = "Burger House";
    private static final String RESTAURANT_DESCRIPTION = "Fast food";
    private static final String RESTAURANT_ADDRESS = "Abay 1";
    private static final String PHONE = "+7 (777) 777 7777";
    private static final String SEARCH_TEXT = "burger";
    private static final Double RATING_AVG = 4.5D;
    private static final Integer RATING_COUNT = 10;
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);
    private static final String GET_ACTIVE_RESTAURANTS_QUERY = "SELECT * FROM restaurants WHERE status='ACTIVE'";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM restaurants WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM restaurants WHERE name = ? AND (status='ACTIVE' OR status='INACTIVE')";
    private static final String FIND_BY_SIMILAR_NAME_QUERY = "SELECT * FROM restaurants WHERE name ILIKE CONCAT('%', ?, '%') AND status='ACTIVE'";
    private static final String FIND_BY_MANAGER_ID_QUERY = "SELECT * FROM restaurants WHERE manager_id = ?";
    private static final String FIND_PENDING_QUERY = "SELECT * FROM restaurants WHERE status='PENDING'";
    private static final String SAVE_QUERY = "INSERT INTO restaurants (name, description, address, phone, opening_time, closing_time, manager_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE restaurants SET name=?, description=?, address=?, phone=?, rating_avg=?, rating_count=?, opening_time=?, closing_time=?, updated_at=? WHERE id=?";
    private static final String DELETE_QUERY = "DELETE FROM restaurants WHERE id=?";
    private static final String ACTIVATE_QUERY = "UPDATE restaurants SET status='ACTIVE' WHERE id = ?";
    private static final String REJECT_QUERY = "UPDATE restaurants SET status='REJECTED' WHERE id = ?";
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final String ADDRESS_COLUMN = "address";
    private static final String PHONE_COLUMN = "phone";
    private static final String RATING_AVG_COLUMN = "rating_avg";
    private static final String RATING_COUNT_COLUMN = "rating_count";
    private static final String OPENING_TIME_COLUMN = "opening_time";
    private static final String CLOSING_TIME_COLUMN = "closing_time";
    private static final String MANAGER_ID_COLUMN = "manager_id";
    private static final String STATUS_COLUMN = "status";
    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String UPDATED_AT_COLUMN = "updated_at";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private RestaurantDaoImpl testingInstance;

    @Test
    void shouldGetRestaurantsAndMapRows() throws Exception {
        // given
        ArgumentCaptor<RowMapper<Restaurant>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(eq(GET_ACTIVE_RESTAURANTS_QUERY), mapperCaptor.capture()))
                .thenReturn(List.of(createRestaurant()));

        // when
        List<Restaurant> result = testingInstance.getRestaurants();

        // then
        assertEquals(1, result.size());
        verify(jdbcTemplate).query(eq(GET_ACTIVE_RESTAURANTS_QUERY), any(RowMapper.class));

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getLong(ID_COLUMN)).thenReturn(RESTAURANT_ID);
        when(resultSet.getString(NAME_COLUMN)).thenReturn(RESTAURANT_NAME);
        when(resultSet.getString(DESCRIPTION_COLUMN)).thenReturn(RESTAURANT_DESCRIPTION);
        when(resultSet.getString(ADDRESS_COLUMN)).thenReturn(RESTAURANT_ADDRESS);
        when(resultSet.getString(PHONE_COLUMN)).thenReturn(PHONE);
        when(resultSet.getDouble(RATING_AVG_COLUMN)).thenReturn(RATING_AVG);
        when(resultSet.getInt(RATING_COUNT_COLUMN)).thenReturn(RATING_COUNT);
        when(resultSet.getObject(OPENING_TIME_COLUMN, LocalTime.class)).thenReturn(OPENING_TIME);
        when(resultSet.getObject(CLOSING_TIME_COLUMN, LocalTime.class)).thenReturn(CLOSING_TIME);
        when(resultSet.getLong(MANAGER_ID_COLUMN)).thenReturn(MANAGER_ID);
        when(resultSet.getString(STATUS_COLUMN)).thenReturn(RestaurantStatus.ACTIVE.name());
        when(resultSet.getObject(CREATED_AT_COLUMN, LocalDateTime.class)).thenReturn(now);
        when(resultSet.getObject(UPDATED_AT_COLUMN, LocalDateTime.class)).thenReturn(now);

        Restaurant mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(RESTAURANT_ID, mapped.getId());
        assertEquals(RESTAURANT_NAME, mapped.getName());
        assertEquals(RestaurantStatus.ACTIVE, mapped.getStatus());
        assertEquals(MANAGER_ID, mapped.getManagerId());
    }

    @Test
    void shouldFindRestaurantMethods() {
        // given
        Restaurant restaurant = createRestaurant();

        when(jdbcTemplate.query(eq(FIND_BY_ID_QUERY), any(RowMapper.class), eq(RESTAURANT_ID)))
                .thenReturn(List.of(restaurant), List.of(restaurant), List.of());
        when(jdbcTemplate.query(eq(FIND_BY_NAME_QUERY),
                any(RowMapper.class), eq(RESTAURANT_NAME))).thenReturn(List.of(restaurant));

        // when
        Restaurant foundById = testingInstance.findById(RESTAURANT_ID);
        boolean existsById = testingInstance.existsById(RESTAURANT_ID);
        boolean missingById = testingInstance.existsById(RESTAURANT_ID);
        Restaurant foundByName = testingInstance.findByName(RESTAURANT_NAME);

        // then
        assertEquals(restaurant, foundById);
        assertTrue(existsById);
        assertFalse(missingById);
        assertEquals(restaurant, foundByName);
        verify(jdbcTemplate, org.mockito.Mockito.times(3))
                .query(eq(FIND_BY_ID_QUERY), any(RowMapper.class), eq(RESTAURANT_ID));
        verify(jdbcTemplate).query(
                eq(FIND_BY_NAME_QUERY),
                any(RowMapper.class),
                eq(RESTAURANT_NAME));
    }

    @Test
    void shouldQueryRestaurantLists() {
        // when
        testingInstance.findBySimilarName(SEARCH_TEXT);
        testingInstance.findByManagerId(MANAGER_ID);
        testingInstance.findPendingRestaurants();

        // then
        verify(jdbcTemplate).query(
                eq(FIND_BY_SIMILAR_NAME_QUERY),
                any(RowMapper.class),
                eq(SEARCH_TEXT));
        verify(jdbcTemplate).query(eq(FIND_BY_MANAGER_ID_QUERY), any(RowMapper.class), eq(MANAGER_ID));
        verify(jdbcTemplate).query(eq(FIND_PENDING_QUERY), any(RowMapper.class));
    }

    @Test
    void shouldSaveUpdateDeleteAndChangeStatus() {
        // given
        Restaurant restaurant = createRestaurant();

        // when
        testingInstance.save(restaurant);
        testingInstance.update(restaurant);
        testingInstance.deleteById(RESTAURANT_ID);
        testingInstance.activateRestaurant(RESTAURANT_ID);
        testingInstance.rejectRestaurant(RESTAURANT_ID);

        // then
        verify(jdbcTemplate).update(
                eq(SAVE_QUERY),
                eq(RESTAURANT_NAME), eq(RESTAURANT_DESCRIPTION), eq(RESTAURANT_ADDRESS), eq(PHONE),
                eq(OPENING_TIME), eq(CLOSING_TIME), eq(MANAGER_ID), eq(RestaurantStatus.ACTIVE.name()));
        verify(jdbcTemplate).update(
                eq(UPDATE_QUERY),
                eq(RESTAURANT_NAME), eq(RESTAURANT_DESCRIPTION), eq(RESTAURANT_ADDRESS), eq(PHONE),
                eq(RATING_AVG), eq(RATING_COUNT), eq(OPENING_TIME), eq(CLOSING_TIME),
                eq(restaurant.getUpdatedAt()), eq(RESTAURANT_ID));
        verify(jdbcTemplate).update(DELETE_QUERY, RESTAURANT_ID);
        verify(jdbcTemplate).update(ACTIVATE_QUERY, RESTAURANT_ID);
        verify(jdbcTemplate).update(REJECT_QUERY, RESTAURANT_ID);
    }

    private Restaurant createRestaurant() {
        LocalDateTime now = LocalDateTime.now();
        return new Restaurant(
                RESTAURANT_ID,
                RESTAURANT_NAME,
                RESTAURANT_DESCRIPTION,
                RESTAURANT_ADDRESS,
                PHONE,
                RATING_AVG,
                RATING_COUNT,
                OPENING_TIME,
                CLOSING_TIME,
                MANAGER_ID,
                RestaurantStatus.ACTIVE,
                now,
                now
        );
    }
}
