package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.OrderDao;
import kz.nurdaulet.dao.OrderItemDao;
import kz.nurdaulet.dto.CheckoutDto;
import kz.nurdaulet.dto.OrderItemDetailsDto;
import kz.nurdaulet.dto.OrderSummaryDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.OrderItem;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.entity.enums.OrderStatus;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.exception.OrderNotFoundException;
import kz.nurdaulet.exception.OrderOperationException;
import kz.nurdaulet.service.CartService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private static final Long USER_ID = 1L;
    private static final Long MANAGER_ID = 2L;
    private static final Long ANOTHER_MANAGER_ID = 3L;
    private static final Long RESTAURANT_ID = 10L;
    private static final Long ANOTHER_RESTAURANT_ID = 20L;
    private static final Long BURGER_ID = 100L;
    private static final Long FRIES_ID = 101L;
    private static final Long ORDER_ID = 1000L;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderItemDao orderItemDao;

    @Mock
    private FoodService foodService;

    @Mock
    private CartService cartService;

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private OrderServiceImpl testingInstance;

    @Test
    void shouldCreateDeliveryOrder() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 2);
        cart.put(FRIES_ID, 1);
        CheckoutDto checkout = new CheckoutDto(DeliveryType.DELIVERY, "Almaty, Abay 15");
        Order savedOrder = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));
        when(foodService.getFoodById(FRIES_ID))
                .thenReturn(createFood(FRIES_ID, 1000D, true, RESTAURANT_ID));
        when(cartService.calculateTotal(cart)).thenReturn(6000D);
        when(orderDao.save(org.mockito.ArgumentMatchers.any(Order.class))).thenReturn(ORDER_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(savedOrder);

        Order result = testingInstance.createOrder(USER_ID, cart, checkout);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).save(orderCaptor.capture());
        Order orderToSave = orderCaptor.getValue();

        assertEquals(USER_ID, orderToSave.getUserId());
        assertEquals(RESTAURANT_ID, orderToSave.getRestaurantId());
        assertEquals(OrderStatus.PENDING_PAYMENT, orderToSave.getStatus());
        assertEquals(DeliveryType.DELIVERY, orderToSave.getDeliveryType());
        assertEquals("Almaty, Abay 15", orderToSave.getDeliveryAddress());
        assertEquals(6000D, orderToSave.getTotalPrice());
        assertEquals(savedOrder, result);

        verify(orderItemDao, org.mockito.Mockito.times(2))
                .save(org.mockito.ArgumentMatchers.any());
        verify(cartService).clear(cart);
    }

    @Test
    void shouldCreatePickupOrderWithoutAddress() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);
        CheckoutDto checkout = new CheckoutDto(DeliveryType.PICKUP, "Should be ignored");
        Order savedOrder = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));
        when(cartService.calculateTotal(cart)).thenReturn(2500D);
        when(orderDao.save(org.mockito.ArgumentMatchers.any(Order.class))).thenReturn(ORDER_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(savedOrder);

        testingInstance.createOrder(USER_ID, cart, checkout);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).save(orderCaptor.capture());

        assertEquals(DeliveryType.PICKUP, orderCaptor.getValue().getDeliveryType());
        assertNull(orderCaptor.getValue().getDeliveryAddress());
    }

    @Test
    void shouldNotCreateOrderWhenCartIsEmpty() {
        Map<Long, Integer> cart = new HashMap<>();

        assertThrows(CartOperationException.class,
                () -> testingInstance.createOrder(USER_ID, cart, new CheckoutDto()));

        verifyNoInteractions(orderDao);
        verifyNoInteractions(orderItemDao);
    }

    @Test
    void shouldNotCreateOrderWhenFoodIsUnavailable() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, false, RESTAURANT_ID));

        assertThrows(CartOperationException.class,
                () -> testingInstance.createOrder(USER_ID, cart, new CheckoutDto()));

        verifyNoInteractions(orderDao);
    }

    @Test
    void shouldNotCreateOrderFromDifferentRestaurants() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);
        cart.put(FRIES_ID, 1);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));
        when(foodService.getFoodById(FRIES_ID))
                .thenReturn(createFood(FRIES_ID, 1000D, true, ANOTHER_RESTAURANT_ID));

        assertThrows(CartOperationException.class,
                () -> testingInstance.createOrder(USER_ID, cart, new CheckoutDto()));

        verifyNoInteractions(orderDao);
    }

    @Test
    void shouldPayOrder() {
        Order pendingOrder = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);
        Order preparingOrder = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);

        when(orderDao.findById(ORDER_ID)).thenReturn(pendingOrder, preparingOrder);

        Order result = testingInstance.payOrder(USER_ID, ORDER_ID);

        verify(orderDao).updateStatus(ORDER_ID, OrderStatus.PREPARING);
        assertEquals(OrderStatus.PREPARING, result.getStatus());
    }

    @Test
    void shouldNotPayOrderTwice() {
        Order preparingOrder = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(preparingOrder);

        assertThrows(CartOperationException.class,
                () -> testingInstance.payOrder(USER_ID, ORDER_ID));
    }

    @Test
    void shouldNotGetAnotherCustomerOrder() {
        Order order = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(order);

        assertThrows(OrderNotFoundException.class,
                () -> testingInstance.getCustomerOrder(999L, ORDER_ID));
    }

    @Test
    void shouldGetCustomerOrders() {
        List<Order> orders = List.of(
                createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID),
                createOrder(OrderStatus.READY, USER_ID, RESTAURANT_ID)
        );
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        restaurant.setName("Burger House");

        when(orderDao.findByUserId(USER_ID)).thenReturn(orders);
        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        List<OrderSummaryDto> result = testingInstance.getCustomerOrders(USER_ID);

        assertEquals(2, result.size());
        assertEquals("Burger House", result.get(0).getRestaurantName());
        assertEquals(OrderStatus.PREPARING, result.get(0).getStatus());
    }

    @Test
    void shouldGetCustomerOrderItems() {
        Order order = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);
        OrderItem orderItem = new OrderItem(1L, ORDER_ID, BURGER_ID, 2, 2500D);

        when(orderDao.findById(ORDER_ID)).thenReturn(order);
        when(orderItemDao.findByOrderId(ORDER_ID)).thenReturn(List.of(orderItem));
        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));

        List<OrderItemDetailsDto> result = testingInstance.getCustomerOrderItems(USER_ID, ORDER_ID);

        assertEquals(1, result.size());
        assertEquals(BURGER_ID, result.get(0).getFoodId());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(5000D, result.get(0).getSubtotal());
    }

    @Test
    void shouldGetManagerOrders() {
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        List<Order> orders = List.of(createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID));

        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderDao.findPaidByRestaurantId(RESTAURANT_ID)).thenReturn(orders);

        List<Order> result = testingInstance.getManagerOrders(MANAGER_ID, RESTAURANT_ID);

        assertEquals(orders, result);
    }

    @Test
    void shouldNotGetManagerOrdersForAnotherManagerRestaurant() {
        Restaurant restaurant = createRestaurant(ANOTHER_MANAGER_ID);
        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        assertThrows(OrderOperationException.class,
                () -> testingInstance.getManagerOrders(MANAGER_ID, RESTAURANT_ID));
    }

    @Test
    void shouldUpdateManagerOrderStatusFromPreparingToReady() {
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        Order preparingOrder = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);
        Order readyOrder = createOrder(OrderStatus.READY, USER_ID, RESTAURANT_ID);

        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderDao.findById(ORDER_ID)).thenReturn(preparingOrder, readyOrder);

        Order result = testingInstance.updateManagerOrderStatus(
                MANAGER_ID, RESTAURANT_ID, ORDER_ID, OrderStatus.READY);

        verify(orderDao).updateStatus(ORDER_ID, OrderStatus.READY);
        assertEquals(OrderStatus.READY, result.getStatus());
    }

    @Test
    void shouldNotUpdateManagerOrderStatusWithInvalidTransition() {
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        Order completedOrder = createOrder(OrderStatus.COMPLETED, USER_ID, RESTAURANT_ID);

        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderDao.findById(ORDER_ID)).thenReturn(completedOrder);

        assertThrows(OrderOperationException.class,
                () -> testingInstance.updateManagerOrderStatus(
                        MANAGER_ID, RESTAURANT_ID, ORDER_ID, OrderStatus.READY));
    }

    private Food createFood(Long id, Double price, Boolean isAvailable, Long restaurantId) {
        return new Food(id, "Food", "Description", price, isAvailable, restaurantId, 1L);
    }

    private Order createOrder(OrderStatus status, Long userId, Long restaurantId) {
        return new Order(
                ORDER_ID,
                userId,
                restaurantId,
                status,
                DeliveryType.DELIVERY,
                "Address",
                5000D,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Restaurant createRestaurant(Long managerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(RESTAURANT_ID);
        restaurant.setManagerId(managerId);

        return restaurant;
    }
}
