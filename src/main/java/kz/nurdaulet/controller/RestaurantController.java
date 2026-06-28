package kz.nurdaulet.controller;

import kz.nurdaulet.dto.PageDto;
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
    private static final int PAGE_SIZE = 6;

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String getRestaurants(Model model,
                                 @RequestParam(required = false, name = "search") String search,
                                 @RequestParam(name = "page", defaultValue = "1") int page) {
        List<Restaurant> restaurants;

        if (search != null && !search.isBlank()) {
            restaurants = restaurantService.searchRestaurantsByName(search);
        } else {
            restaurants = restaurantService.getAllRestaurants();
        }

        PageDto<Restaurant> restaurantPage = PageDto.of(restaurants, page, PAGE_SIZE);

        model.addAttribute("restaurants", restaurantPage.getContent());
        model.addAttribute("page", restaurantPage);
        model.addAttribute("search", search);

        return "restaurant/allRestaurants";
    }
}
