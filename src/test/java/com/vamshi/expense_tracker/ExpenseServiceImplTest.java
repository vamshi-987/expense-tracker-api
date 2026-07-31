package com.vamshi.expense_tracker;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import com.vamshi.expense_tracker.entity.Expense;
import com.vamshi.expense_tracker.exception.ExpenseNotFoundException;
import com.vamshi.expense_tracker.mapper.ExpenseMapper;
import com.vamshi.expense_tracker.repository.ExpenseRepository;
import com.vamshi.expense_tracker.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Expense expense;
    private ExpenseRequest request;
    private ExpenseResponse response;

    @BeforeEach
    void setUp() {

        request = new ExpenseRequest();
        request.setTitle("Lunch");
        request.setAmount(BigDecimal.valueOf(250));
        request.setCategory("Food");
        request.setDate(LocalDate.now());

        expense = new Expense();
        expense.setId(1L);
        expense.setTitle("Lunch");
        expense.setAmount(BigDecimal.valueOf(250));
        expense.setCategory("Food");
        expense.setDate(LocalDate.now());

        response = new ExpenseResponse();
        response.setId(1L);
        response.setTitle("Lunch");
        response.setAmount(BigDecimal.valueOf(250));
        response.setCategory("Food");
        response.setDate(LocalDate.now());
    }

    @Test
    void shouldAddExpenseSuccessfully() {

        when(expenseMapper.toEntity(request))
                .thenReturn(expense);

        when(expenseRepository.save(expense))
                .thenReturn(expense);

        when(expenseMapper.toResponse(expense))
                .thenReturn(response);

        ExpenseResponse result = expenseService.addExpense(request);

        assertNotNull(result);
        assertEquals("Lunch", result.getTitle());

        verify(expenseMapper).toEntity(request);
        verify(expenseRepository).save(expense);
        verify(expenseMapper).toResponse(expense);
    }

    @Test
    void shouldReturnAllExpenses() {

        List<Expense> expenses = List.of(expense);
        List<ExpenseResponse> responses = List.of(response);

        when(expenseRepository.findAll())
                .thenReturn(expenses);

        when(expenseMapper.toResponseList(expenses))
                .thenReturn(responses);

        List<ExpenseResponse> result =
                expenseService.getAllExpenses();

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getTitle());

        verify(expenseRepository).findAll();
        verify(expenseMapper).toResponseList(expenses);
    }

    @Test
    void shouldReturnExpensesByCategory() {

        when(expenseRepository.findByCategoryIgnoreCase("Food"))
                .thenReturn(List.of(expense));

        when(expenseMapper.toResponseList(List.of(expense)))
                .thenReturn(List.of(response));

        List<ExpenseResponse> result =
                expenseService.getExpensesByCategory("Food");

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategory());

        verify(expenseRepository)
                .findByCategoryIgnoreCase("Food");

        verify(expenseMapper)
                .toResponseList(List.of(expense));
    }

    @Test
    void shouldCalculateTotalExpenses() {

        when(expenseRepository.findAll())
                .thenReturn(List.of(expense));

        BigDecimal total =
                expenseService.getTotalExpenses();

        assertEquals(BigDecimal.valueOf(250), total);

        verify(expenseRepository).findAll();
    }

    @Test
    void shouldCalculateCategoryTotal() {

        when(expenseRepository.findByCategoryIgnoreCase("Food"))
                .thenReturn(List.of(expense));

        BigDecimal total =
                expenseService.getTotalByCategory("Food");

        assertEquals(BigDecimal.valueOf(250), total);

        verify(expenseRepository)
                .findByCategoryIgnoreCase("Food");
    }

    @Test
    void shouldDeleteExpense() {

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));

        expenseService.deleteExpense(1L);

        verify(expenseRepository).findById(1L);
        verify(expenseRepository).delete(expense);
    }

    @Test
    void shouldThrowExceptionWhenExpenseNotFound() {

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.empty());

        ExpenseNotFoundException exception =
                assertThrows(
                        ExpenseNotFoundException.class,
                        () -> expenseService.deleteExpense(1L)
                );

        assertEquals(
                "Expense not found with id: 1",
                exception.getMessage()
        );

        verify(expenseRepository).findById(1L);
        verify(expenseRepository, never()).delete(any());
    }

    @Test
    void shouldReturnEmptyListWhenNoExpensesExist() {

        when(expenseRepository.findAll())
                .thenReturn(List.of());

        when(expenseMapper.toResponseList(List.of()))
                .thenReturn(List.of());

        List<ExpenseResponse> result =
                expenseService.getAllExpenses();

        assertTrue(result.isEmpty());

        verify(expenseRepository).findAll();
        verify(expenseMapper).toResponseList(List.of());
    }

    @Test
    void shouldReturnEmptyListWhenCategoryDoesNotExist() {

        when(expenseRepository.findByCategoryIgnoreCase("Travel"))
                .thenReturn(List.of());

        when(expenseMapper.toResponseList(List.of()))
                .thenReturn(List.of());

        List<ExpenseResponse> result =
                expenseService.getExpensesByCategory("Travel");

        assertTrue(result.isEmpty());

        verify(expenseRepository)
                .findByCategoryIgnoreCase("Travel");

        verify(expenseMapper)
                .toResponseList(List.of());
    }

    @Test
    void shouldReturnZeroWhenNoExpensesExist() {

        when(expenseRepository.findAll())
                .thenReturn(List.of());

        BigDecimal total =
                expenseService.getTotalExpenses();

        assertEquals(BigDecimal.ZERO, total);

        verify(expenseRepository).findAll();
    }

    @Test
    void shouldReturnZeroWhenCategoryHasNoExpenses() {

        when(expenseRepository.findByCategoryIgnoreCase("Food"))
                .thenReturn(List.of());

        BigDecimal total =
                expenseService.getTotalByCategory("Food");

        assertEquals(BigDecimal.ZERO, total);

        verify(expenseRepository)
                .findByCategoryIgnoreCase("Food");
    }

    @Test
    void shouldCalculateTotalForMultipleExpenses() {

        Expense second = new Expense();
        second.setAmount(BigDecimal.valueOf(750));

        when(expenseRepository.findAll())
                .thenReturn(List.of(expense, second));

        BigDecimal total =
                expenseService.getTotalExpenses();

        assertEquals(BigDecimal.valueOf(1000), total);

        verify(expenseRepository).findAll();
    }

    @Test
    void shouldCalculateCategoryTotalForMultipleExpenses() {

        Expense second = new Expense();
        second.setCategory("Food");
        second.setAmount(BigDecimal.valueOf(500));

        when(expenseRepository.findByCategoryIgnoreCase("Food"))
                .thenReturn(List.of(expense, second));

        BigDecimal total =
                expenseService.getTotalByCategory("Food");

        assertEquals(BigDecimal.valueOf(750), total);

        verify(expenseRepository)
                .findByCategoryIgnoreCase("Food");
    }

    @Test
    void shouldMapRequestToEntityAndBack() {

        when(expenseMapper.toEntity(request))
                .thenReturn(expense);

        when(expenseRepository.save(expense))
                .thenReturn(expense);

        when(expenseMapper.toResponse(expense))
                .thenReturn(response);

        expenseService.addExpense(request);

        verify(expenseMapper).toEntity(request);
        verify(expenseRepository).save(expense);
        verify(expenseMapper).toResponse(expense);
    }

    @Test
    void shouldFindExpenseBeforeDeleting() {

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));

        expenseService.deleteExpense(1L);

        verify(expenseRepository).findById(1L);
        verify(expenseRepository).delete(expense);
    }
}