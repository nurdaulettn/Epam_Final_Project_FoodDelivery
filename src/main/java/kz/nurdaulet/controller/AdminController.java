package kz.nurdaulet.controller;

import kz.nurdaulet.dto.PageDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.User;
import kz.nurdaulet.entity.enums.Role;
import kz.nurdaulet.service.OrderService;
import kz.nurdaulet.service.RestaurantService;
import kz.nurdaulet.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final int USERS_PAGE_SIZE = 8;

    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(RestaurantService restaurantService,
                           OrderService orderService,
                           UserService userService) {
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.userService = userService;
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

    @GetMapping("/users")
    public String users(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        Model model) {
        List<User> users = userService.getAllUsers();
        PageDto<User> userPage = PageDto.of(users, page, USERS_PAGE_SIZE);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("page", userPage);
        model.addAttribute("roles", Role.values());
        model.addAttribute("currentUserId", userDetails.getId());

        return "admin/users";
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

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable("id") Long id,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateStatus(userDetails.getId(), id, false);

        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unblock")
    public String unblockUser(@PathVariable("id") Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateStatus(userDetails.getId(), id, true);

        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteByAdmin(userDetails.getId(), id);

        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable("id") Long id,
                                 @RequestParam("role") Role role,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateRole(userDetails.getId(), id, role);

        return "redirect:/admin/users";
    }
}
