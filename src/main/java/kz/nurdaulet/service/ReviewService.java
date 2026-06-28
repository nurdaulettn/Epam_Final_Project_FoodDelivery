package kz.nurdaulet.service;

import kz.nurdaulet.dto.ReviewCreateDto;
import kz.nurdaulet.entity.Review;

import java.util.List;

public interface ReviewService {
    List<Review> getRestaurantReviews(Long restaurantId);

    Review getCustomerReview(Long userId, Long restaurantId);

    void createOrUpdateReview(Long userId, Long restaurantId, ReviewCreateDto dto);
}
