package kz.nurdaulet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public class RestaurantCreateDto {
    @NotBlank
    @Size(min = 6, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(min = 6, max = 100)
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\+7 \\(\\d{3}\\) \\d{3} \\d{4}$")
    private String phone;

    @NotNull
    private LocalTime openingTime;

    @NotNull
    private LocalTime closingTime;

    public RestaurantCreateDto() {
    }

    public RestaurantCreateDto(String name, String description, String address, LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
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
}
