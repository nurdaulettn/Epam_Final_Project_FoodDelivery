package kz.nurdaulet.service;

import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
    List<Restaurant> getAllRestaurants();

    List<Restaurant> searchRestaurantsByName(String name);

    Restaurant create(RestaurantCreateDto dto, Long userId);

    List<Restaurant> getMyRestaurants(Long userId);

    List<Restaurant> getPendingRestaurants();

    void confirmRestaurant(Long id);

    void rejectRestaurant(Long id);

    Restaurant getRestaurantById(Long id);
}
