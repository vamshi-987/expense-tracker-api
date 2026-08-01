package com.vamshi.expense_tracker.service;

import com.vamshi.expense_tracker.dto.DeleteExpenseResponse;
import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import org.springframework.data.domain.Page;

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
     * Retrieves all recorded expenses (unpaged).
     *
     * @return list of all expense responses
     */
    List<ExpenseResponse> getAllExpenses();

    /**
     * Retrieves all recorded expenses with pagination and sorting.
     *
     * @param pageNo   page number (0-indexed)
     * @param pageSize number of items per page
     * @param sortBy   property to sort by
     * @param sortDir  sort direction ("asc" or "desc")
     * @return page of expense responses
     */
    Page<ExpenseResponse> getAllExpenses(int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Retrieves expenses filtered by category (case-insensitive, unpaged).
     *
     * @param category category to filter by
     * @return list of matching expense responses
     */
    List<ExpenseResponse> getExpensesByCategory(String category);

    /**
     * Retrieves expenses filtered by category with pagination and sorting.
     *
     * @param category category to filter by
     * @param pageNo   page number
     * @param pageSize items per page
     * @param sortBy   sort property
     * @param sortDir  sort direction
     * @return page of expense responses
     */
    Page<ExpenseResponse> getExpensesByCategory(String category, int pageNo, int pageSize, String sortBy, String sortDir);

    /**
     * Searches expenses matching query keyword in title or category with pagination and sorting.
     *
     * @param query    search keyword
     * @param pageNo   page number
     * @param pageSize items per page
     * @param sortBy   sort property
     * @param sortDir  sort direction
     * @return page of expense responses matching query
     */
    Page<ExpenseResponse> searchExpenses(String query, int pageNo, int pageSize, String sortBy, String sortDir);

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
     * @return delete expense response with confirmation message and deleted expense details
     */
    DeleteExpenseResponse deleteExpense(Long id);
}
