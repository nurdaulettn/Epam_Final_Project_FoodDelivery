package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dao.ReviewDao;
import kz.nurdaulet.dto.ReviewCreateDto;
import kz.nurdaulet.entity.Review;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
    private static final Long USER_ID = 1L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Integer RATING = 5;
    private static final String COMMENT = "Great food";
    private static final String COMMENT_WITH_SPACES = " Great food ";
    private static final Double RATING_AVG = 4.5D;
    private static final Integer RATING_COUNT = 2;

    @Mock
    private ReviewDao reviewDao;

    @Mock
    private RestaurantDao restaurantDao;

    @InjectMocks
    private ReviewServiceImpl testingInstance;

    @Test
    void shouldGetRestaurantReviews() {
        // given
        List<Review> reviews = List.of(createReview());
        when(reviewDao.findByRestaurantId(RESTAURANT_ID)).thenReturn(reviews);

        // when
        List<Review> result = testingInstance.getRestaurantReviews(RESTAURANT_ID);

        // then
        assertEquals(reviews, result);
        verify(reviewDao).findByRestaurantId(RESTAURANT_ID);
    }

    @Test
    void shouldCreateReviewAndRefreshRestaurantRating() {
        // given
        ReviewCreateDto dto = new ReviewCreateDto(RATING, COMMENT_WITH_SPACES);
        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        when(restaurantDao.existsById(RESTAURANT_ID)).thenReturn(true);
        when(reviewDao.findByUserIdAndRestaurantId(USER_ID, RESTAURANT_ID)).thenReturn(null);
        when(reviewDao.getAverageRating(RESTAURANT_ID)).thenReturn(RATING_AVG);
        when(reviewDao.getReviewCount(RESTAURANT_ID)).thenReturn(RATING_COUNT);

        // when
        testingInstance.createOrUpdateReview(USER_ID, RESTAURANT_ID, dto);

        // then
        verify(reviewDao).save(captor.capture());
        assertEquals(USER_ID, captor.getValue().getUserId());
        assertEquals(RESTAURANT_ID, captor.getValue().getRestaurantId());
        assertEquals(RATING, captor.getValue().getRating());
        assertEquals(COMMENT, captor.getValue().getComment());
        verify(restaurantDao).updateRating(RESTAURANT_ID, RATING_AVG, RATING_COUNT);
    }

    @Test
    void shouldUpdateExistingReview() {
        // given
        ReviewCreateDto dto = new ReviewCreateDto(RATING, COMMENT);
        when(restaurantDao.existsById(RESTAURANT_ID)).thenReturn(true);
        when(reviewDao.findByUserIdAndRestaurantId(USER_ID, RESTAURANT_ID)).thenReturn(createReview());
        when(reviewDao.getAverageRating(RESTAURANT_ID)).thenReturn(RATING_AVG);
        when(reviewDao.getReviewCount(RESTAURANT_ID)).thenReturn(RATING_COUNT);

        // when
        testingInstance.createOrUpdateReview(USER_ID, RESTAURANT_ID, dto);

        // then
        verify(reviewDao).update(org.mockito.ArgumentMatchers.any(Review.class));
        verify(restaurantDao).updateRating(RESTAURANT_ID, RATING_AVG, RATING_COUNT);
    }

    @Test
    void shouldThrowWhenRestaurantDoesNotExist() {
        // given
        when(restaurantDao.existsById(RESTAURANT_ID)).thenReturn(false);

        // when / then
        assertThrows(RestaurantNotFoundException.class,
                () -> testingInstance.createOrUpdateReview(USER_ID, RESTAURANT_ID, new ReviewCreateDto(RATING, COMMENT)));
        verify(restaurantDao).existsById(RESTAURANT_ID);
    }

    private Review createReview() {
        return new Review(1L, USER_ID, RESTAURANT_ID, RATING, COMMENT, java.time.LocalDateTime.now());
    }
}
