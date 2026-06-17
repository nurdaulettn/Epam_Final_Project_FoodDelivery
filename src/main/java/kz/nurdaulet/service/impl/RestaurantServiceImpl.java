package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.exception.RestaurantNotFoundException;
import kz.nurdaulet.service.RestaurantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private static final String RESTAURANT_NOT_FOUND = "Restaurant with id %d not found";
    private final RestaurantDao restaurantDao;

    public RestaurantServiceImpl(RestaurantDao restaurantDao) {
        this.restaurantDao = restaurantDao;
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
        restaurant.setConfirmed(false);
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
    public List<Restaurant> getAllNotConfirmedRestaurants() {
        return restaurantDao.findNotConfirmedRestaurants();
    }

    @Override
    public void confirmRestaurant(Long id) {
        if (restaurantDao.existsById(id)) {
            restaurantDao.confirmRestaurantById(id);
        } else {
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(id));
        }
    }

    @Override
    public void deleteRestaurant(Long id) {
        if (restaurantDao.existsById(id)) {
            restaurantDao.deleteById(id);
        } else {
            throw new RestaurantNotFoundException(RESTAURANT_NOT_FOUND.formatted(id));
        }
    }
}
