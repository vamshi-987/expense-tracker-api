package com.vamshi.expense_tracker.controller;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import com.vamshi.expense_tracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(
            summary = "Add a new expense",
            description = "Creates and stores a new expense"
    )
    @ApiResponse(responseCode = "201", description = "Expense created")
    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(
            @Valid @RequestBody ExpenseRequest request) {

        ExpenseResponse response = expenseService.addExpense(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get all expenses"
    )
    @ApiResponse(responseCode = "200", description = "Expenses retrieved")
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {

        List<ExpenseResponse> expenses = expenseService.getAllExpenses();

        return ResponseEntity.ok(expenses);
    }

    @Operation(
            summary = "Get expenses by category"
    )
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(
            @PathVariable String category) {

        List<ExpenseResponse> expenses =
                expenseService.getExpensesByCategory(category);

        return ResponseEntity.ok(expenses);
    }

    @Operation(
            summary = "Calculate total expenses"
    )
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalExpenses() {

        BigDecimal total = expenseService.getTotalExpenses();

        return ResponseEntity.ok(total);
    }

    @Operation(
            summary = "Calculate category total"
    )
    @GetMapping("/total/{category}")
    public ResponseEntity<BigDecimal> getTotalByCategory(
            @PathVariable String category) {

        BigDecimal total =
                expenseService.getTotalByCategory(category);

        return ResponseEntity.ok(total);
    }

    @Operation(
            summary = "Delete an expense"
    )
    @ApiResponse(responseCode = "204", description = "Expense deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id) {

        expenseService.deleteExpense(id);

        return ResponseEntity.noContent().build();
    }

}