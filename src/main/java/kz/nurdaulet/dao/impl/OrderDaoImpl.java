package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.OrderDao;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.entity.enums.OrderStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;

@Repository
public class OrderDaoImpl implements OrderDao {
    private static final String SAVE = """
            INSERT INTO orders (user_id, restaurant_id, status, delivery_type,
                                delivery_address, total_price, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String FIND_BY_ID = "SELECT * FROM orders WHERE id = ?";
    private static final String UPDATE_STATUS = "UPDATE orders SET status = ?, updated_at = ? WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Order> mapper = (rs, rowNum) -> new Order(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getLong("restaurant_id"),
            OrderStatus.valueOf(rs.getString("status")),
            DeliveryType.valueOf(rs.getString("delivery_type")),
            rs.getString("delivery_address"),
            rs.getDouble("total_price"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
    );

    public OrderDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long save(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(SAVE, new String[]{"id"});
            statement.setLong(1, order.getUserId());
            statement.setLong(2, order.getRestaurantId());
            statement.setString(3, order.getStatus().name());
            statement.setString(4, order.getDeliveryType().name());
            statement.setString(5, order.getDeliveryAddress());
            statement.setDouble(6, order.getTotalPrice());
            statement.setObject(7, order.getCreatedAt());
            statement.setObject(8, order.getUpdatedAt());
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();

        return key == null ? null : key.longValue();
    }

    @Override
    public Order findById(Long id) {
        return jdbcTemplate.query(FIND_BY_ID, mapper, id)
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updateStatus(Long id, OrderStatus status) {
        jdbcTemplate.update(UPDATE_STATUS, status.name(), LocalDateTime.now(), id);
    }
}
