package kz.nurdaulet.controller;

import kz.nurdaulet.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final RestaurantService restaurantService;

    public AdminController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/create-requests")
    public String createRequests(Model model) {
        model.addAttribute("restaurants",
                restaurantService.getAllNotConfirmedRestaurants());

        return "admin/create-requests";
    }
}
