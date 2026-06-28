package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.ReviewDao;
import kz.nurdaulet.entity.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReviewDaoImpl implements ReviewDao {
    private static final String FIND_BY_RESTAURANT_ID = "SELECT * FROM reviews WHERE restaurant_id = ? ORDER BY created_at DESC, id DESC";
    private static final String FIND_BY_USER_AND_RESTAURANT = "SELECT * FROM reviews WHERE user_id = ? AND restaurant_id = ?";
    private static final String SAVE = "INSERT INTO reviews (user_id, restaurant_id, rating, comment, created_at) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE reviews SET rating = ?, comment = ?, created_at = ? WHERE user_id = ? AND restaurant_id = ?";
    private static final String AVG_RATING = "SELECT COALESCE(AVG(rating), 0) FROM reviews WHERE restaurant_id = ?";
    private static final String REVIEW_COUNT = "SELECT COUNT(*) FROM reviews WHERE restaurant_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Review> rowMapper = (rs, rowNum) -> new Review(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("restaurant_id"),
            rs.getInt("rating"),
            rs.getString("comment"),
            rs.getObject("created_at", LocalDateTime.class)
    );

    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> findByRestaurantId(Long restaurantId) {
        return jdbcTemplate.query(FIND_BY_RESTAURANT_ID, rowMapper, restaurantId);
    }

    @Override
    public Review findByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        return jdbcTemplate.query(FIND_BY_USER_AND_RESTAURANT, rowMapper, userId, restaurantId)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(Review review) {
        jdbcTemplate.update(
                SAVE,
                review.getUserId(),
                review.getRestaurantId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    @Override
    public void update(Review review) {
        jdbcTemplate.update(
                UPDATE,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUserId(),
                review.getRestaurantId()
        );
    }

    @Override
    public Double getAverageRating(Long restaurantId) {
        return jdbcTemplate.queryForObject(AVG_RATING, Double.class, restaurantId);
    }

    @Override
    public Integer getReviewCount(Long restaurantId) {
        return jdbcTemplate.queryForObject(REVIEW_COUNT, Integer.class, restaurantId);
    }
}
