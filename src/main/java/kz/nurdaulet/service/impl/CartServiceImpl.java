package kz.nurdaulet.service.impl;

import kz.nurdaulet.dto.CartItemDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.CartService;
import kz.nurdaulet.service.FoodService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    private static final String FOOD_IS_NOT_AVAILABLE = "Food is not available";
    private static final String FOODS_FROM_DIFFERENT_RESTAURANTS =
            "Cart can contain foods from only one restaurant";
    private static final String INVALID_QUANTITY = "Quantity must be greater than zero";

    private final FoodService foodService;

    public CartServiceImpl(FoodService foodService) {
        this.foodService = foodService;
    }

    @Override
    public void addFood(Map<Long, Integer> cart, Long foodId) {
        Food food = foodService.getFoodById(foodId);

        validateFoodAvailable(food);
        validateSameRestaurant(cart, food);

        cart.merge(foodId, 1, Integer::sum);
    }

    @Override
    public void removeFood(Map<Long, Integer> cart, Long foodId) {
        cart.remove(foodId);
    }

    @Override
    public void updateQuantity(Map<Long, Integer> cart, Long foodId, Integer quantity) {
        validateQuantity(quantity);

        Food food = foodService.getFoodById(foodId);
        validateFoodAvailable(food);
        validateSameRestaurant(cart, food);

        cart.put(foodId, quantity);
    }

    @Override
    public List<CartItemDto> getCartItems(Map<Long, Integer> cart) {
        List<CartItemDto> cartItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Food food = foodService.getFoodById(entry.getKey());
            Integer quantity = entry.getValue();
            Double subtotal = food.getPrice() * quantity;

            cartItems.add(new CartItemDto(
                    food.getId(),
                    food.getName(),
                    food.getPrice(),
                    quantity,
                    subtotal,
                    food.getRestaurantId()
            ));
        }

        return cartItems;
    }

    @Override
    public Double calculateTotal(Map<Long, Integer> cart) {
        return getCartItems(cart).stream()
                .mapToDouble(CartItemDto::getSubtotal)
                .sum();
    }

    @Override
    public void clear(Map<Long, Integer> cart) {
        cart.clear();
    }

    private void validateSameRestaurant(Map<Long, Integer> cart, Food newFood) {
        if (cart.isEmpty() || cart.containsKey(newFood.getId())) {
            return;
        }

        Long existingFoodId = cart.keySet().iterator().next();
        Food existingFood = foodService.getFoodById(existingFoodId);

        if (!existingFood.getRestaurantId().equals(newFood.getRestaurantId())) {
            throw new CartOperationException(FOODS_FROM_DIFFERENT_RESTAURANTS);
        }
    }

    private void validateFoodAvailable(Food food) {
        if (!Boolean.TRUE.equals(food.getAvailable())) {
            throw new CartOperationException(FOOD_IS_NOT_AVAILABLE);
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new CartOperationException(INVALID_QUANTITY);
        }
    }
}
