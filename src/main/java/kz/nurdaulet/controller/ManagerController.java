package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.FoodCreateDto;
import kz.nurdaulet.dto.PageDto;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Order;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/restaurants/manager")
public class ManagerController {
    private static final int ORDER_PAGE_SIZE = 5;
    private static final String RESTAURANT_ATTRIBUTE = "restaurant";
    private static final String RESTAURANTS_ATTRIBUTE = "restaurants";
    private static final String RESTAURANT_ID_ATTRIBUTE = "restaurantId";
    private static final String FOOD_ATTRIBUTE = "food";
    private static final String FOODS_ATTRIBUTE = "foods";
    private static final String CATEGORY_ATTRIBUTE = "category";
    private static final String ORDERS_ATTRIBUTE = "orders";
    private static final String ORDER_ATTRIBUTE = "order";
    private static final String ORDER_ITEMS_ATTRIBUTE = "orderItems";
    private static final String PAGE_ATTRIBUTE = "page";
    private static final String ORDER_SUCCESS_ATTRIBUTE = "orderSuccess";
    private static final String ORDER_ERROR_ATTRIBUTE = "orderError";
    private static final String RESTAURANT_CREATE_VIEW = "restaurant/create";
    private static final String MY_RESTAURANTS_VIEW = "restaurant/my-restaurants";
    private static final String RESTAURANT_MANAGE_VIEW = "restaurant/restaurantManage";
    private static final String FOOD_CREATE_VIEW = "food/create";
    private static final String FOOD_UPDATE_VIEW = "food/update";
    private static final String MANAGER_ORDERS_VIEW = "order/manager-orders";
    private static final String MANAGER_ORDER_DETAILS_VIEW = "order/manager-order-details";
    private static final String REDIRECT_MY_RESTAURANTS = "redirect:/restaurants/manager/my-restaurants";
    private static final String REDIRECT_RESTAURANT_MANAGE = "redirect:/restaurants/manager/my-restaurants/";
    private static final String REDIRECT_MANAGER_ORDERS = "redirect:/restaurants/manager/%d/orders";
    private static final String REDIRECT_MANAGER_ORDER_DETAILS = "redirect:/restaurants/manager/%d/orders/%d";
    private static final String ORDER_STATUS_UPDATED_MESSAGE = "Order status updated";

    private final RestaurantService restaurantService;
    private final RestaurantValidator restaurantValidator;
    private final CategoryService categoryService;
    private final FoodService foodService;
    private final OrderService orderService;
    private final ManagerFoodFacade managerFoodFacade;

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
    public String getRestaurants(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute(RESTAURANTS_ATTRIBUTE, restaurantService.getMyRestaurants(userDetails.getId()));

        return MY_RESTAURANTS_VIEW;
    }

    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute(RESTAURANT_ATTRIBUTE)) {
            model.addAttribute(RESTAURANT_ATTRIBUTE, new RestaurantCreateDto());
        }

        return RESTAURANT_CREATE_VIEW;
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute(RESTAURANT_ATTRIBUTE) RestaurantCreateDto restaurantCreateDto,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        restaurantValidator.validate(restaurantCreateDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return RESTAURANT_CREATE_VIEW;
        }

        restaurantService.create(restaurantCreateDto, userDetails.getId());

        return REDIRECT_MY_RESTAURANTS;
    }

    @GetMapping("/{restaurantId}/foods/create")
    public String createFoodForm(@PathVariable("restaurantId") Long restaurantId,
                                 Model model) {
        if (!model.containsAttribute(FOOD_ATTRIBUTE)) {
            model.addAttribute(FOOD_ATTRIBUTE, new FoodCreateDto());
        }

        addFoodFormAttributes(model, restaurantId);

        return FOOD_CREATE_VIEW;
    }

    @PostMapping("/{restaurantId}/foods/create")
    public String createFood(@PathVariable("restaurantId") Long restaurantId,
                             @Valid @ModelAttribute(FOOD_ATTRIBUTE) FoodCreateDto foodCreateDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {

        if (bindingResult.hasErrors()) {
            addFoodFormAttributes(model, restaurantId);

            return FOOD_CREATE_VIEW;
        }

        managerFoodFacade.createFood(userDetails.getId(), restaurantId, foodCreateDto);

        return REDIRECT_MY_RESTAURANTS;
    }

    @GetMapping("/{restaurantId}/foods/{foodId}/update")
    public String getUpdateFoodForm(@PathVariable("restaurantId") Long restaurantId,
                                    @PathVariable("foodId") Long foodId,
                                    Model model) {
        if (!model.containsAttribute(FOOD_ATTRIBUTE)) {
            model.addAttribute(FOOD_ATTRIBUTE, foodService.getFoodCreateDtoById(foodId));
        }

        addFoodFormAttributes(model, restaurantId);

        return FOOD_UPDATE_VIEW;
    }

    @PostMapping("/{restaurantId}/foods/{foodId}/update")
    public String updateFood(@PathVariable("restaurantId") Long restaurantId,
                             @PathVariable("foodId") Long foodId,
                             @Valid @ModelAttribute(FOOD_ATTRIBUTE) FoodCreateDto foodCreateDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        if (bindingResult.hasErrors()) {
            addFoodFormAttributes(model, restaurantId);

            return FOOD_UPDATE_VIEW;
        }

        managerFoodFacade.updateFood(userDetails.getId(), restaurantId, foodId, foodCreateDto);

        return REDIRECT_RESTAURANT_MANAGE + restaurantId;
    }

    @PostMapping("/{restaurantId}/foods/{foodId}/delete")
    public String deleteFood(@PathVariable("restaurantId") Long restaurantId,
                             @PathVariable("foodId") Long foodId,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        managerFoodFacade.deleteFood(userDetails.getId(), restaurantId, foodId);

        return REDIRECT_RESTAURANT_MANAGE + restaurantId;
    }

    @PostMapping("/{restaurantId}/foods/{foodId}/disable")
    public String disableFood(@PathVariable("restaurantId") Long restaurantId,
                              @PathVariable("foodId") Long foodId,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        managerFoodFacade.disableFood(userDetails.getId(), restaurantId, foodId);

        return REDIRECT_RESTAURANT_MANAGE + restaurantId;
    }

    @PostMapping("/{restaurantId}/foods/{foodId}/enable")
    public String enableFood(@PathVariable("restaurantId") Long restaurantId,
                             @PathVariable("foodId") Long foodId,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        managerFoodFacade.enableFood(userDetails.getId(), restaurantId, foodId);

        return REDIRECT_RESTAURANT_MANAGE + restaurantId;
    }

    @GetMapping("/my-restaurants/{restaurantId}")
    public String getRestaurants(@PathVariable("restaurantId") Long restaurantId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {
        managerFoodFacade.checkManagerAndRestaurant(userDetails.getId(), restaurantId);

        model.addAttribute(RESTAURANTS_ATTRIBUTE, restaurantService.getMyRestaurants(userDetails.getId()));
        model.addAttribute(RESTAURANT_ID_ATTRIBUTE, restaurantId);
        model.addAttribute(FOODS_ATTRIBUTE, foodService.getFoodByRestaurantIdForManager(restaurantId));

        return RESTAURANT_MANAGE_VIEW;
    }

    @GetMapping("/{restaurantId}/orders")
    public String getRestaurantOrders(@PathVariable("restaurantId") Long restaurantId,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                      Model model) {
        PageDto<Order> orderPage = PageDto.of(
                orderService.getManagerOrders(userDetails.getId(), restaurantId),
                page,
                ORDER_PAGE_SIZE
        );

        model.addAttribute(RESTAURANT_ATTRIBUTE, restaurantService.getRestaurantById(restaurantId));
        model.addAttribute(ORDERS_ATTRIBUTE, orderPage.getContent());
        model.addAttribute(PAGE_ATTRIBUTE, orderPage);

        return MANAGER_ORDERS_VIEW;
    }

    @GetMapping("/{restaurantId}/orders/{orderId}")
    public String getRestaurantOrderDetails(@PathVariable("restaurantId") Long restaurantId,
                                            @PathVariable("orderId") Long orderId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails,
                                            Model model) {
        model.addAttribute(RESTAURANT_ATTRIBUTE, restaurantService.getRestaurantById(restaurantId));
        model.addAttribute(ORDER_ATTRIBUTE, orderService.getManagerOrder(userDetails.getId(), restaurantId, orderId));
        model.addAttribute(ORDER_ITEMS_ATTRIBUTE, orderService.getManagerOrderItems(userDetails.getId(), restaurantId, orderId));

        return MANAGER_ORDER_DETAILS_VIEW;
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
            redirectAttributes.addFlashAttribute(ORDER_SUCCESS_ATTRIBUTE, ORDER_STATUS_UPDATED_MESSAGE);
        } catch (OrderOperationException exception) {
            redirectAttributes.addFlashAttribute(ORDER_ERROR_ATTRIBUTE, exception.getMessage());
        }

        if (redirectToDetails) {
            return REDIRECT_MANAGER_ORDER_DETAILS.formatted(restaurantId, orderId);
        }

        return REDIRECT_MANAGER_ORDERS.formatted(restaurantId);
    }

    private void addFoodFormAttributes(Model model, Long restaurantId) {
        model.addAttribute(RESTAURANT_ID_ATTRIBUTE, restaurantId);
        model.addAttribute(CATEGORY_ATTRIBUTE, categoryService.getAllCategories());
    }
}
