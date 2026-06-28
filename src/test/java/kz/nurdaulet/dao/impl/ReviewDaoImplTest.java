package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Review;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewDaoImplTest {
    private static final Long REVIEW_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long RESTAURANT_ID = 3L;
    private static final Integer RATING = 5;
    private static final String COMMENT = "Great";
    private static final Double RATING_AVG = 4.5D;
    private static final Integer RATING_COUNT = 2;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ReviewDaoImpl testingInstance;

    @Test
    void shouldFindReviewsAndMapRows() throws Exception {
        // given
        ArgumentCaptor<RowMapper<Review>> captor = ArgumentCaptor.forClass(RowMapper.class);
        when(jdbcTemplate.query(any(String.class), captor.capture(), eq(RESTAURANT_ID)))
                .thenReturn(List.of(createReview()));

        // when
        List<Review> result = testingInstance.findByRestaurantId(RESTAURANT_ID);

        // then
        assertEquals(1, result.size());
        verify(jdbcTemplate).query(any(String.class), any(RowMapper.class), eq(RESTAURANT_ID));

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getLong("id")).thenReturn(REVIEW_ID);
        when(resultSet.getLong("user_id")).thenReturn(USER_ID);
        when(resultSet.getLong("restaurant_id")).thenReturn(RESTAURANT_ID);
        when(resultSet.getInt("rating")).thenReturn(RATING);
        when(resultSet.getString("comment")).thenReturn(COMMENT);
        when(resultSet.getObject("created_at", LocalDateTime.class)).thenReturn(now);

        Review mapped = captor.getValue().mapRow(resultSet, 0);

        assertEquals(REVIEW_ID, mapped.getId());
        assertEquals(USER_ID, mapped.getUserId());
        assertEquals(RESTAURANT_ID, mapped.getRestaurantId());
        assertEquals(RATING, mapped.getRating());
        assertEquals(COMMENT, mapped.getComment());
    }

    @Test
    void shouldSaveUpdateAndCalculateRating() {
        // given
        Review review = createReview();
        when(jdbcTemplate.queryForObject(any(String.class), eq(Double.class), eq(RESTAURANT_ID))).thenReturn(RATING_AVG);
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), eq(RESTAURANT_ID))).thenReturn(RATING_COUNT);

        // when
        testingInstance.save(review);
        testingInstance.update(review);
        Double average = testingInstance.getAverageRating(RESTAURANT_ID);
        Integer count = testingInstance.getReviewCount(RESTAURANT_ID);

        // then
        assertEquals(RATING_AVG, average);
        assertEquals(RATING_COUNT, count);
        verify(jdbcTemplate).update(any(String.class), eq(USER_ID), eq(RESTAURANT_ID), eq(RATING), eq(COMMENT), any(LocalDateTime.class));
        verify(jdbcTemplate).update(any(String.class), eq(RATING), eq(COMMENT), any(LocalDateTime.class), eq(USER_ID), eq(RESTAURANT_ID));
    }

    private Review createReview() {
        return new Review(REVIEW_ID, USER_ID, RESTAURANT_ID, RATING, COMMENT, LocalDateTime.now());
    }
}
