package kz.nurdaulet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewCreateDto {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 500)
    private String comment;

    public ReviewCreateDto() {
    }

    public ReviewCreateDto(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
