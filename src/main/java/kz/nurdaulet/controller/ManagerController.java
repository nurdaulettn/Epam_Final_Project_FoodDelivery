package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.service.RestaurantService;
import kz.nurdaulet.validation.RestaurantValidator;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/restaurants/manager")
public class ManagerController {
    private final RestaurantService restaurantService;
    private final RestaurantValidator restaurantValidator;

    public ManagerController(RestaurantService restaurantService, RestaurantValidator restaurantValidator) {
        this.restaurantService = restaurantService;
        this.restaurantValidator = restaurantValidator;
    }

    @GetMapping("/my-restaurants")
    public String restaurants(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("restaurants", restaurantService.getMyRestaurants(userDetails.getId()));

        return "restaurants/my-restaurants";
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
                         BindingResult bindingResult, Model model,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        restaurantValidator.validate(restaurantCreateDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "restaurant/create";
        }

        restaurantService.create(restaurantCreateDto, userDetails.getId());

        return "redirect:/restaurants/my-restaurants";
    }
}
