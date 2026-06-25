package kz.nurdaulet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kz.nurdaulet.entity.enums.DeliveryType;

public class CheckoutDto {
    @NotNull
    private DeliveryType deliveryType = DeliveryType.DELIVERY;

    @Size(max = 500)
    private String deliveryAddress;

    public CheckoutDto() {
    }

    public CheckoutDto(DeliveryType deliveryType, String deliveryAddress) {
        this.deliveryType = deliveryType;
        this.deliveryAddress = deliveryAddress;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
