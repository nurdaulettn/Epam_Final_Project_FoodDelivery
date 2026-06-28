package kz.nurdaulet.controller;

import kz.nurdaulet.dto.PageDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.entity.Food;
import kz.nurdaulet.service.FavoriteFoodService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/favorites")
public class FavoriteFoodController {
    private static final int PAGE_SIZE = 6;
    private static final String REDIRECT_FAVORITES = "redirect:/favorites";
    private static final String REDIRECT_FOODS = "redirect:/foods";

    private final FavoriteFoodService favoriteFoodService;

    public FavoriteFoodController(FavoriteFoodService favoriteFoodService) {
        this.favoriteFoodService = favoriteFoodService;
    }

    @GetMapping
    public String favorites(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(name = "page", defaultValue = "1") int page,
                            Model model) {
        List<Food> favorites = favoriteFoodService.getFavoriteFoods(userDetails.getId());
        PageDto<Food> favoritePage = PageDto.of(favorites, page, PAGE_SIZE);

        model.addAttribute("foods", favoritePage.getContent());
        model.addAttribute("page", favoritePage);

        return "food/favorites";
    }

    @PostMapping("/{foodId}/add")
    public String addFavorite(@PathVariable("foodId") Long foodId,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestHeader(value = "Referer", required = false) String referer) {
        favoriteFoodService.addFavorite(userDetails.getId(), foodId);

        return redirectBack(referer);
    }

    @PostMapping("/{foodId}/remove")
    public String removeFavorite(@PathVariable("foodId") Long foodId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestHeader(value = "Referer", required = false) String referer) {
        favoriteFoodService.removeFavorite(userDetails.getId(), foodId);

        return redirectBack(referer);
    }

    private String redirectBack(String referer) {
        if (referer != null && referer.contains("/favorites")) {
            return REDIRECT_FAVORITES;
        }

        return REDIRECT_FOODS;
    }
}
