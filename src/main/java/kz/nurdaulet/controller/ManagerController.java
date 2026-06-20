package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.facade.ManagerFoodFacade;
import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.service.RestaurantService;
import kz.nurdaulet.validation.RestaurantValidator;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/restaurants/manager")
public class ManagerController {
    private final RestaurantService restaurantService;
    private final RestaurantValidator restaurantValidator;
    private final CategoryService categoryService;
    private final ManagerFoodFacade  managerFoodFacade;

    public ManagerController(RestaurantService restaurantService, RestaurantValidator restaurantValidator, CategoryService categoryService, ManagerFoodFacade managerFoodFacade) {
        this.restaurantService = restaurantService;
        this.restaurantValidator = restaurantValidator;
        this.categoryService = categoryService;
        this.managerFoodFacade = managerFoodFacade;
    }

    @GetMapping("/my-restaurants")
    public String restaurants(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("restaurants", restaurantService.getMyRestaurants(userDetails.getId()));

        return "restaurant/my-restaurants";
    }


    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("restaurant")) {
            model.addAttribute("restaurant", new RestaurantCreateDto());
        }

        return "restaurant/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("restaurant") RestaurantCreateDto restaurantCreateDto,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        restaurantValidator.validate(restaurantCreateDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "restaurant/create";
        }

        restaurantService.create(restaurantCreateDto, userDetails.getId());

        return "redirect:/restaurants/manager/my-restaurants";
    }

    @GetMapping("/{restaurantId}/foods/create")
    public String createFoodForm(@PathVariable("restaurantId") Long restaurantId,
                             Model model) {
        if (!model.containsAttribute("food")) {
            model.addAttribute("food", new FoodCreateDto());
        }

        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("category", categoryService.getAllCategories());

        return "food/create";
    }

    @PostMapping("/{restaurantId}/foods/create")
    public String createFood(@PathVariable("restaurantId") Long restaurantId,
                             @Valid @ModelAttribute("food") FoodCreateDto foodCreateDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurantId", restaurantId);
            model.addAttribute("category", categoryService.getAllCategories());

            return "food/create";
        }

        managerFoodFacade.createFood(userDetails.getId(), restaurantId, foodCreateDto);

        return "redirect:/restaurants/manager/my-restaurants";
    }
}
