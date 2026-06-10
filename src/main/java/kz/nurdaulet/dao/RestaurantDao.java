package kz.nurdaulet.dao;

import kz.nurdaulet.entity.Restaurant;

import java.util.List;

public interface RestaurantDao {
    List<Restaurant> getRestaurants();
    Restaurant findById(Long id);
    List<Restaurant> findByName(String name);
    boolean existsById(Long id);
    void save(Restaurant restaurant);
    void update(Restaurant restaurant);
    void deleteById(Long id);
}
