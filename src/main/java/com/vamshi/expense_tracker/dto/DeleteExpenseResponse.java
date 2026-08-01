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
@Schema(description = "Response returned after deleting an expense")
public class DeleteExpenseResponse {

    @Schema(description = "Confirmation message", example = "Expense deleted successfully")
    private String message;

    @Schema(description = "Details of the deleted expense")
    private ExpenseResponse deletedExpense;
}
