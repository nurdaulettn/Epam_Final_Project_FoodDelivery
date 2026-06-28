package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.OrderDao;
import kz.nurdaulet.dao.OrderItemDao;
import kz.nurdaulet.dto.CheckoutDto;
import kz.nurdaulet.dto.AdminOrderDto;
import kz.nurdaulet.dto.OrderItemDetailsDto;
import kz.nurdaulet.dto.OrderSummaryDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.OrderItem;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.entity.enums.OrderStatus;
import kz.nurdaulet.entity.enums.Role;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.exception.OrderNotFoundException;
import kz.nurdaulet.exception.OrderOperationException;
import kz.nurdaulet.service.CartService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.RestaurantService;
import kz.nurdaulet.service.UserService;
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
    private static final Long FOOD_CATEGORY_ID = 1L;
    private static final String DELIVERY_ADDRESS = "Almaty, Abay 15";
    private static final String IGNORED_PICKUP_ADDRESS = "Should be ignored";
    private static final String DEFAULT_ORDER_ADDRESS = "Address";
    private static final String RESTAURANT_NAME = "Burger House";
    private static final String FOOD_NAME = "Food";
    private static final String FOOD_DESCRIPTION = "Description";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USERNAME = "john";
    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD = "password";
    private static final String CLIENT_DISPLAY_NAME = "John Doe (john)";

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

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderServiceImpl testingInstance;

    @Test
    void shouldCreateDeliveryOrder() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 2);
        cart.put(FRIES_ID, 1);
        CheckoutDto checkout = new CheckoutDto(DeliveryType.DELIVERY, DELIVERY_ADDRESS);
        Order savedOrder = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));
        when(foodService.getFoodById(FRIES_ID))
                .thenReturn(createFood(FRIES_ID, 1000D, true, RESTAURANT_ID));
        when(cartService.calculateTotal(cart)).thenReturn(6000D);
        when(orderDao.save(org.mockito.ArgumentMatchers.any(Order.class))).thenReturn(ORDER_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(savedOrder);

        // when
        Order result = testingInstance.createOrder(USER_ID, cart, checkout);

        // then
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).save(orderCaptor.capture());
        Order orderToSave = orderCaptor.getValue();

        assertEquals(USER_ID, orderToSave.getUserId());
        assertEquals(RESTAURANT_ID, orderToSave.getRestaurantId());
        assertEquals(OrderStatus.PENDING_PAYMENT, orderToSave.getStatus());
        assertEquals(DeliveryType.DELIVERY, orderToSave.getDeliveryType());
        assertEquals(DELIVERY_ADDRESS, orderToSave.getDeliveryAddress());
        assertEquals(6000D, orderToSave.getTotalPrice());
        assertEquals(savedOrder, result);

        verify(orderItemDao, org.mockito.Mockito.times(2))
                .save(org.mockito.ArgumentMatchers.any());
        verify(cartService).clear(cart);
    }

    @Test
    void shouldCreatePickupOrderWithoutAddress() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);
        CheckoutDto checkout = new CheckoutDto(DeliveryType.PICKUP, IGNORED_PICKUP_ADDRESS);
        Order savedOrder = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));
        when(cartService.calculateTotal(cart)).thenReturn(2500D);
        when(orderDao.save(org.mockito.ArgumentMatchers.any(Order.class))).thenReturn(ORDER_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(savedOrder);

        // when
        testingInstance.createOrder(USER_ID, cart, checkout);

        // then
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).save(orderCaptor.capture());

        assertEquals(DeliveryType.PICKUP, orderCaptor.getValue().getDeliveryType());
        assertNull(orderCaptor.getValue().getDeliveryAddress());
        verify(orderItemDao).save(org.mockito.ArgumentMatchers.any());
        verify(cartService).clear(cart);
    }

    @Test
    void shouldNotCreateOrderWhenCartIsEmpty() {
        // given
        Map<Long, Integer> cart = new HashMap<>();

        // when / then
        assertThrows(CartOperationException.class,
                () -> testingInstance.createOrder(USER_ID, cart, new CheckoutDto()));

        verifyNoInteractions(orderDao);
        verifyNoInteractions(orderItemDao);
    }

    @Test
    void shouldNotCreateOrderWhenFoodIsUnavailable() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, false, RESTAURANT_ID));

        // when / then
        assertThrows(CartOperationException.class,
                () -> testingInstance.createOrder(USER_ID, cart, new CheckoutDto()));

        verify(foodService).getFoodById(BURGER_ID);
        verifyNoInteractions(orderDao);
    }

    @Test
    void shouldNotCreateOrderFromDifferentRestaurants() {
        // given
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(BURGER_ID, 1);
        cart.put(FRIES_ID, 1);

        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));
        when(foodService.getFoodById(FRIES_ID))
                .thenReturn(createFood(FRIES_ID, 1000D, true, ANOTHER_RESTAURANT_ID));

        // when / then
        assertThrows(CartOperationException.class,
                () -> testingInstance.createOrder(USER_ID, cart, new CheckoutDto()));

        verify(foodService).getFoodById(BURGER_ID);
        verify(foodService).getFoodById(FRIES_ID);
        verifyNoInteractions(orderDao);
    }

    @Test
    void shouldPayOrder() {
        // given
        Order pendingOrder = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);
        Order preparingOrder = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);

        when(orderDao.findById(ORDER_ID)).thenReturn(pendingOrder, preparingOrder);

        // when
        Order result = testingInstance.payOrder(USER_ID, ORDER_ID);

        // then
        verify(orderDao, org.mockito.Mockito.times(2)).findById(ORDER_ID);
        verify(orderDao).updateStatus(ORDER_ID, OrderStatus.PREPARING);
        assertEquals(OrderStatus.PREPARING, result.getStatus());
    }

    @Test
    void shouldNotPayOrderTwice() {
        // given
        Order preparingOrder = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(preparingOrder);

        // when / then
        assertThrows(CartOperationException.class,
                () -> testingInstance.payOrder(USER_ID, ORDER_ID));
        verify(orderDao).findById(ORDER_ID);
    }

    @Test
    void shouldNotGetAnotherCustomerOrder() {
        // given
        Order order = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);
        when(orderDao.findById(ORDER_ID)).thenReturn(order);

        // when / then
        assertThrows(OrderNotFoundException.class,
                () -> testingInstance.getCustomerOrder(999L, ORDER_ID));
        verify(orderDao).findById(ORDER_ID);
    }

    @Test
    void shouldGetCustomerOrders() {
        // given
        List<Order> orders = List.of(
                createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID),
                createOrder(OrderStatus.READY, USER_ID, RESTAURANT_ID)
        );
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        restaurant.setName(RESTAURANT_NAME);

        when(orderDao.findByUserId(USER_ID)).thenReturn(orders);
        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        // when
        List<OrderSummaryDto> result = testingInstance.getCustomerOrders(USER_ID);

        // then
        assertEquals(2, result.size());
        assertEquals(RESTAURANT_NAME, result.get(0).getRestaurantName());
        assertEquals(OrderStatus.PREPARING, result.get(0).getStatus());
        verify(orderDao).findByUserId(USER_ID);
        verify(restaurantService, org.mockito.Mockito.times(2)).getRestaurantById(RESTAURANT_ID);
    }

    @Test
    void shouldGetCustomerOrderItems() {
        // given
        Order order = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);
        OrderItem orderItem = new OrderItem(1L, ORDER_ID, BURGER_ID, 2, 2500D);

        when(orderDao.findById(ORDER_ID)).thenReturn(order);
        when(orderItemDao.findByOrderId(ORDER_ID)).thenReturn(List.of(orderItem));
        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));

        // when
        List<OrderItemDetailsDto> result = testingInstance.getCustomerOrderItems(USER_ID, ORDER_ID);

        // then
        assertEquals(1, result.size());
        assertEquals(BURGER_ID, result.get(0).getFoodId());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(5000D, result.get(0).getSubtotal());
        verify(orderDao).findById(ORDER_ID);
        verify(orderItemDao).findByOrderId(ORDER_ID);
        verify(foodService).getFoodById(BURGER_ID);
    }

    @Test
    void shouldGetAdminOrders() {
        // given
        Order order = createOrder(OrderStatus.READY, USER_ID, RESTAURANT_ID);
        User user = createUser();
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        restaurant.setName(RESTAURANT_NAME);

        when(orderDao.findAll()).thenReturn(List.of(order));
        when(userService.getById(USER_ID)).thenReturn(user);
        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        // when
        List<AdminOrderDto> result = testingInstance.getAdminOrders();

        // then
        assertEquals(1, result.size());
        assertEquals(ORDER_ID, result.get(0).getId());
        assertEquals(CLIENT_DISPLAY_NAME, result.get(0).getClientName());
        assertEquals(RESTAURANT_NAME, result.get(0).getRestaurantName());
        assertEquals(OrderStatus.READY, result.get(0).getStatus());
        assertEquals(5000D, result.get(0).getTotalPrice());
        verify(orderDao).findAll();
        verify(userService).getById(USER_ID);
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
    }

    @Test
    void shouldGetManagerOrders() {
        // given
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        List<Order> orders = List.of(createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID));

        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderDao.findPaidByRestaurantId(RESTAURANT_ID)).thenReturn(orders);

        // when
        List<Order> result = testingInstance.getManagerOrders(MANAGER_ID, RESTAURANT_ID);

        // then
        assertEquals(orders, result);
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(orderDao).findPaidByRestaurantId(RESTAURANT_ID);
    }

    @Test
    void shouldGetManagerOrderItems() {
        // given
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        Order order = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);
        OrderItem orderItem = new OrderItem(1L, ORDER_ID, BURGER_ID, 2, 2500D);

        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderDao.findById(ORDER_ID)).thenReturn(order);
        when(orderItemDao.findByOrderId(ORDER_ID)).thenReturn(List.of(orderItem));
        when(foodService.getFoodById(BURGER_ID))
                .thenReturn(createFood(BURGER_ID, 2500D, true, RESTAURANT_ID));

        // when
        List<OrderItemDetailsDto> result = testingInstance.getManagerOrderItems(
                MANAGER_ID, RESTAURANT_ID, ORDER_ID);

        // then
        assertEquals(1, result.size());
        assertEquals(BURGER_ID, result.get(0).getFoodId());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(5000D, result.get(0).getSubtotal());
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(orderDao).findById(ORDER_ID);
        verify(orderItemDao).findByOrderId(ORDER_ID);
        verify(foodService).getFoodById(BURGER_ID);
    }

    @Test
    void shouldNotGetPendingPaymentOrderForManager() {
        // given
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        Order pendingOrder = createOrder(OrderStatus.PENDING_PAYMENT, USER_ID, RESTAURANT_ID);

        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderDao.findById(ORDER_ID)).thenReturn(pendingOrder);

        // when / then
        assertThrows(OrderNotFoundException.class,
                () -> testingInstance.getManagerOrder(MANAGER_ID, RESTAURANT_ID, ORDER_ID));

        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(orderDao).findById(ORDER_ID);
        verifyNoInteractions(orderItemDao);
    }

    @Test
    void shouldNotGetManagerOrdersForAnotherManagerRestaurant() {
        // given
        Restaurant restaurant = createRestaurant(ANOTHER_MANAGER_ID);
        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);

        // when / then
        assertThrows(OrderOperationException.class,
                () -> testingInstance.getManagerOrders(MANAGER_ID, RESTAURANT_ID));
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
    }

    @Test
    void shouldUpdateManagerOrderStatusFromPreparingToReady() {
        // given
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        Order preparingOrder = createOrder(OrderStatus.PREPARING, USER_ID, RESTAURANT_ID);
        Order readyOrder = createOrder(OrderStatus.READY, USER_ID, RESTAURANT_ID);

        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderDao.findById(ORDER_ID)).thenReturn(preparingOrder, readyOrder);

        // when
        Order result = testingInstance.updateManagerOrderStatus(
                MANAGER_ID, RESTAURANT_ID, ORDER_ID, OrderStatus.READY);

        // then
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(orderDao, org.mockito.Mockito.times(2)).findById(ORDER_ID);
        verify(orderDao).updateStatus(ORDER_ID, OrderStatus.READY);
        assertEquals(OrderStatus.READY, result.getStatus());
    }

    @Test
    void shouldNotUpdateManagerOrderStatusWithInvalidTransition() {
        // given
        Restaurant restaurant = createRestaurant(MANAGER_ID);
        Order completedOrder = createOrder(OrderStatus.COMPLETED, USER_ID, RESTAURANT_ID);

        when(restaurantService.getRestaurantById(RESTAURANT_ID)).thenReturn(restaurant);
        when(orderDao.findById(ORDER_ID)).thenReturn(completedOrder);

        // when / then
        assertThrows(OrderOperationException.class,
                () -> testingInstance.updateManagerOrderStatus(
                        MANAGER_ID, RESTAURANT_ID, ORDER_ID, OrderStatus.READY));
        verify(restaurantService).getRestaurantById(RESTAURANT_ID);
        verify(orderDao).findById(ORDER_ID);
    }

    private Food createFood(Long id, Double price, Boolean isAvailable, Long restaurantId) {
        return new Food(id, FOOD_NAME, FOOD_DESCRIPTION, price, isAvailable, restaurantId, FOOD_CATEGORY_ID);
    }

    private Order createOrder(OrderStatus status, Long userId, Long restaurantId) {
        return new Order(
                ORDER_ID,
                userId,
                restaurantId,
                status,
                DeliveryType.DELIVERY,
                DEFAULT_ORDER_ADDRESS,
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

    private User createUser() {
        return new User(
                USER_ID,
                FIRST_NAME,
                LAST_NAME,
                USERNAME,
                EMAIL,
                PASSWORD,
                Role.CUSTOMER,
                true,
                LocalDateTime.now()
        );
    }
}
