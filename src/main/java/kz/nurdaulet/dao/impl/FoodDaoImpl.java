package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.FoodDao;
import kz.nurdaulet.entity.Food;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public class FoodDaoImpl implements FoodDao {
    private static final String FIND_ALL = "SELECT * FROM foods";
    private static final String FIND_BY_CATEGORY = "SELECT * FROM foods WHERE category_id = ?";
    private static final String FIND_BY_RESTAURANT = "SELECT * FROM foods WHERE restaurant_id = ?";
    private static final String FIND_SIMILAR_NAME = "SELECT * FROM foods WHERE name ILIKE CONCAT('%', ?, '%') AND is_available = true";
    private static final String FIND_BY_ID = "SELECT * FROM foods WHERE id = ?";
    private static final String FIND_BY_NAME = "SELECT * FROM foods WHERE name = ?";
    private static final String SAVE = "INSERT INTO foods (name, description, price, is_available, restaurant_id, category_id) VALUES (?, ?, ?, ?, ?, ?)";

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
    public void save(Food food) {
        jdbcTemplate.update(SAVE, food.getName(), food.getDescription(),
                food.getPrice(), food.getAvailable(),
                food.getRestaurantId(), food.getCategoryId());
    }


}
