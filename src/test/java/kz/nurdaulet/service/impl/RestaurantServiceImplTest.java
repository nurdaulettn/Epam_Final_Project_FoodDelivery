package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {
    private static final Long RESTAURANT_ID = 1L;

    @Mock
    private Restaurant restaurant;

    @Mock
    private RestaurantDao restaurantDao;

    @InjectMocks
    private RestaurantServiceImpl testingInstance;

    @Test
    void shouldGetAllRestaurants() {
    }

    @Test
    void shouldSearchRestaurantsByName() {
    }

    @Test
    void shouldCreate() {
    }

    @Test
    void shouldGetMyRestaurants() {
    }

    @Test
    void shouldGetPendingRestaurants() {
    }

    @Test
    void shouldConfirmRestaurant() {
    }

    @Test
    void shouldRejectRestaurant() {
    }

    @Test
    void shouldGetRestaurantById() {
        when(restaurantDao.findById(RESTAURANT_ID)).thenReturn(restaurant);

        final Restaurant result = testingInstance.getRestaurantById(RESTAURANT_ID);

        verify(restaurantDao).findById(RESTAURANT_ID);
        assertEquals(restaurant, result);
    }

    @Test
    void shouldNotGetRestaurantByIdWhenRestaurantNotExist() {
        when(restaurantDao.findById(RESTAURANT_ID)).thenReturn(null);

        final Executable executable = () -> testingInstance.getRestaurantById(RESTAURANT_ID);

        assertThrows(RestaurantNotFoundException.class, executable);
    }
}