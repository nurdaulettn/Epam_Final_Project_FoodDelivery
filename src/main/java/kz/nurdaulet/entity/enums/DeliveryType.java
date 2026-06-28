package kz.nurdaulet.entity.enums;

public enum DeliveryType {
    DELIVERY("Delivery"),
    PICKUP("Pickup");

    private final String displayName;

    DeliveryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
