package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.RestaurantStatus;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import kz.nurdaulet.service.RestaurantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private static final String RESTAURANT_NOT_FOUND = "Restaurant with id %d not found";
    private final RestaurantDao restaurantDao;
    private final RestaurantService restaurantService;

    public RestaurantServiceImpl(RestaurantDao restaurantDao, RestaurantService restaurantService) {
        this.restaurantDao = restaurantDao;
        this.restaurantService = restaurantService;
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
        restaurant.setRatingAvg(0D);
        restaurant.setRatingCount(0);
        restaurant.setOpeningTime(dto.getOpeningTime());
        restaurant.setClosingTime(dto.getClosingTime());
        restaurant.setManagerId(userId);
        restaurant.setStatus(RestaurantStatus.PENDING);
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());

        restaurantDao.save(restaurant);

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
        } else {
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(id));
        }
    }

    @Override
    public void rejectRestaurant(Long id) {
        if (restaurantDao.existsById(id)) {
            restaurantDao.rejectRestaurant(id);
        } else {
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(id));
        }
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);

        if (restaurant == null) {
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(id));
        }

        return restaurant;
    }
}
