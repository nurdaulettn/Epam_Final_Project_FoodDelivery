package kz.nurdaulet.controller;

import jakarta.servlet.http.HttpSession;
import kz.nurdaulet.exception.CartOperationException;
import kz.nurdaulet.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
