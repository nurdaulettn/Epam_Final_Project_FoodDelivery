package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dao.RestaurantDao;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Restaurant;
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
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final RestaurantValidator restaurantValidator;

    public RestaurantController(RestaurantService restaurantService, RestaurantValidator restaurantValidator) {
        this.restaurantService = restaurantService;
        this.restaurantValidator = restaurantValidator;
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

        return "redirect:/restaurants";
    }
}
