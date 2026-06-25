package kz.nurdaulet.dto;

import kz.nurdaulet.entity.enums.DeliveryType;
import kz.nurdaulet.entity.enums.OrderStatus;

import java.time.LocalDateTime;

public class OrderSummaryDto {
    private Long id;
    private String restaurantName;
    private OrderStatus status;
    private DeliveryType deliveryType;
    private Double totalPrice;
    private LocalDateTime createdAt;

    public OrderSummaryDto() {
    }

    public OrderSummaryDto(Long id, String restaurantName, OrderStatus status,
                           DeliveryType deliveryType, Double totalPrice,
                           LocalDateTime createdAt) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.status = status;
        this.deliveryType = deliveryType;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
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
