package kz.nurdaulet.dto;

public class CartItemDto {
    private Long foodId;
    private String foodName;
    private Double price;
    private Integer quantity;
    private Double subtotal;
    private Long restaurantId;

    public CartItemDto() {
    }

    public CartItemDto(Long foodId, String foodName, Double price,
                       Integer quantity, Double subtotal, Long restaurantId) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.restaurantId = restaurantId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
