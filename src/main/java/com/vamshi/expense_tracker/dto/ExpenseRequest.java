package com.vamshi.expense_tracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Request object used to create a new expense")
public class ExpenseRequest {

    @Schema(
            description = "Title of the expense",
            example = "Lunch"
    )
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(
            description = "Expense amount",
            example = "250.50"
    )
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @Schema(
            description = "Expense category",
            example = "Food"
    )
    @NotBlank(message = "Category is required")
    private String category;

    @Schema(
            description = "Date of expense",
            example = "2026-07-31"
    )
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;
}