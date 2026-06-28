package kz.nurdaulet.dao;

import kz.nurdaulet.entity.Review;

import java.util.List;

public interface ReviewDao {
    List<Review> findByRestaurantId(Long restaurantId);

    Review findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    void save(Review review);

    void update(Review review);

    Double getAverageRating(Long restaurantId);

    Integer getReviewCount(Long restaurantId);
}
