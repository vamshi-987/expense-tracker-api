package com.vamshi.expense_tracker.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating a Category")
public class CategoryRequest {

    @Schema(description = "Name of the category", example = "Food")
    @NotBlank(message = "Category name is required")
    @JsonAlias({"category", "categoryName", "category_name"})
    private String name;
}
