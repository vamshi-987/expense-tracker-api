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
@Schema(description = "Response returned after deleting a category and its associated expenses")
public class DeleteCategoryResponse {

    @Schema(description = "Confirmation message", example = "Category and all its associated expenses deleted successfully")
    private String message;

    @Schema(description = "Details of the deleted category")
    private CategoryResponse deletedCategory;
}
