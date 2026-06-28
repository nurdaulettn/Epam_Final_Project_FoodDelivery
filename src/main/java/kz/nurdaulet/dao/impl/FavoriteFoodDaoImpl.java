package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.FavoriteFoodDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FavoriteFoodDaoImpl implements FavoriteFoodDao {
    private static final String FIND_FOOD_IDS_BY_USER = "SELECT food_id FROM favorite_foods WHERE user_id = ? ORDER BY id DESC";
    private static final String EXISTS = "SELECT COUNT(*) FROM favorite_foods WHERE user_id = ? AND food_id = ?";
    private static final String SAVE = "INSERT INTO favorite_foods (user_id, food_id) VALUES (?, ?)";
    private static final String DELETE = "DELETE FROM favorite_foods WHERE user_id = ? AND food_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public FavoriteFoodDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> findFoodIdsByUserId(Long userId) {
        return jdbcTemplate.queryForList(FIND_FOOD_IDS_BY_USER, Long.class, userId);
    }

    @Override
    public boolean existsByUserIdAndFoodId(Long userId, Long foodId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS, Integer.class, userId, foodId);

        return count != null && count > 0;
    }

    @Override
    public void save(Long userId, Long foodId) {
        jdbcTemplate.update(SAVE, userId, foodId);
    }

    @Override
    public void delete(Long userId, Long foodId) {
        jdbcTemplate.update(DELETE, userId, foodId);
    }
}
