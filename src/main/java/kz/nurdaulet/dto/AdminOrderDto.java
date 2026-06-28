package kz.nurdaulet.dto;

import kz.nurdaulet.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public class AdminOrderDto {
    private Long id;
    private String clientName;
    private String restaurantName;
    private OrderStatus status;
    private Double totalPrice;
    private LocalDateTime createdAt;

    public AdminOrderDto() {
    }

    public AdminOrderDto(Long id, String clientName, String restaurantName,
                         OrderStatus status, Double totalPrice, LocalDateTime createdAt) {
        this.id = id;
        this.clientName = clientName;
        this.restaurantName = restaurantName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
