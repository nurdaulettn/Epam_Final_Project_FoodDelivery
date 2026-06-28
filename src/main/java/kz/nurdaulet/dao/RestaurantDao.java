package kz.nurdaulet.dao;

import kz.nurdaulet.entity.Restaurant;

import java.util.List;

public interface RestaurantDao {
    List<Restaurant> getRestaurants();

    Restaurant findById(Long id);

    List<Restaurant> findBySimilarName(String name);

    Restaurant findByName(String name);

    boolean existsById(Long id);

    void save(Restaurant restaurant);

    void update(Restaurant restaurant);

    void deleteById(Long id);

    List<Restaurant> findByManagerId(Long id);

    List<Restaurant> findPendingRestaurants();

    void activateRestaurant(Long id);

    void rejectRestaurant(Long id);

    void updateRating(Long id, Double ratingAvg, Integer ratingCount);
}
