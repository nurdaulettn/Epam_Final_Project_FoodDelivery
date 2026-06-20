package kz.nurdaulet.controller;

import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String getRestaurants(Model model, @RequestParam(required = false, name = "search") String search) {
        List<Restaurant> restaurants;

        if (search != null && !search.isBlank()) {
            restaurants = restaurantService.searchRestaurantsByName(search);
        } else {
            restaurants = restaurantService.getAllRestaurants();
        }

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("search", search);

        return "restaurant/allRestaurants";
    }
}
