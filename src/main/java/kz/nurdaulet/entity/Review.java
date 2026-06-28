package kz.nurdaulet.entity;

import java.time.LocalDateTime;

public class Review {
    private Long id;
    private Long userId;
    private Long restaurantId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review() {
    }

    public Review(Long id, Long userId, Long restaurantId, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
