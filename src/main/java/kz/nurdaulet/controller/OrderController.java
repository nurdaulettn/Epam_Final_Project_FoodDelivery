package kz.nurdaulet.controller;

import kz.nurdaulet.dto.OrderSummaryDto;
import kz.nurdaulet.dto.PageDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.OrderService;
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
    private static final String ORDERS_ATTRIBUTE = "orders";
    private static final String PAGE_ATTRIBUTE = "page";
    private static final String ORDER_ATTRIBUTE = "order";
    private static final String ORDER_ITEMS_ATTRIBUTE = "orderItems";
    private static final String ORDER_SUCCESS_ATTRIBUTE = "orderSuccess";
    private static final String ORDER_ERROR_ATTRIBUTE = "orderError";
    private static final String ORDERS_VIEW = "order/orders";
    private static final String ORDER_DETAILS_VIEW = "order/order-details";
    private static final String REDIRECT_ORDER_DETAILS = "redirect:/orders/";
    private static final String PAYMENT_SUCCESS_MESSAGE = "Payment successful. The order is now being prepared.";

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getOrders(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            Model model) {
        List<OrderSummaryDto> orders = orderService.getCustomerOrders(userDetails.getId());
        PageDto<OrderSummaryDto> orderPage = PageDto.of(orders, page, PAGE_SIZE);

        model.addAttribute(ORDERS_ATTRIBUTE, orderPage.getContent());
        model.addAttribute(PAGE_ATTRIBUTE, orderPage);

        return ORDERS_VIEW;
    }

    @GetMapping("/{orderId}")
    public String getOrderDetails(@PathVariable("orderId") Long orderId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {
        Order order = orderService.getCustomerOrder(userDetails.getId(), orderId);

        model.addAttribute(ORDER_ATTRIBUTE, order);
        model.addAttribute(ORDER_ITEMS_ATTRIBUTE, orderService.getCustomerOrderItems(userDetails.getId(), orderId));

        return ORDER_DETAILS_VIEW;
    }

    @PostMapping("/{orderId}/pay")
    public String payOrder(@PathVariable("orderId") Long orderId,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        try {
            orderService.payOrder(userDetails.getId(), orderId);

            redirectAttributes.addFlashAttribute(ORDER_SUCCESS_ATTRIBUTE, PAYMENT_SUCCESS_MESSAGE);
        } catch (CartOperationException exception) {
            redirectAttributes.addFlashAttribute(ORDER_ERROR_ATTRIBUTE, exception.getMessage());
        }

        return REDIRECT_ORDER_DETAILS + orderId;
    }
}
