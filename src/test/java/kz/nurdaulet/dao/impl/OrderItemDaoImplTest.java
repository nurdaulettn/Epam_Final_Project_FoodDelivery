package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Test
    void shouldFindOrderItemsAndMapRows() throws Exception {
        OrderItemDaoImpl dao = new OrderItemDaoImpl(jdbcTemplate);
        ArgumentCaptor<RowMapper<OrderItem>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(eq("SELECT * FROM order_items WHERE order_id = ?"), mapperCaptor.capture(), eq(10L)))
                .thenReturn(List.of(new OrderItem(1L, 10L, 100L, 2, 2500D)));

        List<OrderItem> result = dao.findByOrderId(10L);

        assertEquals(1, result.size());

        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getLong("order_id")).thenReturn(10L);
        when(resultSet.getLong("food_id")).thenReturn(100L);
        when(resultSet.getInt("quantity")).thenReturn(2);
        when(resultSet.getDouble("price")).thenReturn(2500D);

        OrderItem mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(1L, mapped.getId());
        assertEquals(10L, mapped.getOrderId());
        assertEquals(100L, mapped.getFoodId());
        assertEquals(2, mapped.getQuantity());
        assertEquals(2500D, mapped.getPrice());
    }

    @Test
    void shouldSaveOrderItem() {
        OrderItemDaoImpl dao = new OrderItemDaoImpl(jdbcTemplate);

        dao.save(new OrderItem(1L, 10L, 100L, 2, 2500D));

        verify(jdbcTemplate).update(any(String.class), eq(10L), eq(100L), eq(2), eq(2500D));
    }
}
