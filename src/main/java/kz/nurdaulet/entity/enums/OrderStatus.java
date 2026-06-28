package kz.nurdaulet.entity.enums;

public enum OrderStatus {
    PENDING_PAYMENT("Pending payment"),
    PREPARING("Preparing"),
    READY("Ready for pickup"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
