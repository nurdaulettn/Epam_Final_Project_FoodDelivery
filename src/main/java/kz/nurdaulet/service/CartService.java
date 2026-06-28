package kz.nurdaulet.service;

import kz.nurdaulet.dto.CartItemDto;

import java.util.List;
import java.util.Map;

public interface CartService {
    /**
     * Adds one food item to the session cart.
     */
    void addFood(Map<Long, Integer> cart, Long foodId);

    /**
     * Removes a food item from the session cart.
     */
    void removeFood(Map<Long, Integer> cart, Long foodId);

    /**
     * Updates the quantity of a food item in the session cart.
     */
    void updateQuantity(Map<Long, Integer> cart, Long foodId, Integer quantity);

    /**
     * Builds cart item details for displaying the session cart.
     */
    List<CartItemDto> getCartItems(Map<Long, Integer> cart);

    /**
     * Calculates the total price of all items in the session cart.
     */
    Double calculateTotal(Map<Long, Integer> cart);

    /**
     * Removes all items from the session cart.
     */
    void clear(Map<Long, Integer> cart);
}
