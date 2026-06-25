package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.OrderDao;
import kz.nurdaulet.dao.OrderItemDao;
import kz.nurdaulet.dto.CheckoutDto;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.OrderItem;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.entity.enums.OrderStatus;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.exception.OrderNotFoundException;
import kz.nurdaulet.service.CartService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    private static final String EMPTY_CART = "Cart is empty";
    private static final String FOOD_IS_NOT_AVAILABLE = "Food is not available";
    private static final String FOODS_FROM_DIFFERENT_RESTAURANTS =
            "Cart can contain foods from only one restaurant";
    private static final String ORDER_NOT_FOUND = "Order with id %d not found";
    private static final String ORDER_CAN_NOT_BE_PAID = "Order can not be paid";

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final FoodService foodService;
    private final CartService cartService;

    public OrderServiceImpl(OrderDao orderDao,
                            OrderItemDao orderItemDao,
                            FoodService foodService,
                            CartService cartService) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.foodService = foodService;
        this.cartService = cartService;
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
    public List<OrderItem> getCustomerOrderItems(Long userId, Long orderId) {
        getCustomerOrder(userId, orderId);

        return orderItemDao.findByOrderId(orderId);
    }

    @Override
    public Order payOrder(Long userId, Long orderId) {
        Order order = getCustomerOrder(userId, orderId);

        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new CartOperationException(ORDER_CAN_NOT_BE_PAID);
        }

        orderDao.updateStatus(orderId, OrderStatus.PREPARING);

        return getCustomerOrder(userId, orderId);
    }

    private void validateCart(Map<Long, Integer> cart) {
        if (cart == null || cart.isEmpty()) {
            throw new CartOperationException(EMPTY_CART);
        }

        findRestaurantId(cart);
    }

    private Long findRestaurantId(Map<Long, Integer> cart) {
        Long restaurantId = null;

        for (Long foodId : cart.keySet()) {
            Food food = foodService.getFoodById(foodId);

            if (!Boolean.TRUE.equals(food.getAvailable())) {
                throw new CartOperationException(FOOD_IS_NOT_AVAILABLE);
            }

            if (restaurantId == null) {
                restaurantId = food.getRestaurantId();
            } else if (!restaurantId.equals(food.getRestaurantId())) {
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
}
