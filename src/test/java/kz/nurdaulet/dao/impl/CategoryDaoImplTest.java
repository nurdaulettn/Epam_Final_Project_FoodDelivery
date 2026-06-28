package kz.nurdaulet.dao.impl;

import kz.nurdaulet.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
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
    private static final Long CATEGORY_ID = 1L;
    private static final Long MAPPED_CATEGORY_ID = 2L;
    private static final String GET_ALL_CATEGORIES_QUERY = "SELECT * FROM categories";
    private static final String BURGERS = "Burgers";
    private static final String PIZZA = "Pizza";
    private static final String MISSING_CATEGORY = "Missing";
    private static final String SEARCH_TEXT = "bur";
    private static final String ID_COLUMN = "id";
    private static final String NAME_COLUMN = "name";
    private static final String GET_CATEGORY_BY_ID = "SELECT * FROM categories WHERE id = ?";
    private static final String GET_CATEGORY_BY_NAME = "SELECT * FROM categories WHERE name = ?";
    private static final String FIND_BY_SIMILAR_NAME_QUERY = "SELECT * FROM categories WHERE name ILIKE CONCAT('%', ?, '%')";
    private static final String SAVE_CATEGORY_QUERY = "INSERT INTO categories (name) VALUES (?)";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Captor
    ArgumentCaptor<RowMapper<Category>> categoryCaptor;

    @InjectMocks
    CategoryDaoImpl testingInstance;


    @Test
    void shouldFindAllCategoriesAndMapRows() throws Exception {
        // given
        when(jdbcTemplate.query(eq(GET_ALL_CATEGORIES_QUERY), any(RowMapper.class)))
                .thenReturn(List.of(new Category(CATEGORY_ID, BURGERS)));
        when(resultSet.getLong(ID_COLUMN)).thenReturn(MAPPED_CATEGORY_ID);
        when(resultSet.getString(NAME_COLUMN)).thenReturn(PIZZA);

        // when
        List<Category> result = testingInstance.findAll();

        // then
        verify(jdbcTemplate).query(eq(GET_ALL_CATEGORIES_QUERY), categoryCaptor.capture());
        assertEquals(1, result.size());
        assertEquals(BURGERS, result.get(0).getName());

        Category mappedCategory = categoryCaptor.getValue().mapRow(resultSet, 0);
        assertEquals(MAPPED_CATEGORY_ID, mappedCategory.getId());
        assertEquals(PIZZA, mappedCategory.getName());
    }

    @Test
    void shouldFindCategoryById() {
        // given
        Category category = new Category(CATEGORY_ID, BURGERS);

        when(jdbcTemplate.query(eq(GET_CATEGORY_BY_ID), any(RowMapper.class), eq(CATEGORY_ID)))
                .thenReturn(List.of(category));

        // when
        Category result = testingInstance.findById(CATEGORY_ID);

        // then
        assertEquals(category, result);
        verify(jdbcTemplate).query(eq(GET_CATEGORY_BY_ID), any(RowMapper.class), eq(CATEGORY_ID));
    }

    @Test
    void shouldReturnNullWhenCategoryByNameNotFound() {
        // given
        when(jdbcTemplate.query(eq(GET_CATEGORY_BY_NAME), any(RowMapper.class), eq(MISSING_CATEGORY)))
                .thenReturn(List.of());

        // when
        Category result = testingInstance.findByName(MISSING_CATEGORY);

        // then
        assertNull(result);
        verify(jdbcTemplate).query(eq(GET_CATEGORY_BY_NAME), any(RowMapper.class), eq(MISSING_CATEGORY));
    }

    @Test
    void shouldFindCategoriesBySimilarName() {
        // when
        testingInstance.findBySimilarName(SEARCH_TEXT);

        // then
        verify(jdbcTemplate).query(
                eq(FIND_BY_SIMILAR_NAME_QUERY),
                any(RowMapper.class),
                eq(SEARCH_TEXT));
    }

    @Test
    void shouldSaveCategory() {
        // when
        testingInstance.save(new Category(CATEGORY_ID, BURGERS));

        // then
        verify(jdbcTemplate).update(SAVE_CATEGORY_QUERY, BURGERS);
    }
}
