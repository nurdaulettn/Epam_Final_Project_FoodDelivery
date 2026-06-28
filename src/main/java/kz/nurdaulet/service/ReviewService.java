package kz.nurdaulet.service;

import kz.nurdaulet.dto.ReviewCreateDto;
import kz.nurdaulet.entity.Review;

import java.util.List;

public interface ReviewService {
    /**
     * Returns all reviews for the given restaurant.
     */
    List<Review> getRestaurantReviews(Long restaurantId);

    /**
     * Returns the user's review for the given restaurant, or null when it does not exist.
     */
    Review getCustomerReview(Long userId, Long restaurantId);

    /**
     * Creates a new review or updates the existing review for the same user and restaurant.
     */
    void createOrUpdateReview(Long userId, Long restaurantId, ReviewCreateDto dto);
}
