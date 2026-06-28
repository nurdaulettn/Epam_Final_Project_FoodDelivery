package kz.nurdaulet.dao.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteFoodDaoImplTest {
    private static final Long USER_ID = 1L;
    private static final Long FOOD_ID = 10L;
    private static final Long SECOND_FOOD_ID = 11L;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private FavoriteFoodDaoImpl testingInstance;

    @Test
    void shouldFindFavoriteFoodIds() {
        // given
        List<Long> foodIds = List.of(FOOD_ID, SECOND_FOOD_ID);
        when(jdbcTemplate.queryForList(any(String.class), eq(Long.class), eq(USER_ID))).thenReturn(foodIds);

        // when
        List<Long> result = testingInstance.findFoodIdsByUserId(USER_ID);

        // then
        assertEquals(foodIds, result);
        verify(jdbcTemplate).queryForList(any(String.class), eq(Long.class), eq(USER_ID));
    }

    @Test
    void shouldCheckExistsAndSaveAndDelete() {
        // given
        when(jdbcTemplate.queryForObject(any(String.class), eq(Integer.class), eq(USER_ID), eq(FOOD_ID))).thenReturn(1);

        // when
        boolean result = testingInstance.existsByUserIdAndFoodId(USER_ID, FOOD_ID);
        testingInstance.save(USER_ID, FOOD_ID);
        testingInstance.delete(USER_ID, FOOD_ID);

        // then
        assertTrue(result);
        verify(jdbcTemplate).queryForObject(any(String.class), eq(Integer.class), eq(USER_ID), eq(FOOD_ID));
        verify(jdbcTemplate, times(2)).update(any(String.class), eq(USER_ID), eq(FOOD_ID));
    }
}
