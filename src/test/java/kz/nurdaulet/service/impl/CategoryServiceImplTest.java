package kz.nurdaulet.service.impl;

import kz.nurdaulet.dao.CategoryDao;
import kz.nurdaulet.dto.CategoryCreateDto;
import kz.nurdaulet.entity.Category;
import kz.nurdaulet.exception.CategoryNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Burgers";
    private static final String SEARCH_TEXT = "bur";
    private static final String CATEGORY_NAME_WITH_SPACES = " Burgers ";

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl testingInstance;

    @Test
    void shouldGetAllCategories() {
        // given
        List<Category> categories = List.of(new Category(CATEGORY_ID, CATEGORY_NAME));
        when(categoryDao.findAll()).thenReturn(categories);

        // when
        List<Category> result = testingInstance.getAllCategories();

        // then
        assertEquals(categories, result);
        verify(categoryDao).findAll();
    }

    @Test
    void shouldSearchCategoryByName() {
        // given
        List<Category> categories = List.of(new Category(CATEGORY_ID, CATEGORY_NAME));
        when(categoryDao.findBySimilarName(SEARCH_TEXT)).thenReturn(categories);

        // when
        List<Category> result = testingInstance.searchCategoryByName(SEARCH_TEXT);

        // then
        assertEquals(categories, result);
        verify(categoryDao).findBySimilarName(SEARCH_TEXT);
    }

    @Test
    void shouldGetCategoryById() {
        // given
        Category category = new Category(CATEGORY_ID, CATEGORY_NAME);
        when(categoryDao.findById(CATEGORY_ID)).thenReturn(category);

        // when
        Category result = testingInstance.getCategoryById(CATEGORY_ID);

        // then
        assertEquals(category, result);
        verify(categoryDao).findById(CATEGORY_ID);
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        // given
        when(categoryDao.findById(CATEGORY_ID)).thenReturn(null);

        // when / then
        assertThrows(CategoryNotFoundException.class, () -> testingInstance.getCategoryById(CATEGORY_ID));
        verify(categoryDao).findById(CATEGORY_ID);
    }

    @Test
    void shouldCreateCategoryWithTrimmedName() {
        // given
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

        // when
        testingInstance.createCategory(new CategoryCreateDto(CATEGORY_NAME_WITH_SPACES));

        // then
        verify(categoryDao).save(captor.capture());
        assertEquals(CATEGORY_NAME, captor.getValue().getName());
    }
}
