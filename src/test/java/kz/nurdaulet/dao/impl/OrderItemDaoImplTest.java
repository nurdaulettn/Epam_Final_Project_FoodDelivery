package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemDaoImplTest {
    private static final Long ORDER_ITEM_ID = 1L;
    private static final Long ORDER_ID = 10L;
    private static final Long FOOD_ID = 100L;
    private static final Integer QUANTITY = 2;
    private static final Double PRICE = 2500D;
    private static final String FIND_BY_ORDER_ID_QUERY = "SELECT * FROM order_items WHERE order_id = ?";
    private static final String ID_COLUMN = "id";
    private static final String ORDER_ID_COLUMN = "order_id";
    private static final String FOOD_ID_COLUMN = "food_id";
    private static final String QUANTITY_COLUMN = "quantity";
    private static final String PRICE_COLUMN = "price";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private OrderItemDaoImpl testingInstance;

    @Test
    void shouldFindOrderItemsAndMapRows() throws Exception {
        // given
        ArgumentCaptor<RowMapper<OrderItem>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(eq(FIND_BY_ORDER_ID_QUERY), mapperCaptor.capture(), eq(ORDER_ID)))
                .thenReturn(List.of(new OrderItem(ORDER_ITEM_ID, ORDER_ID, FOOD_ID, QUANTITY, PRICE)));

        // when
        List<OrderItem> result = testingInstance.findByOrderId(ORDER_ID);

        // then
        assertEquals(1, result.size());
        verify(jdbcTemplate).query(eq(FIND_BY_ORDER_ID_QUERY), any(RowMapper.class), eq(ORDER_ID));

        when(resultSet.getLong(ID_COLUMN)).thenReturn(ORDER_ITEM_ID);
        when(resultSet.getLong(ORDER_ID_COLUMN)).thenReturn(ORDER_ID);
        when(resultSet.getLong(FOOD_ID_COLUMN)).thenReturn(FOOD_ID);
        when(resultSet.getInt(QUANTITY_COLUMN)).thenReturn(QUANTITY);
        when(resultSet.getDouble(PRICE_COLUMN)).thenReturn(PRICE);

        OrderItem mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(ORDER_ITEM_ID, mapped.getId());
        assertEquals(ORDER_ID, mapped.getOrderId());
        assertEquals(FOOD_ID, mapped.getFoodId());
        assertEquals(QUANTITY, mapped.getQuantity());
        assertEquals(PRICE, mapped.getPrice());
    }

    @Test
    void shouldSaveOrderItem() {
        // given
        OrderItem orderItem = new OrderItem(ORDER_ITEM_ID, ORDER_ID, FOOD_ID, QUANTITY, PRICE);

        // when
        testingInstance.save(orderItem);

        // then
        verify(jdbcTemplate).update(any(String.class), eq(ORDER_ID), eq(FOOD_ID), eq(QUANTITY), eq(PRICE));
    }
}
