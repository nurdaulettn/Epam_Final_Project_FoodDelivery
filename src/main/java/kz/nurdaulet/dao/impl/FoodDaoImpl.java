package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.FoodDao;
import kz.nurdaulet.entity.Food;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FoodDaoImpl implements FoodDao {
    public static final String FIND_ALL = "SELECT * FROM foods WHERE is_available = true";
    public static final String FIND_BY_CATEGORY = "SELECT * FROM foods WHERE category_id = ? AND is_available = true";
    public static final String FIND_BY_RESTAURANT = "SELECT * FROM foods WHERE restaurant_id = ? AND is_available = true";
    public static final String FIND_BY_RESTAURANT_FOR_MANAGER = "SELECT * FROM foods WHERE restaurant_id = ?";
    public static final String FIND_SIMILAR_NAME = "SELECT * FROM foods WHERE name ILIKE CONCAT('%', ?, '%') AND is_available = true";
    public static final String FIND_BY_ID = "SELECT * FROM foods WHERE id = ?";
    public static final String FIND_BY_NAME = "SELECT * FROM foods WHERE name = ?";
    public static final String UPDATE = "UPDATE foods SET name=?, description=?, price=?, is_available=?, category_id=? WHERE id = ?";
    public static final String DISABLE_BY_ID = "UPDATE foods SET is_available=false WHERE id = ?";
    public static final String ENABLE_BY_ID = "UPDATE foods SET is_available=true WHERE id = ?";
    public static final String SAVE = "INSERT INTO foods (name, description, price, is_available, restaurant_id, category_id) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String DELETE = "DELETE FROM foods WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Food> mapper = (rs, rowNum) -> new Food(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getBoolean("is_available"),
            rs.getLong("restaurant_id"),
            rs.getLong("category_id")
    );

    public FoodDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Food> getAllFoods() {
        return jdbcTemplate.query(FIND_ALL, mapper);
    }

    @Override
    public List<Food> getFoodsByCategory(Long categoryId) {
        return jdbcTemplate.query(FIND_BY_CATEGORY, mapper, categoryId);
    }

    @Override
    public List<Food> getFoodsByRestaurant(Long restaurantId) {
        return jdbcTemplate.query(FIND_BY_RESTAURANT, mapper, restaurantId);
    }

    @Override
    public List<Food> getFoodsByRestaurantForManager(Long restaurantId) {
        return jdbcTemplate.query(FIND_BY_RESTAURANT_FOR_MANAGER, mapper, restaurantId);
    }

    @Override
    public List<Food> getFoodsBySimilarName(String name) {
        return jdbcTemplate.query(FIND_SIMILAR_NAME, mapper, name);
    }

    @Override
    public Food getFoodById(Long id) {
        return jdbcTemplate.query(FIND_BY_ID, mapper, id)
                .stream().findFirst().orElse(null);
    }

    @Override
    public Food getFoodByName(String name) {
        return jdbcTemplate.query(FIND_BY_NAME, mapper, name)
                .stream().findFirst().orElse(null);
    }

    @Override
    public void update(Food food) {
        jdbcTemplate.update(UPDATE, food.getName(), food.getDescription(),
                food.getPrice(), food.getAvailable(),
                food.getCategoryId(), food.getId());
    }

    @Override
    public void disableById(Long id) {
        jdbcTemplate.update(DISABLE_BY_ID, id);
    }

    @Override
    public void enableById(Long id) {
        jdbcTemplate.update(ENABLE_BY_ID, id);
    }

    @Override
    public void save(Food food) {
        jdbcTemplate.update(SAVE, food.getName(), food.getDescription(),
                food.getPrice(), food.getAvailable(),
                food.getRestaurantId(), food.getCategoryId());
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(DELETE, id);
    }
}
