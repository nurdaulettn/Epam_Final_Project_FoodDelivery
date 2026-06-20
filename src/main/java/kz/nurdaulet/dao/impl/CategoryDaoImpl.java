package kz.nurdaulet.dao.impl;

import kz.nurdaulet.dao.CategoryDao;
import kz.nurdaulet.dto.CategoryCreateDto;
import kz.nurdaulet.entity.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryDaoImpl implements CategoryDao {
    private static final String FIND_ALL = "SELECT * FROM categories";
    private static final String FIND_BY_ID = "SELECT * FROM categories WHERE id = ?";
    private static final String FIND_BY_SIMILAR_NAME = "SELECT * FROM categories WHERE name ILIKE CONCAT('%', ?, '%')";
    private static final String FIND_BY_NAME =  "SELECT * FROM categories WHERE name = ?";
    private static final String SAVE =  "INSERT INTO categories (name) VALUES (?)";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Category> mapper = (rs, rowNum) -> {
        return new Category(
                rs.getLong("id"),
                rs.getString("name")
        );
    };

    public CategoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Category> findAll() {
        return jdbcTemplate.query(FIND_ALL, mapper);
    }

    @Override
    public List<Category> findBySimilarName(String name) {
        return jdbcTemplate.query(FIND_BY_SIMILAR_NAME, mapper, name);
    }

    @Override
    public Category findById(Long id) {
        return jdbcTemplate.query(FIND_BY_ID, mapper, id)
                .stream().findFirst().orElse(null);
    }

    @Override
    public Category findByName(String name) {
        return jdbcTemplate.query(FIND_BY_NAME, mapper, name)
                .stream().findFirst().orElse(null);
    }

    @Override
    public void save(Category category) {
        jdbcTemplate.update(SAVE, category.getName());
    }
}
