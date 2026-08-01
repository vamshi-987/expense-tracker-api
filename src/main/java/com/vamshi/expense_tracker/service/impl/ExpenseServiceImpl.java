package com.vamshi.expense_tracker.service.impl;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import com.vamshi.expense_tracker.entity.Category;
import com.vamshi.expense_tracker.entity.Expense;
import com.vamshi.expense_tracker.exception.CategoryNotFoundException;
import com.vamshi.expense_tracker.exception.ExpenseNotFoundException;
import com.vamshi.expense_tracker.mapper.ExpenseMapper;
import com.vamshi.expense_tracker.repository.CategoryRepository;
import com.vamshi.expense_tracker.repository.ExpenseRepository;
import com.vamshi.expense_tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of {@link ExpenseService} managing transactions, category resolution, mapping, and logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseMapper expenseMapper;

    /**
     * Creates and saves a new expense for an existing category (case-insensitive search).
     *
     * @param request expense creation details
     * @return created expense response
     * @throws CategoryNotFoundException if the specified category name does not exist
     */
    @Override
    @Transactional
    public ExpenseResponse addExpense(ExpenseRequest request) {
        log.info("Adding new expense with title: '{}', category: '{}', amount: {}",
                request.getTitle(), request.getCategory(), request.getAmount());

        String categoryName = request.getCategory() != null ? request.getCategory().trim() : "";
        Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> {
                    log.error("Failed to add expense: Category '{}' not found", categoryName);
                    return new CategoryNotFoundException("Category not found: " + categoryName);
                });

        Expense expense = expenseMapper.toEntity(request);
        expense.setCategory(category);

        Expense savedExpense = expenseRepository.save(expense);
        log.info("Successfully created expense with ID: {} under Category: '{}' (ID: {})",
                savedExpense.getId(), category.getName(), category.getId());

        return expenseMapper.toResponse(savedExpense);
    }

    /**
     * Retrieves all expenses from the database.
     *
     * @return list of expense responses
     */
    @Override
    public List<ExpenseResponse> getAllExpenses() {
        log.info("Fetching all expenses");
        List<Expense> expenses = expenseRepository.findAll();
        log.debug("Found {} total expenses", expenses.size());
        return expenseMapper.toResponseList(expenses);
    }

    /**
     * Retrieves expenses matching the given category name (case-insensitive).
     *
     * @param category category name
     * @return list of matching expense responses
     */
    @Override
    public List<ExpenseResponse> getExpensesByCategory(String category) {
        String categoryName = category != null ? category.trim() : "";
        log.info("Fetching expenses for category: '{}'", categoryName);
        List<Expense> expenses = expenseRepository.findByCategory_NameIgnoreCase(categoryName);
        log.debug("Found {} expenses for category: '{}'", expenses.size(), categoryName);
        return expenseMapper.toResponseList(expenses);
    }

    /**
     * Calculates the sum of all expenses via database aggregation.
     *
     * @return overall total expense amount
     */
    @Override
    public BigDecimal getTotalExpenses() {
        log.info("Calculating total expenses overall");
        BigDecimal total = expenseRepository.sumTotalExpenses();
        log.info("Overall total expenses calculated: {}", total);
        return total;
    }

    /**
     * Calculates the total expenses for a specified category name via database aggregation (case-insensitive).
     *
     * @param category category name
     * @return category total expense amount
     */
    @Override
    public BigDecimal getTotalByCategory(String category) {
        String categoryName = category != null ? category.trim() : "";
        log.info("Calculating total expenses for category: '{}'", categoryName);
        BigDecimal total = expenseRepository.sumTotalByCategory(categoryName);
        log.info("Total expenses for category '{}': {}", categoryName, total);
        return total;
    }

    /**
     * Deletes an expense by ID.
     *
     * @param id expense ID to delete
     * @throws ExpenseNotFoundException if expense with ID does not exist
     */
    @Override
    @Transactional
    public void deleteExpense(Long id) {
        log.info("Attempting to delete expense with ID: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to delete expense: Expense not found with ID: {}", id);
                    return new ExpenseNotFoundException("Expense not found with id: " + id);
                });

        expenseRepository.delete(expense);
        log.info("Successfully deleted expense with ID: {}", id);
    }
}
