package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.FoodDao;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.service.FoodService;

import java.util.List;

public class FoodServiceImpl implements FoodService {
    private final FoodDao foodDao;

    public FoodServiceImpl(FoodDao foodDao) {
        this.foodDao = foodDao;
    }

    @Override
    public List<Food> getAllFood() {
        return foodDao.getAllFoods();
    }

    @Override
    public List<Food> getFoodByCategoryId(Long categoryId) {
        return foodDao.getFoodsByCategory(categoryId);
    }

    @Override
    public List<Food> getFoodBySimilarName(String name) {
        return foodDao.getFoodsBySimilarName(name);
    }

    @Override
    public Food getFoodById(Long id) {
        return foodDao.getFoodById(id);
    }
}
