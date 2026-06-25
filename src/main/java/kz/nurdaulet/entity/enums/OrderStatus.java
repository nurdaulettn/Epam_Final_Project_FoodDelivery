package kz.nurdaulet.entity.enums;

public enum OrderStatus {
    PENDING_PAYMENT("Ожидает оплаты"),
    PREPARING("Готовится"),
    READY("Готов к выдаче"),
    COMPLETED("Завершён"),
    CANCELLED("Отменён");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
