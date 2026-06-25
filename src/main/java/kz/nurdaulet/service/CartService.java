package kz.nurdaulet.service;

import kz.nurdaulet.dto.CartItemDto;

import java.util.List;
import java.util.Map;

public interface CartService {
    void addFood(Map<Long, Integer> cart, Long foodId);

    void removeFood(Map<Long, Integer> cart, Long foodId);

    void updateQuantity(Map<Long, Integer> cart, Long foodId, Integer quantity);

    List<CartItemDto> getCartItems(Map<Long, Integer> cart);

    Double calculateTotal(Map<Long, Integer> cart);

    void clear(Map<Long, Integer> cart);
}
