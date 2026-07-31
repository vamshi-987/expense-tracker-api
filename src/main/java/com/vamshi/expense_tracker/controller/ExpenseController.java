package com.vamshi.expense_tracker.controller;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import com.vamshi.expense_tracker.service.ExpenseService;
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

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(
            @Valid @RequestBody ExpenseRequest request) {

        ExpenseResponse response = expenseService.addExpense(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {

        List<ExpenseResponse> expenses = expenseService.getAllExpenses();

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(
            @PathVariable String category) {

        List<ExpenseResponse> expenses =
                expenseService.getExpensesByCategory(category);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalExpenses() {

        BigDecimal total = expenseService.getTotalExpenses();

        return ResponseEntity.ok(total);
    }

    @GetMapping("/total/{category}")
    public ResponseEntity<BigDecimal> getTotalByCategory(
            @PathVariable String category) {

        BigDecimal total =
                expenseService.getTotalByCategory(category);

        return ResponseEntity.ok(total);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id) {

        expenseService.deleteExpense(id);

        return ResponseEntity.noContent().build();
    }

}