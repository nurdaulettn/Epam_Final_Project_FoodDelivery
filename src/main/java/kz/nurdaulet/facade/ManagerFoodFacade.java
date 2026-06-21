package kz.nurdaulet.facade;

import kz.nurdaulet.dto.FoodCreateDto;

public interface ManagerFoodFacade {
    void createFood(Long managerId, Long restaurantId, FoodCreateDto foodDto);
    void updateFood(Long managerId, Long restaurantId, Long foodId, FoodCreateDto foodDto);
    void deleteFood(Long managerId, Long restaurantId, Long foodId);
}
