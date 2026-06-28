package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.entity.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Test
    void shouldFindOrderByIdAndMapRows() throws Exception {
        OrderDaoImpl dao = new OrderDaoImpl(jdbcTemplate);
        ArgumentCaptor<RowMapper<Order>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);
        Order order = createOrder();

        when(jdbcTemplate.query(eq("SELECT * FROM orders WHERE id = ?"), mapperCaptor.capture(), eq(10L)))
                .thenReturn(List.of(order));

        assertEquals(order, dao.findById(10L));

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getLong("id")).thenReturn(10L);
        when(resultSet.getLong("user_id")).thenReturn(1L);
        when(resultSet.getLong("restaurant_id")).thenReturn(2L);
        when(resultSet.getString("status")).thenReturn("PREPARING");
        when(resultSet.getString("delivery_type")).thenReturn("DELIVERY");
        when(resultSet.getString("delivery_address")).thenReturn("Abay 1");
        when(resultSet.getDouble("total_price")).thenReturn(5000D);
        when(resultSet.getObject("created_at", LocalDateTime.class)).thenReturn(now);
        when(resultSet.getObject("updated_at", LocalDateTime.class)).thenReturn(now);

        Order mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(10L, mapped.getId());
        assertEquals(OrderStatus.PREPARING, mapped.getStatus());
        assertEquals(DeliveryType.DELIVERY, mapped.getDeliveryType());
        assertEquals(5000D, mapped.getTotalPrice());
    }

    @Test
    void shouldReturnNullWhenOrderNotFound() {
        OrderDaoImpl dao = new OrderDaoImpl(jdbcTemplate);

        when(jdbcTemplate.query(eq("SELECT * FROM orders WHERE id = ?"), any(RowMapper.class), eq(999L)))
                .thenReturn(List.of());

        assertNull(dao.findById(999L));
    }

    @Test
    void shouldQueryOrderLists() {
        OrderDaoImpl dao = new OrderDaoImpl(jdbcTemplate);

        dao.findAll();
        dao.findByUserId(1L);
        dao.findPaidByRestaurantId(2L);

        verify(jdbcTemplate).query(eq("SELECT * FROM orders ORDER BY created_at DESC, id DESC"), any(RowMapper.class));
        verify(jdbcTemplate).query(
                eq("SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC, id DESC"),
                any(RowMapper.class),
                eq(1L));
        verify(jdbcTemplate).query(any(String.class), any(RowMapper.class), eq(2L));
    }

    @Test
    void shouldUpdateStatus() {
        OrderDaoImpl dao = new OrderDaoImpl(jdbcTemplate);

        dao.updateStatus(10L, OrderStatus.READY);

        verify(jdbcTemplate).update(eq("UPDATE orders SET status = ?, updated_at = ? WHERE id = ?"),
                eq("READY"), any(LocalDateTime.class), eq(10L));
    }

    @Test
    void shouldSaveOrderAndReturnGeneratedId() throws Exception {
        OrderDaoImpl dao = new OrderDaoImpl(jdbcTemplate);
        Order order = createOrder();
        ArgumentCaptor<PreparedStatementCreator> creatorCaptor =
                ArgumentCaptor.forClass(PreparedStatementCreator.class);

        when(connection.prepareStatement(any(String.class), eq(new String[]{"id"})))
                .thenReturn(preparedStatement);
        when(jdbcTemplate.update(creatorCaptor.capture(), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Map.of("id", 42L));
            return 1;
        });

        Long result = dao.save(order);
        PreparedStatement statement = creatorCaptor.getValue().createPreparedStatement(connection);

        assertEquals(42L, result);
        assertEquals(preparedStatement, statement);
        verify(preparedStatement).setLong(1, 1L);
        verify(preparedStatement).setLong(2, 2L);
        verify(preparedStatement).setString(3, "PREPARING");
        verify(preparedStatement).setString(4, "DELIVERY");
        verify(preparedStatement).setString(5, "Abay 1");
        verify(preparedStatement).setDouble(6, 5000D);
        verify(preparedStatement).setObject(7, order.getCreatedAt());
        verify(preparedStatement).setObject(8, order.getUpdatedAt());
    }

    private Order createOrder() {
        LocalDateTime now = LocalDateTime.now();
        return new Order(
                10L,
                1L,
                2L,
                OrderStatus.PREPARING,
                DeliveryType.DELIVERY,
                "Abay 1",
                5000D,
                now,
                now
        );
    }
}
