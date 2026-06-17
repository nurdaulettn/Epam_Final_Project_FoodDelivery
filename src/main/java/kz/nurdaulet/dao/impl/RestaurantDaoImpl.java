package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public class RestaurantDaoImpl implements RestaurantDao {
    private static final String FIND_ALL_ACTIVE =  "SELECT * FROM restaurants WHERE status='ACTIVE'";
    private static final String FIND_BY_ID =  "SELECT * FROM restaurants WHERE id = ?";
    private static final String FIND_BY_SIMILAR_NAME = "SELECT * FROM restaurants WHERE name ILIKE CONCAT('%', ?, '%') AND status='ACTIVE'";
    private static final String FIND_BY_NAME = "SELECT * FROM restaurants WHERE name = ? AND (status='ACTIVE' OR status='INACTIVE')";
    private static final String SAVE = "INSERT INTO restaurants (name, description, address, phone, opening_time, closing_time, manager_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE restaurants SET name=?, description=?, address=?, phone=?, rating_avg=?, rating_count=?, opening_time=?, closing_time=?, updated_at=? WHERE id=?";
    private static final String DELETE = "DELETE FROM restaurants WHERE id=?";
    private static final String FIND_BY_MANAGER_ID = "SELECT * FROM restaurants WHERE manager_id = ?";
    private static final String FIND_PENDING = "SELECT * FROM restaurants WHERE status='PENDING'";
    private static final String CONFIRM_RESTAURANT = "UPDATE restaurants SET status='ACTIVE' WHERE id = ?";
    private static final String REJECT_RESTAURANT = "UPDATE restaurants SET status='REJECTED' WHERE id = ?";
    private static final Logger log = LoggerFactory.getLogger(RestaurantDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Restaurant> restaurantRowMapper = (rs, rowNum) -> {
        return new Restaurant(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getDouble("rating_avg"),
                rs.getInt("rating_count"),
                rs.getObject("opening_time", LocalTime.class),
                rs.getObject("closing_time", LocalTime.class),
                rs.getLong("manager_id"),
                RestaurantStatus.valueOf(rs.getString("status")),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    };

    public RestaurantDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Restaurant> getRestaurants() {
        return jdbcTemplate.query(FIND_ALL_ACTIVE, restaurantRowMapper);
    }

    @Override
    public Restaurant findById(Long id) {
        return jdbcTemplate.query(FIND_BY_ID, restaurantRowMapper, id)
                .stream().findFirst().orElse(null);
    }

    @Override
    public List<Restaurant> findBySimilarName(String name) {
        return jdbcTemplate.query(FIND_BY_SIMILAR_NAME, restaurantRowMapper, name);
    }

    @Override
    public Restaurant findByName(String name) {
        return jdbcTemplate.query(FIND_BY_NAME, restaurantRowMapper, name).stream().findFirst().orElse(null);
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id) != null;
    }

    @Override
    public void save(Restaurant restaurant) {
        jdbcTemplate.update(SAVE, restaurant.getName(), restaurant.getDescription(),
                restaurant.getAddress(), restaurant.getPhone(), restaurant.getOpeningTime(),
                restaurant.getClosingTime(), restaurant.getManagerId(),
                restaurant.getStatus().name()
        );
    }

    @Override
    public void update(Restaurant restaurant) {
        jdbcTemplate.update(UPDATE, restaurant.getName(), restaurant.getDescription(),
                restaurant.getAddress(), restaurant.getPhone(), restaurant.getRatingAvg(),
                restaurant.getRatingCount(), restaurant.getOpeningTime(),
                restaurant.getClosingTime(), restaurant.getUpdatedAt(),
                restaurant.getId()
        );
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE, id);
    }

    @Override
    public List<Restaurant> findByManagerId(Long id) {
        return jdbcTemplate.query(FIND_BY_MANAGER_ID, restaurantRowMapper, id);
    }

    @Override
    public List<Restaurant> findPendingRestaurants() {
        return jdbcTemplate.query(FIND_PENDING, restaurantRowMapper);
    }

    @Override
    public void activateRestaurant(Long id) {
        jdbcTemplate.update(CONFIRM_RESTAURANT, id);
    }

    @Override
    public void rejectRestaurant(Long id) {
        jdbcTemplate.update(REJECT_RESTAURANT, id);
    }

}
