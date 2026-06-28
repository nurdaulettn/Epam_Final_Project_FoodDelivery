package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.FavoriteFoodDao;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.service.FavoriteFoodService;
import kz.nurdaulet.service.FoodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteFoodServiceImpl implements FavoriteFoodService {
    private static final Logger log = LoggerFactory.getLogger(FavoriteFoodServiceImpl.class);
    private static final String LOG_FAVORITE_ADDED = "User {} added food {} to favorites";
    private static final String LOG_FAVORITE_ADD_IGNORED = "Favorite add ignored because user {} already has food {}";
    private static final String LOG_FAVORITE_REMOVED = "User {} removed food {} from favorites";
    private static final String LOG_FAVORITE_REMOVE_IGNORED =
            "Favorite remove ignored because user {} does not have food {}";
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
            log.info(LOG_FAVORITE_ADDED, userId, foodId);
        } else {
            log.debug(LOG_FAVORITE_ADD_IGNORED, userId, foodId);
        }
    }

    @Override
    public void removeFavorite(Long userId, Long foodId) {
        if (favoriteFoodDao.existsByUserIdAndFoodId(userId, foodId)) {
            favoriteFoodDao.delete(userId, foodId);
            log.info(LOG_FAVORITE_REMOVED, userId, foodId);
        } else {
            log.debug(LOG_FAVORITE_REMOVE_IGNORED, userId, foodId);
        }
    }
}
