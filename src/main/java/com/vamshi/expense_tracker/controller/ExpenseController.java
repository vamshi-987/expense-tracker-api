package com.vamshi.expense_tracker.controller;

import com.vamshi.expense_tracker.dto.DeleteExpenseResponse;
import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import com.vamshi.expense_tracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
            summary = "Get all expenses (unpaged)"
    )
    @ApiResponse(responseCode = "200", description = "Expenses retrieved")
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {

        List<ExpenseResponse> expenses = expenseService.getAllExpenses();

        return ResponseEntity.ok(expenses);
    }

    @Operation(
            summary = "Get expenses with pagination and sorting",
            description = "Retrieves expenses wrapped in Spring Data Page interface"
    )
    @GetMapping("/paged")
    public ResponseEntity<Page<ExpenseResponse>> getExpensesPaged(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (date, amount, title, category, id)") @RequestParam(defaultValue = "date") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Page<ExpenseResponse> response = expenseService.getAllExpenses(page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search expenses by keyword",
            description = "Searches for keyword in expense title or category name with pagination and sorting"
    )
    @GetMapping("/search")
    public ResponseEntity<Page<ExpenseResponse>> searchExpenses(
            @Parameter(description = "Keyword to search in title or category") @RequestParam(defaultValue = "") String query,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "date") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "desc") String sortDir) {

        Page<ExpenseResponse> response = expenseService.searchExpenses(query, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get expenses by category (unpaged)"
    )
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(
            @PathVariable String category) {

        List<ExpenseResponse> expenses =
                expenseService.getExpensesByCategory(category);

        return ResponseEntity.ok(expenses);
    }

    @Operation(
            summary = "Get expenses by category with pagination and sorting"
    )
    @GetMapping("/category/{category}/paged")
    public ResponseEntity<Page<ExpenseResponse>> getExpensesByCategoryPaged(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<ExpenseResponse> response = expenseService.getExpensesByCategory(category, page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
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
    @ApiResponse(responseCode = "200", description = "Expense deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteExpenseResponse> deleteExpense(
            @PathVariable Long id) {

        DeleteExpenseResponse response = expenseService.deleteExpense(id);

        return ResponseEntity.ok(response);
    }
}