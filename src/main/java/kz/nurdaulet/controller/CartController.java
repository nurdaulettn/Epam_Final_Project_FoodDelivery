package kz.nurdaulet.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kz.nurdaulet.dto.CheckoutDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Order;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.CartService;
import kz.nurdaulet.service.OrderService;
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

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {
    private static final String CART_SESSION_ATTRIBUTE = "cart";
    private static final String CART_ITEMS_ATTRIBUTE = "cartItems";
    private static final String TOTAL_ATTRIBUTE = "total";
    private static final String CHECKOUT_ATTRIBUTE = "checkout";
    private static final String DELIVERY_TYPES_ATTRIBUTE = "deliveryTypes";
    private static final String CART_ERROR_ATTRIBUTE = "cartError";
    private static final String CART_SUCCESS_ATTRIBUTE = "cartSuccess";
    private static final String CART_VIEW = "cart/cart";
    private static final String CHECKOUT_VIEW = "cart/checkout";
    private static final String REDIRECT_CART = "redirect:/cart";
    private static final String REDIRECT_FOODS = "redirect:/foods";
    private static final String REDIRECT_ORDERS = "redirect:/orders/";
    private static final String CART_EMPTY_ERROR_CODE = "cart.empty";
    private static final String ORDER_CREATE_FAILED_ERROR_CODE = "order.create.failed";
    private static final String DELIVERY_ADDRESS_ERROR_FIELD = "deliveryAddress";
    private static final String DELIVERY_ADDRESS_REQUIRED_ERROR_CODE = "deliveryAddress.required";
    private static final String CART_EMPTY_MESSAGE = "Cart is empty";
    private static final String FOOD_ADDED_MESSAGE = "Food added to cart";
    private static final String QUANTITY_UPDATED_MESSAGE = "Quantity updated";
    private static final String FOOD_REMOVED_MESSAGE = "Food removed from cart";
    private static final String CART_CLEARED_MESSAGE = "Cart cleared";
    private static final String DELIVERY_ADDRESS_REQUIRED_MESSAGE = "Delivery address is required";

    private final CartService cartService;
    private final OrderService orderService;

    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping
    public String cart(HttpSession session, Model model) {
        Map<Long, Integer> cart = getCart(session);
        model.addAttribute(CART_ITEMS_ATTRIBUTE, cartService.getCartItems(cart));
        model.addAttribute(TOTAL_ATTRIBUTE, cartService.calculateTotal(cart));

        return CART_VIEW;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Map<Long, Integer> cart = getCart(session);

        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute(CART_ERROR_ATTRIBUTE, CART_EMPTY_MESSAGE);

            return REDIRECT_CART;
        }

        if (!model.containsAttribute(CHECKOUT_ATTRIBUTE)) {
            model.addAttribute(CHECKOUT_ATTRIBUTE, new CheckoutDto());
        }

        addCheckoutAttributes(model, cart);

        return CHECKOUT_VIEW;
    }

    @PostMapping("/checkout")
    public String checkout(@Valid @ModelAttribute(CHECKOUT_ATTRIBUTE) CheckoutDto checkoutDto,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           HttpSession session,
                           Model model) {
        Map<Long, Integer> cart = getCart(session);

        if (cart.isEmpty()) {
            bindingResult.reject(CART_EMPTY_ERROR_CODE, CART_EMPTY_MESSAGE);
        }

        validateDeliveryAddress(checkoutDto, bindingResult);

        if (bindingResult.hasErrors()) {
            addCheckoutAttributes(model, cart);

            return CHECKOUT_VIEW;
        }

        if (DeliveryType.PICKUP.equals(checkoutDto.getDeliveryType())) {
            checkoutDto.setDeliveryAddress(null);
        }

        try {
            Order order = orderService.createOrder(userDetails.getId(), cart, checkoutDto);

            return REDIRECT_ORDERS + order.getId();
        } catch (CartOperationException exception) {
            bindingResult.reject(ORDER_CREATE_FAILED_ERROR_CODE, exception.getMessage());
            addCheckoutAttributes(model, cart);

            return CHECKOUT_VIEW;
        }
    }

    @PostMapping("/add/{foodId}")
    public String addFood(@PathVariable("foodId") Long foodId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            cartService.addFood(getCart(session), foodId);

            redirectAttributes.addFlashAttribute(CART_SUCCESS_ATTRIBUTE, FOOD_ADDED_MESSAGE);
        } catch (CartOperationException exception) {
            redirectAttributes.addFlashAttribute(CART_ERROR_ATTRIBUTE, exception.getMessage());
        }

        return REDIRECT_FOODS;
    }

    @PostMapping("/update/{foodId}")
    public String updateQuantity(@PathVariable("foodId") Long foodId,
                                 @RequestParam("quantity") Integer quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(getCart(session), foodId, quantity);

            redirectAttributes.addFlashAttribute(CART_SUCCESS_ATTRIBUTE, QUANTITY_UPDATED_MESSAGE);
        } catch (CartOperationException exception) {
            redirectAttributes.addFlashAttribute(CART_ERROR_ATTRIBUTE, exception.getMessage());
        }

        return REDIRECT_CART;
    }

    @PostMapping("/remove/{foodId}")
    public String removeFood(@PathVariable("foodId") Long foodId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        cartService.removeFood(getCart(session), foodId);
        redirectAttributes.addFlashAttribute(CART_SUCCESS_ATTRIBUTE, FOOD_REMOVED_MESSAGE);

        return REDIRECT_CART;
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.clear(getCart(session));
        redirectAttributes.addFlashAttribute(CART_SUCCESS_ATTRIBUTE, CART_CLEARED_MESSAGE);

        return REDIRECT_CART;
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Object sessionCart = session.getAttribute(CART_SESSION_ATTRIBUTE);

        if (sessionCart instanceof Map<?, ?>) {
            return (Map<Long, Integer>) sessionCart;
        }

        Map<Long, Integer> cart = new LinkedHashMap<>();
        session.setAttribute(CART_SESSION_ATTRIBUTE, cart);

        return cart;
    }

    private void addCheckoutAttributes(Model model, Map<Long, Integer> cart) {
        model.addAttribute(CART_ITEMS_ATTRIBUTE, cartService.getCartItems(cart));
        model.addAttribute(TOTAL_ATTRIBUTE, cartService.calculateTotal(cart));
        model.addAttribute(DELIVERY_TYPES_ATTRIBUTE, DeliveryType.values());
    }

    private void validateDeliveryAddress(CheckoutDto checkoutDto, BindingResult bindingResult) {
        if (DeliveryType.DELIVERY.equals(checkoutDto.getDeliveryType())
                && (checkoutDto.getDeliveryAddress() == null
                || checkoutDto.getDeliveryAddress().isBlank())) {
            bindingResult.rejectValue(
                    DELIVERY_ADDRESS_ERROR_FIELD,
                    DELIVERY_ADDRESS_REQUIRED_ERROR_CODE,
                    DELIVERY_ADDRESS_REQUIRED_MESSAGE
            );
        }
    }
}
