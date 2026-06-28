package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Category;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryDaoImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Test
    void shouldFindAllCategoriesAndMapRows() throws Exception {
        CategoryDaoImpl dao = new CategoryDaoImpl(jdbcTemplate);
        ArgumentCaptor<RowMapper<Category>> mapperCaptor = ArgumentCaptor.forClass(RowMapper.class);

        when(jdbcTemplate.query(eq("SELECT * FROM categories"), mapperCaptor.capture()))
                .thenReturn(List.of(new Category(1L, "Burgers")));

        List<Category> result = dao.findAll();

        assertEquals(1, result.size());
        assertEquals("Burgers", result.get(0).getName());

        when(resultSet.getLong("id")).thenReturn(2L);
        when(resultSet.getString("name")).thenReturn("Pizza");

        Category mapped = mapperCaptor.getValue().mapRow(resultSet, 0);

        assertEquals(2L, mapped.getId());
        assertEquals("Pizza", mapped.getName());
    }

    @Test
    void shouldFindCategoryById() {
        CategoryDaoImpl dao = new CategoryDaoImpl(jdbcTemplate);
        Category category = new Category(1L, "Burgers");

        when(jdbcTemplate.query(eq("SELECT * FROM categories WHERE id = ?"), any(RowMapper.class), eq(1L)))
                .thenReturn(List.of(category));

        assertEquals(category, dao.findById(1L));
    }

    @Test
    void shouldReturnNullWhenCategoryByNameNotFound() {
        CategoryDaoImpl dao = new CategoryDaoImpl(jdbcTemplate);

        when(jdbcTemplate.query(eq("SELECT * FROM categories WHERE name = ?"), any(RowMapper.class), eq("Missing")))
                .thenReturn(List.of());

        assertNull(dao.findByName("Missing"));
    }

    @Test
    void shouldFindCategoriesBySimilarName() {
        CategoryDaoImpl dao = new CategoryDaoImpl(jdbcTemplate);

        dao.findBySimilarName("bur");

        verify(jdbcTemplate).query(
                eq("SELECT * FROM categories WHERE name ILIKE CONCAT('%', ?, '%')"),
                any(RowMapper.class),
                eq("bur"));
    }

    @Test
    void shouldSaveCategory() {
        CategoryDaoImpl dao = new CategoryDaoImpl(jdbcTemplate);

        dao.save(new Category(1L, "Burgers"));

        verify(jdbcTemplate).update("INSERT INTO categories (name) VALUES (?)", "Burgers");
    }
}
