package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.dto.PageDto;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.enums.OrderStatus;
import kz.nurdaulet.exception.OrderOperationException;
import kz.nurdaulet.facade.ManagerFoodFacade;
import kz.nurdaulet.service.CategoryService;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.OrderService;
import kz.nurdaulet.service.RestaurantService;
import kz.nurdaulet.validation.RestaurantValidator;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/restaurants/manager")
public class ManagerController {
    private static final int ORDER_PAGE_SIZE = 5;

    private final RestaurantService restaurantService;
    private final RestaurantValidator restaurantValidator;
    private final CategoryService categoryService;
    private final FoodService foodService;
    private final OrderService orderService;
    private final ManagerFoodFacade  managerFoodFacade;

    public ManagerController(RestaurantService restaurantService,
                             RestaurantValidator restaurantValidator,
                             CategoryService categoryService,
                             FoodService foodService,
                             OrderService orderService,
                             ManagerFoodFacade managerFoodFacade) {
        this.restaurantService = restaurantService;
        this.restaurantValidator = restaurantValidator;
        this.categoryService = categoryService;
        this.foodService = foodService;
        this.orderService = orderService;
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

    @PostMapping("/{restaurantId}/foods/{foodId}/delete")
    public String deleteFood(@PathVariable("restaurantId") Long restaurantId,
                             @PathVariable("foodId") Long foodId,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        managerFoodFacade.deleteFood(userDetails.getId(), restaurantId, foodId);

        return "redirect:/restaurants/manager/my-restaurants/" + restaurantId;
    }

    @PostMapping("/{restaurantId}/foods/{foodId}/disable")
    public String disableFood(@PathVariable("restaurantId") Long restaurantId,
                              @PathVariable("foodId") Long foodId,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        managerFoodFacade.disableFood(userDetails.getId(), restaurantId, foodId);

        return "redirect:/restaurants/manager/my-restaurants/" + restaurantId;
    }

    @PostMapping("/{restaurantId}/foods/{foodId}/enable")
    public String enableFood(@PathVariable("restaurantId") Long restaurantId,
                              @PathVariable("foodId") Long foodId,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        managerFoodFacade.enableFood(userDetails.getId(), restaurantId, foodId);

        return "redirect:/restaurants/manager/my-restaurants/" + restaurantId;
    }


    @GetMapping("/my-restaurants/{restaurantId}")
    public String restaurants(@PathVariable("restaurantId") Long restaurantId,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {
        managerFoodFacade.checkManagerAndRestaurant(userDetails.getId(), restaurantId);

        model.addAttribute("restaurants", restaurantService.getMyRestaurants(userDetails.getId()));
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("foods", foodService.getFoodByRestaurantIdForManager(restaurantId));

        return "restaurant/restaurantManage";
    }

    @GetMapping("/{restaurantId}/orders")
    public String restaurantOrders(@PathVariable("restaurantId") Long restaurantId,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   @RequestParam(name = "page", defaultValue = "1") int page,
                                   Model model) {
        PageDto<Order> orderPage = PageDto.of(
                orderService.getManagerOrders(userDetails.getId(), restaurantId),
                page,
                ORDER_PAGE_SIZE
        );

        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId));
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("page", orderPage);

        return "order/manager-orders";
    }

    @GetMapping("/{restaurantId}/orders/{orderId}")
    public String restaurantOrderDetails(@PathVariable("restaurantId") Long restaurantId,
                                         @PathVariable("orderId") Long orderId,
                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                         Model model) {
        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId));
        model.addAttribute("order", orderService.getManagerOrder(userDetails.getId(), restaurantId, orderId));
        model.addAttribute("orderItems", orderService.getManagerOrderItems(userDetails.getId(), restaurantId, orderId));

        return "order/manager-order-details";
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/status")
    public String updateOrderStatus(@PathVariable("restaurantId") Long restaurantId,
                                    @PathVariable("orderId") Long orderId,
                                    @RequestParam("status") OrderStatus status,
                                    @RequestParam(value = "redirectToDetails", defaultValue = "false") boolean redirectToDetails,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateManagerOrderStatus(userDetails.getId(), restaurantId, orderId, status);
            redirectAttributes.addFlashAttribute("orderSuccess", "Статус заказа обновлён");
        } catch (OrderOperationException exception) {
            redirectAttributes.addFlashAttribute("orderError", exception.getMessage());
        }

        if (redirectToDetails) {
            return "redirect:/restaurants/manager/" + restaurantId + "/orders/" + orderId;
        }

        return "redirect:/restaurants/manager/" + restaurantId + "/orders";
    }
}
