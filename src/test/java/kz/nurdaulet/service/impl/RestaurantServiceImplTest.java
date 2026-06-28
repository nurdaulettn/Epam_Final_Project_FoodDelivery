package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {
    private static final Long RESTAURANT_ID = 1L;
    private static final Long MANAGER_ID = 5L;
    private static final String RESTAURANT_NAME = "Burger House";
    private static final String RESTAURANT_NAME_WITH_SPACES = " Burger House ";
    private static final String RESTAURANT_DESCRIPTION = "Fast food";
    private static final String RESTAURANT_ADDRESS = "Abay 1";
    private static final String PHONE = "+7 (777) 777 7777";
    private static final String SEARCH_TEXT = "burger";
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);

    @Mock
    private Restaurant restaurant;

    @Mock
    private RestaurantDao restaurantDao;

    @InjectMocks
    private RestaurantServiceImpl testingInstance;

    @Test
    void shouldGetAllRestaurants() {
        // given
        List<Restaurant> restaurants = List.of(restaurant);
        when(restaurantDao.getRestaurants()).thenReturn(restaurants);

        // when
        List<Restaurant> result = testingInstance.getAllRestaurants();

        // then
        assertEquals(restaurants, result);
        verify(restaurantDao).getRestaurants();
    }

    @Test
    void shouldSearchRestaurantsByName() {
        // given
        List<Restaurant> restaurants = List.of(restaurant);
        when(restaurantDao.findBySimilarName(SEARCH_TEXT)).thenReturn(restaurants);

        // when
        List<Restaurant> result = testingInstance.searchRestaurantsByName(SEARCH_TEXT);

        // then
        assertEquals(restaurants, result);
        verify(restaurantDao).findBySimilarName(SEARCH_TEXT);
    }

    @Test
    void shouldCreate() {
        // given
        RestaurantCreateDto dto = createRestaurantDto();
        ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);

        // when
        Restaurant result = testingInstance.create(dto, MANAGER_ID);

        // then
        verify(restaurantDao).save(captor.capture());
        Restaurant saved = captor.getValue();
        assertEquals(RESTAURANT_NAME, saved.getName());
        assertEquals(RESTAURANT_DESCRIPTION, saved.getDescription());
        assertEquals(RESTAURANT_ADDRESS, saved.getAddress());
        assertEquals(PHONE, saved.getPhone());
        assertEquals(OPENING_TIME, saved.getOpeningTime());
        assertEquals(CLOSING_TIME, saved.getClosingTime());
        assertEquals(MANAGER_ID, saved.getManagerId());
        assertEquals(RestaurantStatus.PENDING, saved.getStatus());
        assertEquals(saved, result);
    }

    @Test
    void shouldGetMyRestaurants() {
        // given
        List<Restaurant> restaurants = List.of(restaurant);
        when(restaurantDao.findByManagerId(MANAGER_ID)).thenReturn(restaurants);

        // when
        List<Restaurant> result = testingInstance.getMyRestaurants(MANAGER_ID);

        // then
        assertEquals(restaurants, result);
        verify(restaurantDao).findByManagerId(MANAGER_ID);
    }

    @Test
    void shouldGetPendingRestaurants() {
        // given
        List<Restaurant> restaurants = List.of(restaurant);
        when(restaurantDao.findPendingRestaurants()).thenReturn(restaurants);

        // when
        List<Restaurant> result = testingInstance.getPendingRestaurants();

        // then
        assertEquals(restaurants, result);
        verify(restaurantDao).findPendingRestaurants();
    }

    @Test
    void shouldConfirmRestaurant() {
        // given
        when(restaurantDao.existsById(RESTAURANT_ID)).thenReturn(true);

        // when
        testingInstance.confirmRestaurant(RESTAURANT_ID);

        // then
        verify(restaurantDao).existsById(RESTAURANT_ID);
        verify(restaurantDao).activateRestaurant(RESTAURANT_ID);
    }

    @Test
    void shouldRejectRestaurant() {
        // given
        when(restaurantDao.existsById(RESTAURANT_ID)).thenReturn(true);

        // when
        testingInstance.rejectRestaurant(RESTAURANT_ID);

        // then
        verify(restaurantDao).existsById(RESTAURANT_ID);
        verify(restaurantDao).rejectRestaurant(RESTAURANT_ID);
    }

    @Test
    void shouldGetRestaurantById() {
        // given
        when(restaurantDao.findById(RESTAURANT_ID)).thenReturn(restaurant);

        // when
        final Restaurant result = testingInstance.getRestaurantById(RESTAURANT_ID);

        // then
        verify(restaurantDao).findById(RESTAURANT_ID);
        assertEquals(restaurant, result);
    }

    @Test
    void shouldNotGetRestaurantByIdWhenRestaurantNotExist() {
        // given
        when(restaurantDao.findById(RESTAURANT_ID)).thenReturn(null);

        // when
        final Executable executable = () -> testingInstance.getRestaurantById(RESTAURANT_ID);

        // then
        assertThrows(RestaurantNotFoundException.class, executable);
        verify(restaurantDao).findById(RESTAURANT_ID);
    }

    private RestaurantCreateDto createRestaurantDto() {
        RestaurantCreateDto dto = new RestaurantCreateDto();
        dto.setName(RESTAURANT_NAME_WITH_SPACES);
        dto.setDescription(RESTAURANT_DESCRIPTION);
        dto.setAddress(RESTAURANT_ADDRESS);
        dto.setPhone(PHONE);
        dto.setOpeningTime(OPENING_TIME);
        dto.setClosingTime(CLOSING_TIME);

        return dto;
    }
}
