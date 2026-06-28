package kz.nurdaulet.service;

import kz.nurdaulet.dto.CheckoutDto;
import kz.nurdaulet.dto.AdminOrderDto;
import kz.nurdaulet.dto.OrderItemDetailsDto;
import kz.nurdaulet.dto.OrderSummaryDto;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.enums.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {
    /**
     * Creates a new order from the user's cart and checkout data.
     */
    Order createOrder(Long userId, Map<Long, Integer> cart, CheckoutDto checkoutDto);

    /**
     * Returns a customer order owned by the given user.
     */
    Order getCustomerOrder(Long userId, Long orderId);

    /**
     * Returns order summaries for the customer order history.
     */
    List<OrderSummaryDto> getCustomerOrders(Long userId);

    /**
     * Returns item details for a customer order owned by the given user.
     */
    List<OrderItemDetailsDto> getCustomerOrderItems(Long userId, Long orderId);

    /**
     * Marks a pending customer order as paid and starts preparation.
     */
    Order payOrder(Long userId, Long orderId);

    /**
     * Returns all orders for admin monitoring.
     */
    List<AdminOrderDto> getAdminOrders();

    /**
     * Returns a paid restaurant order that belongs to the manager's restaurant.
     */
    Order getManagerOrder(Long managerId, Long restaurantId, Long orderId);

    /**
     * Returns item details for a manager-visible restaurant order.
     */
    List<OrderItemDetailsDto> getManagerOrderItems(Long managerId, Long restaurantId, Long orderId);

    /**
     * Returns paid orders for the manager's restaurant.
     */
    List<Order> getManagerOrders(Long managerId, Long restaurantId);

    /**
     * Updates a manager-visible order status using allowed manager transitions.
     */
    Order updateManagerOrderStatus(Long managerId, Long restaurantId, Long orderId, OrderStatus status);
}
