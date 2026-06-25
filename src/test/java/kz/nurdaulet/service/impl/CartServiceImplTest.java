package kz.nurdaulet.service.impl;

import kz.nurdaulet.dto.CartItemDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.FoodService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    private static final Long BURGER_ID = 1L;
    private static final Long FRIES_ID = 2L;
    private static final Long PIZZA_ID = 3L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long ANOTHER_RESTAURANT_ID = 20L;

    @Mock
    private FoodService foodService;

    @InjectMocks
    private CartServiceImpl testingInstance;

    @Test
    void shouldAddFoodToCart() {
        Map<Long, Integer> cart = new HashMap<>();
        when(foodService.getFoodById(BURGER_ID)).thenReturn(createFood(BURGER_ID, "Burger", 2500D, true, RESTAURANT_ID));

        testingInstance.addFood(cart, BURGER_ID);
        testingInstance.addFood(cart, BURGER_ID);

        assertEquals(1, cart.size());
        assertEquals(2, cart.get(BURGER_ID));
    }

    @Test
    void shouldNotAddFoodFromDifferentRestaurant() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);

        when(foodService.getFoodById(PIZZA_ID)).thenReturn(createFood(PIZZA_ID, "Pizza", 4000D, true, ANOTHER_RESTAURANT_ID));
        when(foodService.getFoodById(BURGER_ID)).thenReturn(createFood(BURGER_ID, "Burger", 2500D, true, RESTAURANT_ID));

        assertThrows(CartOperationException.class, () -> testingInstance.addFood(cart, PIZZA_ID));
    }

    @Test
    void shouldNotAddUnavailableFood() {
        Map<Long, Integer> cart = new HashMap<>();
        when(foodService.getFoodById(BURGER_ID)).thenReturn(createFood(BURGER_ID, "Burger", 2500D, false, RESTAURANT_ID));

        assertThrows(CartOperationException.class, () -> testingInstance.addFood(cart, BURGER_ID));
    }

    @Test
    void shouldUpdateQuantity() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);

        when(foodService.getFoodById(BURGER_ID)).thenReturn(createFood(BURGER_ID, "Burger", 2500D, true, RESTAURANT_ID));

        testingInstance.updateQuantity(cart, BURGER_ID, 3);

        assertEquals(3, cart.get(BURGER_ID));
    }

    @Test
    void shouldNotUpdateQuantityWhenQuantityIsInvalid() {
        Map<Long, Integer> cart = new HashMap<>();

        assertThrows(CartOperationException.class, () -> testingInstance.updateQuantity(cart, BURGER_ID, 0));
    }

    @Test
    void shouldReturnCartItemsAndCalculateTotal() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 2);
        cart.put(FRIES_ID, 3);

        when(foodService.getFoodById(BURGER_ID)).thenReturn(createFood(BURGER_ID, "Burger", 2500D, true, RESTAURANT_ID));
        when(foodService.getFoodById(FRIES_ID)).thenReturn(createFood(FRIES_ID, "Fries", 1000D, true, RESTAURANT_ID));

        List<CartItemDto> cartItems = testingInstance.getCartItems(cart);
        Double total = testingInstance.calculateTotal(cart);

        assertEquals(2, cartItems.size());
        assertEquals(8000D, total);
    }

    @Test
    void shouldRemoveFoodAndClearCart() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);
        cart.put(FRIES_ID, 1);

        testingInstance.removeFood(cart, BURGER_ID);
        testingInstance.clear(cart);

        assertTrue(cart.isEmpty());
    }

    private Food createFood(Long id, String name, Double price, Boolean isAvailable, Long restaurantId) {
        return new Food(id, name, "Description", price, isAvailable, restaurantId, 1L);
    }
}
