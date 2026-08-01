package com.vamshi.expense_tracker.service.impl;

import com.vamshi.expense_tracker.dto.DeleteExpenseResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of {@link ExpenseService} managing transactions, category resolution, pagination, sorting, search, and logging.
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
     * Creates and persists a new expense entry.
     *
     * @param request expense creation details
     * @return created expense response with generated ID
     * @throws CategoryNotFoundException if the specified category does not exist
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
     * Retrieves all recorded expenses (unpaged).
     *
     * @return list of all expense responses
     */
    @Override
    public List<ExpenseResponse> getAllExpenses() {
        log.info("Fetching all expenses (unpaged)");
        List<Expense> expenses = expenseRepository.findAll();
        log.debug("Found {} total expenses", expenses.size());
        return expenseMapper.toResponseList(expenses);
    }

    /**
     * Retrieves all recorded expenses with pagination and sorting.
     *
     * @param pageNo   page number (0-indexed)
     * @param pageSize page size
     * @param sortBy   sort property name
     * @param sortDir  sort direction ("asc" or "desc")
     * @return page of expense responses
     */
    @Override
    public Page<ExpenseResponse> getAllExpenses(int pageNo, int pageSize, String sortBy, String sortDir) {
        log.info("Fetching paged expenses: page={}, size={}, sortBy={}, sortDir={}", pageNo, pageSize, sortBy, sortDir);
        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Expense> expensePage = expenseRepository.findAll(pageable);
        return expensePage.map(expenseMapper::toResponse);
    }

    /**
     * Retrieves expenses matching the given category name (case-insensitive, unpaged).
     *
     * @param category category name
     * @return list of matching expense responses
     */
    @Override
    public List<ExpenseResponse> getExpensesByCategory(String category) {
        String categoryName = category != null ? category.trim() : "";
        log.info("Fetching expenses for category (unpaged): '{}'", categoryName);
        List<Expense> expenses = expenseRepository.findByCategory_NameIgnoreCase(categoryName);
        return expenseMapper.toResponseList(expenses);
    }

    /**
     * Retrieves expenses matching the given category name with pagination and sorting.
     *
     * @param category category name
     * @param pageNo   page number
     * @param pageSize page size
     * @param sortBy   sort property name
     * @param sortDir  sort direction
     * @return page of matching expense responses
     */
    @Override
    public Page<ExpenseResponse> getExpensesByCategory(String category, int pageNo, int pageSize, String sortBy, String sortDir) {
        String categoryName = category != null ? category.trim() : "";
        log.info("Fetching paged expenses for category: '{}', page={}, size={}, sortBy={}, sortDir={}",
                categoryName, pageNo, pageSize, sortBy, sortDir);

        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Expense> expensePage = expenseRepository.findByCategory_NameIgnoreCase(categoryName, pageable);
        return expensePage.map(expenseMapper::toResponse);
    }

    /**
     * Searches expenses matching query keyword in title or category with pagination and sorting.
     *
     * @param query    search keyword
     * @param pageNo   page number
     * @param pageSize page size
     * @param sortBy   sort property name
     * @param sortDir  sort direction
     * @return page of matching expense responses
     */
    @Override
    public Page<ExpenseResponse> searchExpenses(String query, int pageNo, int pageSize, String sortBy, String sortDir) {
        String keyword = query != null ? query.trim() : "";
        log.info("Searching expenses matching query: '{}', page={}, size={}, sortBy={}, sortDir={}",
                keyword, pageNo, pageSize, sortBy, sortDir);

        Pageable pageable = createPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Expense> expensePage = expenseRepository.findByTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(
                keyword, keyword, pageable);

        return expensePage.map(expenseMapper::toResponse);
    }

    /**
     * Calculates total sum of all recorded expenses.
     *
     * @return total expense sum
     */
    @Override
    public BigDecimal getTotalExpenses() {
        log.info("Calculating total expenses overall");
        BigDecimal total = expenseRepository.sumTotalExpenses();
        log.info("Overall total expenses calculated: {}", total);
        return total;
    }

    /**
     * Calculates total sum of expenses within a specific category.
     *
     * @param category category name
     * @return total expense sum for category
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
     * Deletes an expense entry by ID.
     *
     * @param id expense ID
     * @return delete expense response with confirmation message and deleted expense details
     * @throws ExpenseNotFoundException if expense does not exist
     */
    @Override
    @Transactional
    public DeleteExpenseResponse deleteExpense(Long id) {
        log.info("Attempting to delete expense with ID: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to delete expense: Expense not found with ID: {}", id);
                    return new ExpenseNotFoundException("Expense not found with id: " + id);
                });

        ExpenseResponse expenseResponse = expenseMapper.toResponse(expense);
        expenseRepository.delete(expense);
        log.info("Successfully deleted expense with ID: {}", id);

        return DeleteExpenseResponse.builder()
                .message("Expense deleted successfully")
                .deletedExpense(expenseResponse)
                .build();
    }

    private Pageable createPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = sortBy.equalsIgnoreCase("category") ? "category.name" : sortBy;
        return PageRequest.of(pageNo, pageSize, Sort.by(direction, sortProperty));
    }
}
