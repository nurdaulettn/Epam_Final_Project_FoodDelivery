package kz.nurdaulet.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kz.nurdaulet.dto.CheckoutDto;
import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.CartService;
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
    private static final String CHECKOUT_SESSION_ATTRIBUTE = "checkout";

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String cart(HttpSession session, Model model) {
        Map<Long, Integer> cart = getCart(session);

        model.addAttribute("cartItems", cartService.getCartItems(cart));
        model.addAttribute("total", cartService.calculateTotal(cart));

        return "cart/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Map<Long, Integer> cart = getCart(session);

        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("cartError", "Корзина пуста");

            return "redirect:/cart";
        }

        if (!model.containsAttribute("checkout")) {
            model.addAttribute("checkout", new CheckoutDto());
        }

        addCheckoutAttributes(model, cart);

        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String checkout(@Valid @ModelAttribute("checkout") CheckoutDto checkoutDto,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model) {
        Map<Long, Integer> cart = getCart(session);

        if (cart.isEmpty()) {
            bindingResult.reject("cart.empty", "Корзина пуста");
        }

        validateDeliveryAddress(checkoutDto, bindingResult);

        if (bindingResult.hasErrors()) {
            addCheckoutAttributes(model, cart);

            return "cart/checkout";
        }

        if (DeliveryType.PICKUP.equals(checkoutDto.getDeliveryType())) {
            checkoutDto.setDeliveryAddress(null);
        }

        session.setAttribute(CHECKOUT_SESSION_ATTRIBUTE, checkoutDto);
        model.addAttribute("checkoutSuccess", "Данные оформления проверены. Создание заказа будет в следующем шаге.");
        addCheckoutAttributes(model, cart);

        return "cart/checkout";
    }

    @PostMapping("/add/{foodId}")
    public String addFood(@PathVariable("foodId") Long foodId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            cartService.addFood(getCart(session), foodId);
            redirectAttributes.addFlashAttribute("cartSuccess", "Блюдо добавлено в корзину");
        } catch (CartOperationException exception) {
            redirectAttributes.addFlashAttribute("cartError", exception.getMessage());
        }

        return "redirect:/foods";
    }

    @PostMapping("/update/{foodId}")
    public String updateQuantity(@PathVariable("foodId") Long foodId,
                                 @RequestParam("quantity") Integer quantity,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(getCart(session), foodId, quantity);
            redirectAttributes.addFlashAttribute("cartSuccess", "Количество обновлено");
        } catch (CartOperationException exception) {
            redirectAttributes.addFlashAttribute("cartError", exception.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{foodId}")
    public String removeFood(@PathVariable("foodId") Long foodId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        cartService.removeFood(getCart(session), foodId);
        redirectAttributes.addFlashAttribute("cartSuccess", "Блюдо удалено из корзины");

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.clear(getCart(session));
        redirectAttributes.addFlashAttribute("cartSuccess", "Корзина очищена");

        return "redirect:/cart";
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
        model.addAttribute("cartItems", cartService.getCartItems(cart));
        model.addAttribute("total", cartService.calculateTotal(cart));
        model.addAttribute("deliveryTypes", DeliveryType.values());
    }

    private void validateDeliveryAddress(CheckoutDto checkoutDto, BindingResult bindingResult) {
        if (DeliveryType.DELIVERY.equals(checkoutDto.getDeliveryType())
                && (checkoutDto.getDeliveryAddress() == null
                || checkoutDto.getDeliveryAddress().isBlank())) {
            bindingResult.rejectValue(
                    "deliveryAddress",
                    "deliveryAddress.required",
                    "Адрес доставки обязателен"
            );
        }
    }
}
