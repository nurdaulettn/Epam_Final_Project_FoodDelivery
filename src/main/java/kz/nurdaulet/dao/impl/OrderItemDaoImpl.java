package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.OrderItemDao;
import kz.nurdaulet.entity.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemDaoImpl implements OrderItemDao {
    private static final String SAVE = """
            INSERT INTO order_items (order_id, food_id, quantity, price)
            VALUES (?, ?, ?, ?)
            """;
    private static final String FIND_BY_ORDER_ID = "SELECT * FROM order_items WHERE order_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<OrderItem> mapper = (rs, rowNum) -> new OrderItem(
            rs.getLong("id"),
            rs.getLong("order_id"),
            rs.getLong("food_id"),
            rs.getInt("quantity"),
            rs.getDouble("price")
    );

    public OrderItemDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(OrderItem orderItem) {
        jdbcTemplate.update(SAVE,
                orderItem.getOrderId(),
                orderItem.getFoodId(),
                orderItem.getQuantity(),
                orderItem.getPrice());
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return jdbcTemplate.query(FIND_BY_ORDER_ID, mapper, orderId);
    }
}
