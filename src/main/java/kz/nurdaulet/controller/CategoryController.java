package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.CategoryCreateDto;
import kz.nurdaulet.entity.Category;
import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.validation.CategoryValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryValidator categoryValidator;

    public CategoryController(CategoryService categoryService, CategoryValidator categoryValidator) {
        this.categoryService = categoryService;
        this.categoryValidator = categoryValidator;
    }

    @GetMapping("/categories")
    public String getCategories(Model model,
                                @RequestParam(required = false, name = "search") String search) {
        List<Category> categories;

        if (search != null && !search.isBlank()) {
            categories = categoryService.searchCategoryByName(search);
        } else {
            categories = categoryService.getAllCategories();
        }

        model.addAttribute("categories", categories);
        model.addAttribute("search", search);

        return "category/categories";
    }

    @GetMapping("/admin/categories/create")
    public String createCategory(Model model) {
        if (!model.containsAttribute("category")) {
            model.addAttribute("category", new CategoryCreateDto());
        }

        return "category/createCategory";
    }

    @PostMapping("/admin/categories/create")
    public String createCategory(@Valid @ModelAttribute("category") CategoryCreateDto categoryDto,
                                 BindingResult bindingResult) {
        categoryValidator.validate(categoryDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "category/createCategory";
        }

        categoryService.createCategory(categoryDto);

        return "redirect:/categories";
    }
}
