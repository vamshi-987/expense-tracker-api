package com.vamshi.expense_tracker.service;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing expense tracking operations.
 */
public interface ExpenseService {

    /**
     * Adds a new expense entry.
     *
     * @param request expense creation details
     * @return the created expense response with generated ID
     */
    ExpenseResponse addExpense(ExpenseRequest request);

    /**
     * Retrieves all recorded expenses.
     *
     * @return list of all expense responses
     */
    List<ExpenseResponse> getAllExpenses();

    /**
     * Retrieves expenses filtered by category (case-insensitive).
     *
     * @param category category to filter by
     * @return list of matching expense responses
     */
    List<ExpenseResponse> getExpensesByCategory(String category);

    /**
     * Calculates the sum of all recorded expenses.
     *
     * @return total overall expense amount
     */
    BigDecimal getTotalExpenses();

    /**
     * Calculates the sum of expenses within a specific category.
     *
     * @param category category to aggregate
     * @return total expense amount for the given category
     */
    BigDecimal getTotalByCategory(String category);

    /**
     * Deletes an expense by its unique identifier.
     *
     * @param id ID of the expense to delete
     */
    void deleteExpense(Long id);

}

