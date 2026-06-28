package kz.nurdaulet.controller;

import kz.nurdaulet.dto.OrderSummaryDto;
import kz.nurdaulet.dto.PageDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Order;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private static final int PAGE_SIZE = 5;

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String orders(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @RequestParam(name = "page", defaultValue = "1") int page,
                         Model model) {
        List<OrderSummaryDto> orders = orderService.getCustomerOrders(userDetails.getId());
        PageDto<OrderSummaryDto> orderPage = PageDto.of(orders, page, PAGE_SIZE);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("page", orderPage);

        return "order/orders";
    }

    @GetMapping("/{orderId}")
    public String orderDetails(@PathVariable("orderId") Long orderId,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        Order order = orderService.getCustomerOrder(userDetails.getId(), orderId);

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderService.getCustomerOrderItems(userDetails.getId(), orderId));

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
}
