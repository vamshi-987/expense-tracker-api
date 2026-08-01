package com.vamshi.expense_tracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing a Category")
public class CategoryResponse {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category Name", example = "Food")
    private String name;
}
