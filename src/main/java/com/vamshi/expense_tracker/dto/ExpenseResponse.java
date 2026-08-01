package com.vamshi.expense_tracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object returned after expense operations")
public class ExpenseResponse {

    @Schema(
            description = "Expense ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Title of the expense",
            example = "Lunch"
    )
    private String title;

    @Schema(
            description = "Expense amount",
            example = "250.50"
    )
    private BigDecimal amount;

    @Schema(
            description = "Expense category name",
            example = "Food"
    )
    private String category;

    @Schema(
            description = "Expense category ID",
            example = "1"
    )
    private Long categoryId;

    @Schema(
            description = "Expense date",
            example = "2026-07-31"
    )
    private LocalDate date;
}