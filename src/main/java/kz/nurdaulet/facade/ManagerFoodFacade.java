package kz.nurdaulet.facade;

import kz.nurdaulet.dto.FoodCreateDto;

public interface ManagerFoodFacade {
    void createFood(Long managerId, Long restaurantId, FoodCreateDto foodDto);
}
