package kz.nurdaulet.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryCreateDto {
    @NotBlank
    private String name;

    public CategoryCreateDto() {}

    public CategoryCreateDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
