package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.OrderDao;
import kz.nurdaulet.dao.OrderItemDao;
import kz.nurdaulet.dto.AdminOrderDto;
import kz.nurdaulet.dto.CheckoutDto;
import kz.nurdaulet.dto.OrderItemDetailsDto;
import kz.nurdaulet.dto.OrderSummaryDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.OrderItem;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.entity.enums.OrderStatus;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.exception.OrderOperationException;
import kz.nurdaulet.exception.OrderNotFoundException;
import kz.nurdaulet.service.CartService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.OrderService;
import kz.nurdaulet.service.RestaurantService;
import kz.nurdaulet.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String EMPTY_CART = "Cart is empty";
    private static final String FOOD_IS_NOT_AVAILABLE = "Food is not available";
    private static final String FOODS_FROM_DIFFERENT_RESTAURANTS = "Cart can contain foods from only one restaurant";
    private static final String ORDER_NOT_FOUND = "Order with id %d not found";
    private static final String ORDER_CAN_NOT_BE_PAID = "Order can not be paid";
    private static final String DO_NOT_HAVE_PERMISSION = "You can not manage this order";
    private static final String INVALID_ORDER_STATUS = "Invalid order status";
    private static final String INVALID_STATUS_TRANSITION = "Invalid order status transition";
    private static final String LOG_ORDER_CREATED =
            "Order {} created: userId={}, restaurantId={}, totalPrice={}, deliveryType={}";
    private static final String LOG_PAYMENT_ALREADY_HANDLED =
            "Payment request ignored because order {} is already paid";
    private static final String LOG_PAYMENT_REJECTED = "Payment rejected for order {} with status {}";
    private static final String LOG_ORDER_PAID = "Order {} paid by user {} and moved to {}";
    private static final String LOG_ORDER_STATUS_ALREADY_SET =
            "Order status update ignored because order {} already has status {}";
    private static final String LOG_MANAGER_UPDATED_ORDER_STATUS =
            "Manager {} updated order {} status from {} to {}";
    private static final String LOG_EMPTY_CART_REJECTED = "Order creation rejected because cart is empty";
    private static final String LOG_UNAVAILABLE_FOOD_REJECTED =
            "Order creation rejected because food {} is not available";
    private static final String LOG_DIFFERENT_RESTAURANTS_REJECTED =
            "Order creation rejected because cart contains restaurants {} and {}";
    private static final String LOG_MANAGER_PERMISSION_REJECTED =
            "Manager {} tried to manage order for restaurant {}";
    private static final String LOG_INVALID_MANAGER_TARGET_STATUS_REJECTED =
            "Rejected manager order status update to invalid target status {}";
    private static final String LOG_INVALID_STATUS_TRANSITION_REJECTED =
            "Rejected invalid order status transition from {} to {}";

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final FoodService foodService;
    private final CartService cartService;
    private final RestaurantService restaurantService;
    private final UserService userService;

    public OrderServiceImpl(OrderDao orderDao,
                            OrderItemDao orderItemDao,
                            FoodService foodService,
                            CartService cartService,
                            RestaurantService restaurantService,
                            UserService userService) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.foodService = foodService;
        this.cartService = cartService;
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, Map<Long, Integer> cart, CheckoutDto checkoutDto) {
        validateCart(cart);

        Long restaurantId = findRestaurantId(cart);
        Double totalPrice = cartService.calculateTotal(cart);
        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setUserId(userId);
        order.setRestaurantId(restaurantId);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setDeliveryType(checkoutDto.getDeliveryType());
        order.setDeliveryAddress(getDeliveryAddress(checkoutDto));
        order.setTotalPrice(totalPrice);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Long orderId = orderDao.save(order);

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Food food = foodService.getFoodById(entry.getKey());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setFoodId(food.getId());
            orderItem.setQuantity(entry.getValue());
            orderItem.setPrice(food.getPrice());

            orderItemDao.save(orderItem);
        }

        cartService.clear(cart);
        log.info(LOG_ORDER_CREATED,
                orderId,
                userId,
                restaurantId,
                totalPrice,
                order.getDeliveryType());

        return orderDao.findById(orderId);
    }

    @Override
    public Order getCustomerOrder(Long userId, Long orderId) {
        Order order = orderDao.findById(orderId);

        if (order == null || !order.getUserId().equals(userId)) {
            throw new OrderNotFoundException(ORDER_NOT_FOUND.formatted(orderId));
        }

        return order;
    }

    @Override
    public List<OrderSummaryDto> getCustomerOrders(Long userId) {
        return buildOrderSummaries(orderDao.findByUserId(userId));
    }

    @Override
    public List<OrderItemDetailsDto> getCustomerOrderItems(Long userId, Long orderId) {
        getCustomerOrder(userId, orderId);

        return buildOrderItemDetails(orderItemDao.findByOrderId(orderId));
    }

    @Override
    public Order payOrder(Long userId, Long orderId) {
        Order order = getCustomerOrder(userId, orderId);

        if (OrderStatus.PREPARING.equals(order.getStatus())) {
            log.info(LOG_PAYMENT_ALREADY_HANDLED, orderId);
            return order;
        }

        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            log.warn(LOG_PAYMENT_REJECTED, orderId, order.getStatus());
            throw new CartOperationException(ORDER_CAN_NOT_BE_PAID);
        }

        orderDao.updateStatus(orderId, OrderStatus.PREPARING);
        log.info(LOG_ORDER_PAID, orderId, userId, OrderStatus.PREPARING);

        return getCustomerOrder(userId, orderId);
    }

    @Override
    public List<AdminOrderDto> getAdminOrders() {
        List<AdminOrderDto> result = new ArrayList<>();

        for (Order order : orderDao.findAll()) {
            User user = userService.getById(order.getUserId());
            Restaurant restaurant = restaurantService.getRestaurantById(order.getRestaurantId());

            result.add(new AdminOrderDto(
                    order.getId(),
                    buildClientName(user),
                    restaurant.getName(),
                    order.getStatus(),
                    order.getTotalPrice(),
                    order.getCreatedAt()
            ));
        }

        return result;
    }

    @Override
    public Order getManagerOrder(Long managerId, Long restaurantId, Long orderId) {
        checkManagerRestaurant(managerId, restaurantId);

        Order order = orderDao.findById(orderId);

        if (order == null
                || !order.getRestaurantId().equals(restaurantId)
                || OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new OrderNotFoundException(ORDER_NOT_FOUND.formatted(orderId));
        }

        return order;
    }

    @Override
    public List<OrderItemDetailsDto> getManagerOrderItems(Long managerId, Long restaurantId, Long orderId) {
        getManagerOrder(managerId, restaurantId, orderId);

        return buildOrderItemDetails(orderItemDao.findByOrderId(orderId));
    }

    @Override
    public List<Order> getManagerOrders(Long managerId, Long restaurantId) {
        checkManagerRestaurant(managerId, restaurantId);

        return orderDao.findPaidByRestaurantId(restaurantId);
    }

    @Override
    public Order updateManagerOrderStatus(Long managerId, Long restaurantId, Long orderId, OrderStatus status) {
        validateManagerTargetStatus(status);

        Order order = getManagerOrder(managerId, restaurantId, orderId);

        if (order.getStatus().equals(status)) {
            log.info(LOG_ORDER_STATUS_ALREADY_SET, orderId, status);
            return order;
        }

        validateManagerStatusTransition(order.getStatus(), status);
        orderDao.updateStatus(orderId, status);
        log.info(LOG_MANAGER_UPDATED_ORDER_STATUS,
                managerId,
                orderId,
                order.getStatus(),
                status);

        return orderDao.findById(orderId);
    }

    private List<OrderSummaryDto> buildOrderSummaries(List<Order> orders) {
        List<OrderSummaryDto> summaries = new ArrayList<>();

        for (Order order : orders) {
            Restaurant restaurant = restaurantService.getRestaurantById(order.getRestaurantId());
            summaries.add(new OrderSummaryDto(
                    order.getId(),
                    restaurant.getName(),
                    order.getStatus(),
                    order.getDeliveryType(),
                    order.getTotalPrice(),
                    order.getCreatedAt()
            ));
        }

        return summaries;
    }

    private List<OrderItemDetailsDto> buildOrderItemDetails(List<OrderItem> orderItems) {
        List<OrderItemDetailsDto> details = new ArrayList<>();

        for (OrderItem item : orderItems) {
            Food food = foodService.getFoodById(item.getFoodId());
            details.add(new OrderItemDetailsDto(
                    item.getFoodId(),
                    food.getName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getPrice() * item.getQuantity()
            ));
        }

        return details;
    }

    private String buildClientName(User user) {
        String fullName = (user.getFirstName() + " " + user.getLastName()).trim();

        if (!fullName.isBlank()) {
            return fullName + " (" + user.getUsername() + ")";
        }

        return user.getUsername();
    }

    private void validateCart(Map<Long, Integer> cart) {
        if (cart == null || cart.isEmpty()) {
            log.warn(LOG_EMPTY_CART_REJECTED);
            throw new CartOperationException(EMPTY_CART);
        }

        findRestaurantId(cart);
    }

    private Long findRestaurantId(Map<Long, Integer> cart) {
        Long restaurantId = null;

        for (Long foodId : cart.keySet()) {
            Food food = foodService.getFoodById(foodId);

            if (!Boolean.TRUE.equals(food.getAvailable())) {
                log.warn(LOG_UNAVAILABLE_FOOD_REJECTED, food.getId());
                throw new CartOperationException(FOOD_IS_NOT_AVAILABLE);
            }

            if (restaurantId == null) {
                restaurantId = food.getRestaurantId();
            } else if (!restaurantId.equals(food.getRestaurantId())) {
                log.warn(LOG_DIFFERENT_RESTAURANTS_REJECTED,
                        restaurantId,
                        food.getRestaurantId());
                throw new CartOperationException(FOODS_FROM_DIFFERENT_RESTAURANTS);
            }
        }

        return restaurantId;
    }

    private String getDeliveryAddress(CheckoutDto checkoutDto) {
        if (DeliveryType.PICKUP.equals(checkoutDto.getDeliveryType())) {
            return null;
        }

        return checkoutDto.getDeliveryAddress();
    }

    private void checkManagerRestaurant(Long managerId, Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        if (!restaurant.getManagerId().equals(managerId)) {
            log.warn(LOG_MANAGER_PERMISSION_REJECTED, managerId, restaurantId);
            throw new OrderOperationException(DO_NOT_HAVE_PERMISSION);
        }
    }

    private void validateManagerTargetStatus(OrderStatus status) {
        if (!(OrderStatus.READY.equals(status)
                || OrderStatus.COMPLETED.equals(status)
                || OrderStatus.CANCELLED.equals(status))) {
            log.warn(LOG_INVALID_MANAGER_TARGET_STATUS_REJECTED, status);
            throw new OrderOperationException(INVALID_ORDER_STATUS);
        }
    }

    private void validateManagerStatusTransition(OrderStatus currentStatus, OrderStatus targetStatus) {
        boolean validTransition = (OrderStatus.PREPARING.equals(currentStatus)
                && (OrderStatus.READY.equals(targetStatus) || OrderStatus.CANCELLED.equals(targetStatus)))
                || (OrderStatus.READY.equals(currentStatus)
                && (OrderStatus.COMPLETED.equals(targetStatus) || OrderStatus.CANCELLED.equals(targetStatus)));

        if (!validTransition) {
            log.warn(LOG_INVALID_STATUS_TRANSITION_REJECTED, currentStatus, targetStatus);
            throw new OrderOperationException(INVALID_STATUS_TRANSITION);
        }
    }
}
