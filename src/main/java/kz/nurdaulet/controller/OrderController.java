package kz.nurdaulet.controller;

import kz.nurdaulet.dto.OrderItemDetailsDto;
import kz.nurdaulet.dto.OrderSummaryDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.OrderItem;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.OrderService;
import kz.nurdaulet.service.RestaurantService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final FoodService foodService;
    private final RestaurantService restaurantService;

    public OrderController(OrderService orderService,
                           FoodService foodService,
                           RestaurantService restaurantService) {
        this.orderService = orderService;
        this.foodService = foodService;
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public String orders(@AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        List<Order> orders = orderService.getCustomerOrders(userDetails.getId());

        model.addAttribute("orders", buildOrderSummaries(orders));

        return "order/orders";
    }

    @GetMapping("/{orderId}")
    public String orderDetails(@PathVariable("orderId") Long orderId,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        Order order = orderService.getCustomerOrder(userDetails.getId(), orderId);
        List<OrderItem> orderItems = orderService.getCustomerOrderItems(userDetails.getId(), orderId);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", buildOrderItemDetails(orderItems));

        return "order/order-details";
    }

    @PostMapping("/{orderId}/pay")
    public String payOrder(@PathVariable("orderId") Long orderId,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        try {
            orderService.payOrder(userDetails.getId(), orderId);
            redirectAttributes.addFlashAttribute("orderSuccess", "Оплата прошла успешно. Заказ начал готовиться.");
        } catch (CartOperationException exception) {
            redirectAttributes.addFlashAttribute("orderError", exception.getMessage());
        }

        return "redirect:/orders/" + orderId;
    }

    private List<OrderSummaryDto> buildOrderSummaries(List<Order> orders) {
        List<OrderSummaryDto> summaries = new ArrayList<>();

        for (Order order : orders) {
            Restaurant restaurant = restaurantService.getRestaurantById(order.getRestaurantId());
            summaries.add(new OrderSummaryDto(
                    order.getId(),
                    restaurant.getName(),
                    order.getStatus(),
                    order.getDeliveryType(),
                    order.getTotalPrice(),
                    order.getCreatedAt()
            ));
        }

        return summaries;
    }

    private List<OrderItemDetailsDto> buildOrderItemDetails(List<OrderItem> orderItems) {
        List<OrderItemDetailsDto> details = new ArrayList<>();

        for (OrderItem item : orderItems) {
            Food food = foodService.getFoodById(item.getFoodId());
            details.add(new OrderItemDetailsDto(
                    item.getFoodId(),
                    food.getName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getPrice() * item.getQuantity()
            ));
        }

        return details;
    }
}
