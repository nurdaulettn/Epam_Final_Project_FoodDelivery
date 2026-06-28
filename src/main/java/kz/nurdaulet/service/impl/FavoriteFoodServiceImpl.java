package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.FavoriteFoodDao;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.service.FavoriteFoodService;
import kz.nurdaulet.service.FoodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteFoodServiceImpl implements FavoriteFoodService {
    private final FavoriteFoodDao favoriteFoodDao;
    private final FoodService foodService;

    public FavoriteFoodServiceImpl(FavoriteFoodDao favoriteFoodDao, FoodService foodService) {
        this.favoriteFoodDao = favoriteFoodDao;
        this.foodService = foodService;
    }

    @Override
    public List<Food> getFavoriteFoods(Long userId) {
        return getFavoriteFoodIds(userId).stream()
                .map(foodService::getFoodById)
                .toList();
    }

    @Override
    public List<Long> getFavoriteFoodIds(Long userId) {
        return favoriteFoodDao.findFoodIdsByUserId(userId);
    }

    @Override
    public boolean isFavorite(Long userId, Long foodId) {
        return favoriteFoodDao.existsByUserIdAndFoodId(userId, foodId);
    }

    @Override
    public void addFavorite(Long userId, Long foodId) {
        foodService.getFoodById(foodId);

        if (!favoriteFoodDao.existsByUserIdAndFoodId(userId, foodId)) {
            favoriteFoodDao.save(userId, foodId);
        }
    }

    @Override
    public void removeFavorite(Long userId, Long foodId) {
        if (favoriteFoodDao.existsByUserIdAndFoodId(userId, foodId)) {
            favoriteFoodDao.delete(userId, foodId);
        }
    }
}
