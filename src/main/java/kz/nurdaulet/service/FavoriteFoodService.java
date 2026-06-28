package kz.nurdaulet.service;

import kz.nurdaulet.entity.Food;

import java.util.List;

public interface FavoriteFoodService {
    /**
     * Returns favorite food entities for the given user.
     */
    List<Food> getFavoriteFoods(Long userId);

    /**
     * Returns favorite food ids for the given user.
     */
    List<Long> getFavoriteFoodIds(Long userId);

    /**
     * Checks whether a food item is marked as favorite by the user.
     */
    boolean isFavorite(Long userId, Long foodId);

    /**
     * Adds a food item to the user's favorites.
     */
    void addFavorite(Long userId, Long foodId);

    /**
     * Removes a food item from the user's favorites.
     */
    void removeFavorite(Long userId, Long foodId);
}
