package kz.nurdaulet.controller;

import kz.nurdaulet.dto.PageDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.service.FavoriteFoodService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.RestaurantService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/foods")
public class FoodController {
    private static final int PAGE_SIZE = 6;

    private final FoodService foodService;
    private final CategoryService categoryService;
    private final RestaurantService restaurantService;
    private final FavoriteFoodService favoriteFoodService;

    public FoodController(FoodService foodService,
                          CategoryService categoryService,
                          RestaurantService restaurantService,
                          FavoriteFoodService favoriteFoodService) {
        this.foodService = foodService;
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
        this.favoriteFoodService = favoriteFoodService;
    }

    @GetMapping
    public String foods(@RequestParam(name = "category", required = false) Long categoryId,
                        @RequestParam(name = "restaurant" , required = false) Long restaurantId,
                        @RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model) {
        PageDto<Food> foodPage = PageDto.of(foodService.getFoods(search, categoryId, restaurantId), page, PAGE_SIZE);

        model.addAttribute("foods", foodPage.getContent());
        model.addAttribute("page", foodPage);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedRestaurantId", restaurantId);
        model.addAttribute("search", search);
        model.addAttribute("favoriteFoodIds", userDetails == null
                ? java.util.List.of()
                : favoriteFoodService.getFavoriteFoodIds(userDetails.getId()));

        return "food/foods";
    }

}
