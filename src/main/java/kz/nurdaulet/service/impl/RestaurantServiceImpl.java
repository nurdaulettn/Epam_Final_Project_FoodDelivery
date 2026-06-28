package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import kz.nurdaulet.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private static final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);
    private static final String RESTAURANT_NOT_FOUND = "Restaurant with id %d not found";
    private static final String LOG_RESTAURANT_REQUEST_SUBMITTED =
            "Restaurant creation request submitted: name={}, managerId={}";
    private static final String LOG_RESTAURANT_CONFIRMED = "Restaurant {} confirmed by admin";
    private static final String LOG_RESTAURANT_CONFIRMATION_NOT_FOUND =
            "Restaurant {} confirmation failed because restaurant was not found";
    private static final String LOG_RESTAURANT_REJECTED = "Restaurant {} rejected by admin";
    private static final String LOG_RESTAURANT_REJECTION_NOT_FOUND =
            "Restaurant {} rejection failed because restaurant was not found";
    private static final String LOG_RESTAURANT_NOT_FOUND = "Restaurant {} was not found";
    private final RestaurantDao restaurantDao;

    public RestaurantServiceImpl(RestaurantDao restaurantDao) {
        this.restaurantDao = restaurantDao;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantDao.getRestaurants();
    }

    public List<Restaurant> searchRestaurantsByName(String name){
        return restaurantDao.findBySimilarName(name);
    }

    @Override
    public Restaurant create(RestaurantCreateDto dto, Long userId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName().trim());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());
        restaurant.setPhone(dto.getPhone());
        restaurant.setRatingAvg(0.0D);
        restaurant.setRatingCount(0);
        restaurant.setOpeningTime(dto.getOpeningTime());
        restaurant.setClosingTime(dto.getClosingTime());
        restaurant.setManagerId(userId);
        restaurant.setStatus(RestaurantStatus.PENDING);
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());

        restaurantDao.save(restaurant);
        log.info(LOG_RESTAURANT_REQUEST_SUBMITTED, restaurant.getName(), userId);

        return restaurant;
    }

    @Override
    public List<Restaurant> getMyRestaurants(Long userId) {
        return restaurantDao.findByManagerId(userId);
    }

    @Override
    public List<Restaurant> getPendingRestaurants() {
        return restaurantDao.findPendingRestaurants();
    }

    @Override
    public void confirmRestaurant(Long id) {
        if (restaurantDao.existsById(id)) {
            restaurantDao.activateRestaurant(id);
            log.info(LOG_RESTAURANT_CONFIRMED, id);
        } else {
            log.warn(LOG_RESTAURANT_CONFIRMATION_NOT_FOUND, id);
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(id));
        }
    }

    @Override
    public void rejectRestaurant(Long id) {
        if (restaurantDao.existsById(id)) {
            restaurantDao.rejectRestaurant(id);
            log.info(LOG_RESTAURANT_REJECTED, id);
        } else {
            log.warn(LOG_RESTAURANT_REJECTION_NOT_FOUND, id);
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(id));
        }
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        Restaurant restaurant = restaurantDao.findById(id);

        if (restaurant == null) {
            log.warn(LOG_RESTAURANT_NOT_FOUND, id);
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(id));
        }

        return restaurant;
    }
}
