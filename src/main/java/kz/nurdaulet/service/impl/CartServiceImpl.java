package kz.nurdaulet.service.impl;

import kz.nurdaulet.dto.CartItemDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.CartService;
import kz.nurdaulet.service.FoodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);
    private static final String FOOD_IS_NOT_AVAILABLE = "Food is not available";
    private static final String FOODS_FROM_DIFFERENT_RESTAURANTS = "Cart can contain foods from only one restaurant";
    private static final String INVALID_QUANTITY = "Quantity must be greater than zero";
    private static final String LOG_FOOD_ADDED_TO_CART = "Food {} added to cart";
    private static final String LOG_FOOD_REMOVED_FROM_CART = "Food {} removed from cart";
    private static final String LOG_CART_QUANTITY_UPDATED = "Cart food {} quantity updated to {}";
    private static final String LOG_CART_CLEARED = "Cart cleared";
    private static final String LOG_CART_DIFFERENT_RESTAURANTS_REJECTED =
            "Rejected cart update with food {} from restaurant {} because cart already contains restaurant {}";
    private static final String LOG_CART_UNAVAILABLE_FOOD_REJECTED =
            "Rejected cart update because food {} is not available";
    private static final String LOG_CART_INVALID_QUANTITY_REJECTED =
            "Rejected cart quantity update with invalid quantity {}";

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
        log.info(LOG_FOOD_ADDED_TO_CART, foodId);
    }

    @Override
    public void removeFood(Map<Long, Integer> cart, Long foodId) {
        cart.remove(foodId);
        log.info(LOG_FOOD_REMOVED_FROM_CART, foodId);
    }

    @Override
    public void updateQuantity(Map<Long, Integer> cart, Long foodId, Integer quantity) {
        validateQuantity(quantity);

        Food food = foodService.getFoodById(foodId);
        validateFoodAvailable(food);
        validateSameRestaurant(cart, food);

        cart.put(foodId, quantity);
        log.info(LOG_CART_QUANTITY_UPDATED, foodId, quantity);
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
        log.debug(LOG_CART_CLEARED);
    }

    private void validateSameRestaurant(Map<Long, Integer> cart, Food newFood) {
        if (!(cart.isEmpty() || cart.containsKey(newFood.getId()))) {
            Long existingFoodId = cart.keySet().iterator().next();
            Food existingFood = foodService.getFoodById(existingFoodId);

            if (!existingFood.getRestaurantId().equals(newFood.getRestaurantId())) {
                log.warn(LOG_CART_DIFFERENT_RESTAURANTS_REJECTED,
                        newFood.getId(),
                        newFood.getRestaurantId(),
                        existingFood.getRestaurantId());
                throw new CartOperationException(FOODS_FROM_DIFFERENT_RESTAURANTS);
            }
        }
    }

    private void validateFoodAvailable(Food food) {
        if (!Boolean.TRUE.equals(food.getAvailable())) {
            log.warn(LOG_CART_UNAVAILABLE_FOOD_REJECTED, food.getId());
            throw new CartOperationException(FOOD_IS_NOT_AVAILABLE);
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            log.warn(LOG_CART_INVALID_QUANTITY_REJECTED, quantity);
            throw new CartOperationException(INVALID_QUANTITY);
        }
    }
}
