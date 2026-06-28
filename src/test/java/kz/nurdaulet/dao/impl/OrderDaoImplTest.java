package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.entity.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
    private static final Long ORDER_ID = 10L;
    private static final Long MISSING_ORDER_ID = 999L;
    private static final Long GENERATED_ORDER_ID = 42L;
    private static final Long USER_ID = 1L;
    private static final Long RESTAURANT_ID = 2L;
    private static final Double TOTAL_PRICE = 5000D;
    private static final String DELIVERY_ADDRESS = "Abay 1";
    private static final String ORDER_ID_COLUMN = "id";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String RESTAURANT_ID_COLUMN = "restaurant_id";
    private static final String STATUS_COLUMN = "status";
    private static final String DELIVERY_TYPE_COLUMN = "delivery_type";
    private static final String DELIVERY_ADDRESS_COLUMN = "delivery_address";
    private static final String TOTAL_PRICE_COLUMN = "total_price";
    private static final String CREATED_AT_COLUMN = "created_at";
    private static final String UPDATED_AT_COLUMN = "updated_at";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM orders WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM orders ORDER BY created_at DESC, id DESC";
    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC, id DESC";
    private static final String UPDATE_STATUS_QUERY = "UPDATE orders SET status = ?, updated_at = ? WHERE id = ?";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private OrderDaoImpl testingInstance;

    @Test
    void shouldFindOrderByIdAndMapRows() throws Exception {
        // given
        ArgumentCaptor<RowMapper<Order>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);
        Order order = createOrder();

        when(jdbcTemplate.query(eq(FIND_BY_ID_QUERY), mapperCaptor.capture(), eq(ORDER_ID)))
                .thenReturn(List.of(order));

        // when
        Order result = testingInstance.findById(ORDER_ID);

        // then
        assertEquals(order, result);
        verify(jdbcTemplate).query(eq(FIND_BY_ID_QUERY), any(RowMapper.class), eq(ORDER_ID));

        LocalDateTime now = LocalDateTime.now();
        when(resultSet.getLong(ORDER_ID_COLUMN)).thenReturn(ORDER_ID);
        when(resultSet.getLong(USER_ID_COLUMN)).thenReturn(USER_ID);
        when(resultSet.getLong(RESTAURANT_ID_COLUMN)).thenReturn(RESTAURANT_ID);
        when(resultSet.getString(STATUS_COLUMN)).thenReturn(OrderStatus.PREPARING.name());
        when(resultSet.getString(DELIVERY_TYPE_COLUMN)).thenReturn(DeliveryType.DELIVERY.name());
        when(resultSet.getString(DELIVERY_ADDRESS_COLUMN)).thenReturn(DELIVERY_ADDRESS);
        when(resultSet.getDouble(TOTAL_PRICE_COLUMN)).thenReturn(TOTAL_PRICE);
        when(resultSet.getObject(CREATED_AT_COLUMN, LocalDateTime.class)).thenReturn(now);
        when(resultSet.getObject(UPDATED_AT_COLUMN, LocalDateTime.class)).thenReturn(now);

        Order mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(ORDER_ID, mapped.getId());
        assertEquals(OrderStatus.PREPARING, mapped.getStatus());
        assertEquals(DeliveryType.DELIVERY, mapped.getDeliveryType());
        assertEquals(TOTAL_PRICE, mapped.getTotalPrice());
    }

    @Test
    void shouldReturnNullWhenOrderNotFound() {
        // given
        when(jdbcTemplate.query(eq(FIND_BY_ID_QUERY), any(RowMapper.class), eq(MISSING_ORDER_ID)))
                .thenReturn(List.of());

        // when
        Order result = testingInstance.findById(MISSING_ORDER_ID);

        // then
        assertNull(result);
        verify(jdbcTemplate).query(eq(FIND_BY_ID_QUERY), any(RowMapper.class), eq(MISSING_ORDER_ID));
    }

    @Test
    void shouldQueryOrderLists() {
        // when
        testingInstance.findAll();
        testingInstance.findByUserId(USER_ID);
        testingInstance.findPaidByRestaurantId(RESTAURANT_ID);

        // then
        verify(jdbcTemplate).query(eq(FIND_ALL_QUERY), any(RowMapper.class));
        verify(jdbcTemplate).query(
                eq(FIND_BY_USER_ID_QUERY),
                any(RowMapper.class),
                eq(USER_ID));
        verify(jdbcTemplate).query(any(String.class), any(RowMapper.class), eq(RESTAURANT_ID));
    }

    @Test
    void shouldUpdateStatus() {
        // when
        testingInstance.updateStatus(ORDER_ID, OrderStatus.READY);

        // then
        verify(jdbcTemplate).update(eq(UPDATE_STATUS_QUERY),
                eq(OrderStatus.READY.name()), any(LocalDateTime.class), eq(ORDER_ID));
    }

    @Test
    void shouldSaveOrderAndReturnGeneratedId() throws Exception {
        // given
        Order order = createOrder();
        ArgumentCaptor<PreparedStatementCreator> creatorCaptor =
                ArgumentCaptor.forClass(PreparedStatementCreator.class);

        when(connection.prepareStatement(any(String.class), eq(new String[]{ORDER_ID_COLUMN})))
                .thenReturn(preparedStatement);
        when(jdbcTemplate.update(creatorCaptor.capture(), any(KeyHolder.class))).thenAnswer(invocation -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Map.of(ORDER_ID_COLUMN, GENERATED_ORDER_ID));
            return 1;
        });

        // when
        Long result = testingInstance.save(order);
        PreparedStatement statement = creatorCaptor.getValue().createPreparedStatement(connection);

        // then
        assertEquals(GENERATED_ORDER_ID, result);
        assertEquals(preparedStatement, statement);
        verify(preparedStatement).setLong(1, USER_ID);
        verify(preparedStatement).setLong(2, RESTAURANT_ID);
        verify(preparedStatement).setString(3, OrderStatus.PREPARING.name());
        verify(preparedStatement).setString(4, DeliveryType.DELIVERY.name());
        verify(preparedStatement).setString(5, DELIVERY_ADDRESS);
        verify(preparedStatement).setDouble(6, TOTAL_PRICE);
        verify(preparedStatement).setObject(7, order.getCreatedAt());
        verify(preparedStatement).setObject(8, order.getUpdatedAt());
    }

    private Order createOrder() {
        LocalDateTime now = LocalDateTime.now();
        return new Order(
                ORDER_ID,
                USER_ID,
                RESTAURANT_ID,
                OrderStatus.PREPARING,
                DeliveryType.DELIVERY,
                DELIVERY_ADDRESS,
                TOTAL_PRICE,
                now,
                now
        );
    }
}
