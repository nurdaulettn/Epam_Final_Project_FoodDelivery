package kz.nurdaulet.controller;

import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/foods")
public class FoodController {
    private final FoodService foodService;
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;

    public FoodController(FoodService foodService, CategoryService categoryService, RestaurantService restaurantService) {
        this.foodService = foodService;
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String foods(@RequestParam(name = "category", required = false) Long categoryId,
                        @RequestParam(name = "restaurant" , required = false) Long restaurantId,
                        @RequestParam(name = "search", required = false) String search,
                        Model model) {
        model.addAttribute("foods", foodService.getFoods(search, categoryId, restaurantId));

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedRestaurantId", restaurantId);
        model.addAttribute("search", search);

        return "food/foods";
    }

}
