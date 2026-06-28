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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    private static final Long BURGER_ID = 1L;
    private static final Long FRIES_ID = 2L;
    private static final Long PIZZA_ID = 3L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long ANOTHER_RESTAURANT_ID = 20L;
    private static final Long CATEGORY_ID = 1L;
    private static final String BURGER_NAME = "Burger";
    private static final String FRIES_NAME = "Fries";
    private static final String PIZZA_NAME = "Pizza";
    private static final String FOOD_DESCRIPTION = "Description";
    private static final Double BURGER_PRICE = 2500D;
    private static final Double FRIES_PRICE = 1000D;
    private static final Double PIZZA_PRICE = 4000D;
    private static final Double EXPECTED_TOTAL = 8000D;
    private static final Integer INITIAL_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 3;
    private static final Integer BURGER_QUANTITY = 2;
    private static final Integer FRIES_QUANTITY = 3;
    private static final Integer INVALID_QUANTITY = 0;

    @Mock
    private FoodService foodService;

    @InjectMocks
    private CartServiceImpl testingInstance;

    @Test
    void shouldAddFoodToCart() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, BURGER_NAME, BURGER_PRICE, true, RESTAURANT_ID));

        // when
        testingInstance.addFood(cart, BURGER_ID);
        testingInstance.addFood(cart, BURGER_ID);

        // then
        assertEquals(1, cart.size());
        assertEquals(BURGER_QUANTITY, cart.get(BURGER_ID));
        verify(foodService, times(2)).getFoodById(BURGER_ID);
    }

    @Test
    void shouldNotAddFoodFromDifferentRestaurant() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, INITIAL_QUANTITY);

        when(foodService.getFoodById(PIZZA_ID))
                .thenReturn(createFood(PIZZA_ID, PIZZA_NAME, PIZZA_PRICE, true, ANOTHER_RESTAURANT_ID));
        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, BURGER_NAME, BURGER_PRICE, true, RESTAURANT_ID));

        // when / then
        assertThrows(CartOperationException.class, () -> testingInstance.addFood(cart, PIZZA_ID));
        verify(foodService).getFoodById(PIZZA_ID);
        verify(foodService).getFoodById(BURGER_ID);
    }

    @Test
    void shouldNotAddUnavailableFood() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, BURGER_NAME, BURGER_PRICE, false, RESTAURANT_ID));

        // when / then
        assertThrows(CartOperationException.class, () -> testingInstance.addFood(cart, BURGER_ID));
        verify(foodService).getFoodById(BURGER_ID);
    }

    @Test
    void shouldUpdateQuantity() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, INITIAL_QUANTITY);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, BURGER_NAME, BURGER_PRICE, true, RESTAURANT_ID));

        // when
        testingInstance.updateQuantity(cart, BURGER_ID, UPDATED_QUANTITY);

        // then
        assertEquals(UPDATED_QUANTITY, cart.get(BURGER_ID));
        verify(foodService).getFoodById(BURGER_ID);
    }

    @Test
    void shouldNotUpdateQuantityWhenQuantityIsInvalid() {
        // given
        Map<Long, Integer> cart = new HashMap<>();

        // when / then
        assertThrows(CartOperationException.class, () -> testingInstance.updateQuantity(cart, BURGER_ID, INVALID_QUANTITY));
        verifyNoInteractions(foodService);
    }

    @Test
    void shouldReturnCartItemsAndCalculateTotal() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, BURGER_QUANTITY);
        cart.put(FRIES_ID, FRIES_QUANTITY);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, BURGER_NAME, BURGER_PRICE, true, RESTAURANT_ID));
        when(foodService.getFoodById(FRIES_ID))
                .thenReturn(createFood(FRIES_ID, FRIES_NAME, FRIES_PRICE, true, RESTAURANT_ID));

        // when
        List<CartItemDto> cartItems = testingInstance.getCartItems(cart);
        Double total = testingInstance.calculateTotal(cart);

        // then
        assertEquals(2, cartItems.size());
        assertEquals(EXPECTED_TOTAL, total);
        verify(foodService, times(2)).getFoodById(BURGER_ID);
        verify(foodService, times(2)).getFoodById(FRIES_ID);
    }

    @Test
    void shouldRemoveFoodAndClearCart() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, INITIAL_QUANTITY);
        cart.put(FRIES_ID, INITIAL_QUANTITY);

        // when
        testingInstance.removeFood(cart, BURGER_ID);
        testingInstance.clear(cart);

        // then
        assertTrue(cart.isEmpty());
        verifyNoInteractions(foodService);
    }

    private Food createFood(Long id, String name, Double price, Boolean isAvailable, Long restaurantId) {
        return new Food(id, name, FOOD_DESCRIPTION, price, isAvailable, restaurantId, CATEGORY_ID);
    }
}
