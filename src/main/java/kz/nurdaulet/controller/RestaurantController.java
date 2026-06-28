package kz.nurdaulet.controller;

import jakarta.validation.Valid;
import kz.nurdaulet.dto.ReviewCreateDto;
import kz.nurdaulet.entity.CustomUserDetails;
import kz.nurdaulet.dto.PageDto;
import kz.nurdaulet.entity.Review;
import kz.nurdaulet.entity.Restaurant;
import kz.nurdaulet.service.ReviewService;
import kz.nurdaulet.service.RestaurantService;
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

import java.util.List;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
    private static final int PAGE_SIZE = 6;

    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    public RestaurantController(RestaurantService restaurantService, ReviewService reviewService) {
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String getRestaurants(Model model,
                                 @RequestParam(required = false, name = "search") String search,
                                 @RequestParam(name = "page", defaultValue = "1") int page) {
        List<Restaurant> restaurants;

        if (search != null && !search.isBlank()) {
            restaurants = restaurantService.searchRestaurantsByName(search);
        } else {
            restaurants = restaurantService.getAllRestaurants();
        }

        PageDto<Restaurant> restaurantPage = PageDto.of(restaurants, page, PAGE_SIZE);

        model.addAttribute("restaurants", restaurantPage.getContent());
        model.addAttribute("page", restaurantPage);
        model.addAttribute("search", search);

        return "restaurant/allRestaurants";
    }

    @GetMapping("/{restaurantId}")
    public String getRestaurantDetails(@PathVariable("restaurantId") Long restaurantId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails,
                                       Model model) {
        addRestaurantDetailsAttributes(model, restaurantId, userDetails);

        if (!model.containsAttribute("review")) {
            Review customerReview = userDetails == null
                    ? null
                    : reviewService.getCustomerReview(userDetails.getId(), restaurantId);
            model.addAttribute("review", toReviewCreateDto(customerReview));
        }

        return "restaurant/details";
    }

    @PostMapping("/{restaurantId}/reviews")
    public String saveReview(@PathVariable("restaurantId") Long restaurantId,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("review") ReviewCreateDto review,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            addRestaurantDetailsAttributes(model, restaurantId, userDetails);

            return "restaurant/details";
        }

        reviewService.createOrUpdateReview(userDetails.getId(), restaurantId, review);

        return "redirect:/restaurants/" + restaurantId;
    }

    private void addRestaurantDetailsAttributes(Model model,
                                                Long restaurantId,
                                                CustomUserDetails userDetails) {
        model.addAttribute("restaurant", restaurantService.getRestaurantById(restaurantId));
        model.addAttribute("reviews", reviewService.getRestaurantReviews(restaurantId));
        model.addAttribute("customerReviewExists", userDetails != null
                && reviewService.getCustomerReview(userDetails.getId(), restaurantId) != null);
    }

    private ReviewCreateDto toReviewCreateDto(Review review) {
        if (review == null) {
            return new ReviewCreateDto();
        }

        return new ReviewCreateDto(review.getRating(), review.getComment());
    }
}
