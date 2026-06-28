package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dao.ReviewDao;
import kz.nurdaulet.dto.ReviewCreateDto;
import kz.nurdaulet.entity.Review;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import kz.nurdaulet.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private static final String RESTAURANT_NOT_FOUND = "Restaurant with id %d not found";
    private static final String LOG_REVIEW_RESTAURANT_NOT_FOUND =
            "Review save rejected because restaurant {} was not found";
    private static final String LOG_REVIEW_CREATED = "User {} created review for restaurant {} with rating {}";
    private static final String LOG_REVIEW_UPDATED = "User {} updated review for restaurant {} with rating {}";
    private static final String LOG_RESTAURANT_RATING_REFRESHED =
            "Restaurant {} rating refreshed: average={}, count={}";

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
            log.warn(LOG_REVIEW_RESTAURANT_NOT_FOUND, restaurantId);
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
            log.info(LOG_REVIEW_CREATED, userId, restaurantId, dto.getRating());
        } else {
            reviewDao.update(review);
            log.info(LOG_REVIEW_UPDATED, userId, restaurantId, dto.getRating());
        }

        refreshRestaurantRating(restaurantId);
    }

    private void refreshRestaurantRating(Long restaurantId) {
        Double ratingAvg = reviewDao.getAverageRating(restaurantId);
        Integer ratingCount = reviewDao.getReviewCount(restaurantId);

        restaurantDao.updateRating(restaurantId, ratingAvg, ratingCount);
        log.debug(LOG_RESTAURANT_RATING_REFRESHED, restaurantId, ratingAvg, ratingCount);
    }

    private String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }

        return comment.trim();
    }
}
