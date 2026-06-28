package kz.nurdaulet.service;

import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
    /**
     * Returns all restaurants visible in the public restaurant list.
     */
    List<Restaurant> getAllRestaurants();

    /**
     * Finds restaurants whose names match the given search text.
     */
    List<Restaurant> searchRestaurantsByName(String name);

    /**
     * Creates a pending restaurant request for the manager.
     */
    Restaurant create(RestaurantCreateDto dto, Long userId);

    /**
     * Returns restaurants owned by the given manager.
     */
    List<Restaurant> getMyRestaurants(Long userId);

    /**
     * Returns restaurant creation requests waiting for admin approval.
     */
    List<Restaurant> getPendingRestaurants();

    /**
     * Approves a pending restaurant request.
     */
    void confirmRestaurant(Long id);

    /**
     * Rejects a pending restaurant request.
     */
    void rejectRestaurant(Long id);

    /**
     * Returns a restaurant by id or throws an exception when it does not exist.
     */
    Restaurant getRestaurantById(Long id);
}
