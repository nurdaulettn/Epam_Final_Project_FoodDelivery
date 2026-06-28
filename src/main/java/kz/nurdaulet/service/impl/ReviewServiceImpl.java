package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dao.ReviewDao;
import kz.nurdaulet.dto.ReviewCreateDto;
import kz.nurdaulet.entity.Review;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import kz.nurdaulet.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final String RESTAURANT_NOT_FOUND = "Restaurant with id %d not found";

    private final ReviewDao reviewDao;
    private final RestaurantDao restaurantDao;

    public ReviewServiceImpl(ReviewDao reviewDao, RestaurantDao restaurantDao) {
        this.reviewDao = reviewDao;
        this.restaurantDao = restaurantDao;
    }

    @Override
    public List<Review> getRestaurantReviews(Long restaurantId) {
        return reviewDao.findByRestaurantId(restaurantId);
    }

    @Override
    public Review getCustomerReview(Long userId, Long restaurantId) {
        return reviewDao.findByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    @Transactional
    public void createOrUpdateReview(Long userId, Long restaurantId, ReviewCreateDto dto) {
        if (!restaurantDao.existsById(restaurantId)) {
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(restaurantId));
        }

        Review review = new Review(
                null,
                userId,
                restaurantId,
                dto.getRating(),
                normalizeComment(dto.getComment()),
                LocalDateTime.now()
        );

        if (reviewDao.findByUserIdAndRestaurantId(userId, restaurantId) == null) {
            reviewDao.save(review);
        } else {
            reviewDao.update(review);
        }

        refreshRestaurantRating(restaurantId);
    }

    private void refreshRestaurantRating(Long restaurantId) {
        Double ratingAvg = reviewDao.getAverageRating(restaurantId);
        Integer ratingCount = reviewDao.getReviewCount(restaurantId);

        restaurantDao.updateRating(restaurantId, ratingAvg, ratingCount);
    }

    private String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }

        return comment.trim();
    }
}
