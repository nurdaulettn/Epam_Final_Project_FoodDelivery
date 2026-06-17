package kz.nurdaulet.service;

import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
    Restaurant create(RestaurantCreateDto dto, Long userId);
    List<Restaurant> getMyRestaurants(Long userId);
    List<Restaurant> getAllNotConfirmedRestaurants();
    void confirmRestaurant(Long id);
    void deleteRestaurant(Long id);
}
