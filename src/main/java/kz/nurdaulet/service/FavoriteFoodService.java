package kz.nurdaulet.service;

import kz.nurdaulet.entity.Food;

import java.util.List;

public interface FavoriteFoodService {
    List<Food> getFavoriteFoods(Long userId);

    List<Long> getFavoriteFoodIds(Long userId);

    boolean isFavorite(Long userId, Long foodId);

    void addFavorite(Long userId, Long foodId);

    void removeFavorite(Long userId, Long foodId);
}
