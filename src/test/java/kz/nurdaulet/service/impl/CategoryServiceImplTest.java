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
    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl testingInstance;

    @Test
    void shouldGetAllCategories() {
        List<Category> categories = List.of(new Category(1L, "Burgers"));
        when(categoryDao.findAll()).thenReturn(categories);

        assertEquals(categories, testingInstance.getAllCategories());
    }

    @Test
    void shouldSearchCategoryByName() {
        List<Category> categories = List.of(new Category(1L, "Burgers"));
        when(categoryDao.findBySimilarName("bur")).thenReturn(categories);

        assertEquals(categories, testingInstance.searchCategoryByName("bur"));
    }

    @Test
    void shouldGetCategoryById() {
        Category category = new Category(1L, "Burgers");
        when(categoryDao.findById(1L)).thenReturn(category);

        assertEquals(category, testingInstance.getCategoryById(1L));
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        when(categoryDao.findById(1L)).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () -> testingInstance.getCategoryById(1L));
    }

    @Test
    void shouldCreateCategoryWithTrimmedName() {
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

        testingInstance.createCategory(new CategoryCreateDto(" Burgers "));

        verify(categoryDao).save(captor.capture());
        assertEquals("Burgers", captor.getValue().getName());
    }
}
