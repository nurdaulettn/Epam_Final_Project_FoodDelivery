package kz.nurdaulet.entity;

import kz.nurdaulet.entity.enums.RestaurantStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Restaurant {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private Double ratingAvg;
    private Integer ratingCount;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Long managerId;
    private RestaurantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Restaurant() {}

    public Restaurant(Long id, String name, String description,
                      String address, String phone,
                      Double ratingAvg, Integer ratingCount,
                      LocalTime openingTime, LocalTime closingTime,
                      Long managerId, RestaurantStatus status,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.ratingAvg = ratingAvg;
        this.ratingCount = ratingCount;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.managerId = managerId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(Double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public void setStatus(RestaurantStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
