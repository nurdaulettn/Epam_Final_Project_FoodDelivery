package kz.nurdaulet.entity.enums;

public enum DeliveryType {
    DELIVERY("Доставка"),
    PICKUP("Самовывоз");

    private final String displayName;

    DeliveryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
