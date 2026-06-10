package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.UserCreateDto;
import kz.nurdaulet.entity.enums.Role;
import kz.nurdaulet.exception.UserCreatingException;
import kz.nurdaulet.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserCreateDto());
        }

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserCreateDto user,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            if (Role.MANAGER.name().equals(user.getRole())) {
                user.setRole(Role.MANAGER.name());
            } else {
                user.setRole("CUSTOMER");
            }

            userService.create(user);

            return "redirect:/login?registered=true";
        } catch (UserCreatingException exception) {
            model.addAttribute("registrationError", exception.getMessage());

            return "auth/register";
        }
    }
}
