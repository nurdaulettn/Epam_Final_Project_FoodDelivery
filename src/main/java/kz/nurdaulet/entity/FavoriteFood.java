package kz.nurdaulet.entity;

public class FavoriteFood {
    private Long id;
    private Long userId;
    private Long foodId;

    public FavoriteFood() {
    }

    public FavoriteFood(Long id, Long userId, Long foodId) {
        this.id = id;
        this.userId = userId;
        this.foodId = foodId;
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

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }
}
