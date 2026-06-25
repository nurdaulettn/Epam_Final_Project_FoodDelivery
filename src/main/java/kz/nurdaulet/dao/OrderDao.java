package kz.nurdaulet.dao;

import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.enums.OrderStatus;

import java.util.List;

public interface OrderDao {
    Long save(Order order);

    Order findById(Long id);

    List<Order> findByUserId(Long userId);

    void updateStatus(Long id, OrderStatus status);
}
