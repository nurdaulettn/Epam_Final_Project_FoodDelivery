package kz.nurdaulet.service;

import kz.nurdaulet.dto.CheckoutDto;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.OrderItem;
import kz.nurdaulet.entity.enums.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Long userId, Map<Long, Integer> cart, CheckoutDto checkoutDto);

    Order getCustomerOrder(Long userId, Long orderId);

    List<Order> getCustomerOrders(Long userId);

    List<OrderItem> getCustomerOrderItems(Long userId, Long orderId);

    Order payOrder(Long userId, Long orderId);

    List<Order> getManagerOrders(Long managerId, Long restaurantId);

    Order updateManagerOrderStatus(Long managerId, Long restaurantId, Long orderId, OrderStatus status);
}
