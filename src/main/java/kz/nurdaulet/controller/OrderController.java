package kz.nurdaulet.controller;

import kz.nurdaulet.dto.OrderItemDetailsDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.OrderItem;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.FoodService;
import kz.nurdaulet.service.OrderService;
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

    public OrderController(OrderService orderService, FoodService foodService) {
        this.orderService = orderService;
        this.foodService = foodService;
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
