package com.vamshi.expense_tracker.service;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseService {

    ExpenseResponse addExpense(ExpenseRequest request);

    List<ExpenseResponse> getAllExpenses();

    List<ExpenseResponse> getExpensesByCategory(String category);

    BigDecimal getTotalExpenses();

    BigDecimal getTotalByCategory(String category);

    void deleteExpense(Long id);

}
