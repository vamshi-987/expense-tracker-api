package com.vamshi.expense_tracker.service;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import com.vamshi.expense_tracker.entity.Category;
import com.vamshi.expense_tracker.entity.Expense;
import com.vamshi.expense_tracker.exception.CategoryNotFoundException;
import com.vamshi.expense_tracker.exception.ExpenseNotFoundException;
import com.vamshi.expense_tracker.mapper.ExpenseMapper;
import com.vamshi.expense_tracker.repository.CategoryRepository;
import com.vamshi.expense_tracker.repository.ExpenseRepository;
import com.vamshi.expense_tracker.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Category category;
    private Expense expense;
    private ExpenseRequest request;
    private ExpenseResponse response;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("food").build();

        request = new ExpenseRequest();
        request.setTitle("Lunch");
        request.setAmount(BigDecimal.valueOf(250));
        request.setCategory("food");
        request.setDate(LocalDate.now());

        expense = new Expense();
        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(BigDecimal.valueOf(250));
        expense.setCategory(category);
        expense.setDate(LocalDate.now());

        response = new ExpenseResponse();
        response.setId(1L);
        response.setTitle("Lunch");
        response.setAmount(BigDecimal.valueOf(250));
        response.setCategory("food");
        response.setCategoryId(1L);
        response.setDate(LocalDate.now());
    }

    @Test
    void shouldAddExpenseSuccessfullyWhenCategoryExistsCaseInsensitively() {
        when(categoryRepository.findByNameIgnoreCase("food"))
                .thenReturn(Optional.of(category));

        when(expenseMapper.toEntity(request))
                .thenReturn(expense);

        when(expenseRepository.save(expense))
                .thenReturn(expense);

        when(expenseMapper.toResponse(expense))
                .thenReturn(response);

        ExpenseResponse result = expenseService.addExpense(request);

        assertNotNull(result);
        assertEquals("Lunch", result.getTitle());

        verify(categoryRepository).findByNameIgnoreCase("food");
        verify(expenseMapper).toEntity(request);
        verify(expenseRepository).save(expense);
        verify(expenseMapper).toResponse(expense);
    }

    @Test
    void shouldThrowCategoryNotFoundExceptionWhenCategoryDoesNotExist() {
        request.setCategory("NonExistentCategory");

        when(categoryRepository.findByNameIgnoreCase("NonExistentCategory"))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> expenseService.addExpense(request)
        );

        assertEquals("Category not found: NonExistentCategory", exception.getMessage());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllExpenses() {
        List<Expense> expenses = List.of(expense);
        List<ExpenseResponse> responses = List.of(response);

        when(expenseRepository.findAll())
                .thenReturn(expenses);

        when(expenseMapper.toResponseList(expenses))
                .thenReturn(responses);

        List<ExpenseResponse> result = expenseService.getAllExpenses();

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getTitle());

        verify(expenseRepository).findAll();
        verify(expenseMapper).toResponseList(expenses);
    }

    @Test
    void shouldReturnPagedExpenses() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "date"));
        PageImpl<Expense> expensePage = new PageImpl<>(List.of(expense), pageable, 1);

        when(expenseRepository.findAll(pageable))
                .thenReturn(expensePage);

        when(expenseMapper.toResponse(expense))
                .thenReturn(response);

        Page<ExpenseResponse> result = expenseService.getAllExpenses(0, 10, "date", "desc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getNumber());
    }

    @Test
    void shouldSearchExpensesWithPagination() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "date"));
        PageImpl<Expense> expensePage = new PageImpl<>(List.of(expense), pageable, 1);

        when(expenseRepository.findByTitleContainingIgnoreCaseOrCategory_NameContainingIgnoreCase("Lunch", "Lunch", pageable))
                .thenReturn(expensePage);

        when(expenseMapper.toResponse(expense))
                .thenReturn(response);

        Page<ExpenseResponse> result = expenseService.searchExpenses("Lunch", 0, 10, "date", "desc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Lunch", result.getContent().get(0).getTitle());
    }

    @Test
    void shouldReturnExpensesByCategoryCaseInsensitively() {
        when(expenseRepository.findByCategory_NameIgnoreCase("FOOD"))
                .thenReturn(List.of(expense));

        when(expenseMapper.toResponseList(List.of(expense)))
                .thenReturn(List.of(response));

        List<ExpenseResponse> result = expenseService.getExpensesByCategory("FOOD");

        assertEquals(1, result.size());
        assertEquals("food", result.get(0).getCategory());

        verify(expenseRepository).findByCategory_NameIgnoreCase("FOOD");
        verify(expenseMapper).toResponseList(List.of(expense));
    }

    @Test
    void shouldCalculateTotalExpensesViaDatabaseQuery() {
        when(expenseRepository.sumTotalExpenses())
                .thenReturn(BigDecimal.valueOf(250));

        BigDecimal total = expenseService.getTotalExpenses();

        assertEquals(BigDecimal.valueOf(250), total);

        verify(expenseRepository).sumTotalExpenses();
    }

    @Test
    void shouldCalculateCategoryTotalCaseInsensitivelyViaDatabaseQuery() {
        when(expenseRepository.sumTotalByCategory("food"))
                .thenReturn(BigDecimal.valueOf(250));

        BigDecimal total = expenseService.getTotalByCategory("food");

        assertEquals(BigDecimal.valueOf(250), total);

        verify(expenseRepository).sumTotalByCategory("food");
    }

    @Test
    void shouldDeleteExpense() {
        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));
        when(expenseMapper.toResponse(expense))
                .thenReturn(response);

        com.vamshi.expense_tracker.dto.DeleteExpenseResponse deleted = expenseService.deleteExpense(1L);

        assertNotNull(deleted);
        assertEquals("Expense deleted successfully", deleted.getMessage());
        assertEquals(1L, deleted.getDeletedExpense().getId());
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).delete(expense);
    }

    @Test
    void shouldThrowExceptionWhenExpenseNotFound() {
        when(expenseRepository.findById(1L))
                .thenReturn(Optional.empty());

        ExpenseNotFoundException exception = assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.deleteExpense(1L)
        );

        assertEquals("Expense not found with id: 1", exception.getMessage());

        verify(expenseRepository).findById(1L);
        verify(expenseRepository, never()).delete(any());
    }
}