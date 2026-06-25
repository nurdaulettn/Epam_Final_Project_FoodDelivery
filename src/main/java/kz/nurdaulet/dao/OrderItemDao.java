package kz.nurdaulet.dao;

import kz.nurdaulet.entity.OrderItem;

import java.util.List;

public interface OrderItemDao {
    void save(OrderItem orderItem);

    List<OrderItem> findByOrderId(Long orderId);
}
