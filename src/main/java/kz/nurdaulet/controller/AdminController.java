package kz.nurdaulet.controller;

import kz.nurdaulet.service.OrderService;
import kz.nurdaulet.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final RestaurantService restaurantService;
    private final OrderService orderService;

    public AdminController(RestaurantService restaurantService,
                           OrderService orderService) {
        this.restaurantService = restaurantService;
        this.orderService = orderService;
    }

    @GetMapping("/create-requests")
    public String createRequests(Model model) {
        model.addAttribute("restaurants",
                restaurantService.getPendingRestaurants());

        return "admin/create-requests";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAdminOrders());

        return "admin/orders";
    }

    @PostMapping("/create-requests/{id}/confirm")
    public String createRequestConfirm(@PathVariable("id") Long id) {
        restaurantService.confirmRestaurant(id);

        return "redirect:/admin/create-requests";
    }

    @PostMapping("/create-requests/{id}/reject")
    public String createRequestReject(@PathVariable("id") Long id) {
        restaurantService.rejectRestaurant(id);

        return "redirect:/admin/create-requests";
    }
}
