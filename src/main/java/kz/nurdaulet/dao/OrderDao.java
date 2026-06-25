package kz.nurdaulet.dao;

import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.enums.OrderStatus;

public interface OrderDao {
    Long save(Order order);

    Order findById(Long id);

    void updateStatus(Long id, OrderStatus status);
}
