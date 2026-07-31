package com.vamshi.expense_tracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
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
            description = "Expense category",
            example = "Food"
    )
    private String category;

    @Schema(
            description = "Expense date",
            example = "2026-07-31"
    )
    private LocalDate date;
}