package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.facade.ManagerFoodFacade;
import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.service.FoodService;
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
    private final FoodService foodService;
    private final ManagerFoodFacade  managerFoodFacade;

    public ManagerController(RestaurantService restaurantService, RestaurantValidator restaurantValidator, CategoryService categoryService, FoodService foodService, ManagerFoodFacade managerFoodFacade) {
        this.restaurantService = restaurantService;
        this.restaurantValidator = restaurantValidator;
        this.categoryService = categoryService;
        this.foodService = foodService;
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

    @GetMapping("/{restaurantId}/foods/{foodId}/update")
    public String updateFoodForm(@PathVariable("restaurantId") Long restaurantId,
                                 @PathVariable("foodId") Long foodId,
                                 Model model) {
        if (!model.containsAttribute("food")) {
            model.addAttribute("food", foodService.getFoodCreateDtoById(foodId));
        }

        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("category", categoryService.getAllCategories());

        return "food/update";
    }

    @PostMapping("/{restaurantId}/foods/{foodId}/update")
    public String updateFood(@PathVariable("restaurantId") Long restaurantId,
                             @PathVariable("foodId") Long foodId,
                             @Valid @ModelAttribute("food") FoodCreateDto foodCreateDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurantId", restaurantId);
            model.addAttribute("category", categoryService.getAllCategories());

            return "food/update";
        }

        managerFoodFacade.updateFood(userDetails.getId(), restaurantId, foodId, foodCreateDto);

        return "redirect:/restaurants/manager/my-restaurants/" + restaurantId;
    }

    @DeleteMapping("/{restaurantId}/foods/{foodId}")
    public String deleteFood(@PathVariable("restaurantId") Long restaurantId,
                             @PathVariable("foodId") Long foodId,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        managerFoodFacade.deleteFood(userDetails.getId(), restaurantId, foodId);

        return "redirect:/restaurants/manager/my-restaurants/" + restaurantId;
    }

    @GetMapping("/my-restaurants/{restaurantId}")
    public String restaurants(@PathVariable("restaurantId") Long restaurantId, Model model) {
        model.addAttribute("restaurants", restaurantService.getMyRestaurants(restaurantId));
        model.addAttribute("foods", foodService.getFoodByRestaurantId(restaurantId));

        return "restaurant/restaurantManage";
    }
}
