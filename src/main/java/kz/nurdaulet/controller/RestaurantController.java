package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
    private final

    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("restaurant")) {
            model.addAttribute("restaurant", new RestaurantCreateDto());
        }

        return "restaurant/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("restaurant") RestaurantCreateDto restaurantCreateDto,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "restaurant/create";
        }


    }
}
