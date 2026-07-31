package com.vamshi.expense_tracker.service.impl;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import com.vamshi.expense_tracker.entity.Expense;
import com.vamshi.expense_tracker.exception.ExpenseNotFoundException;
import com.vamshi.expense_tracker.mapper.ExpenseMapper;
import com.vamshi.expense_tracker.repository.ExpenseRepository;
import com.vamshi.expense_tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final ExpenseMapper expenseMapper;

//    public ExpenseServiceImpl(
//            ExpenseRepository repository,
//            ExpenseMapper mapper
//    )

    @Override
    public ExpenseResponse addExpense(ExpenseRequest request) {

        Expense expense = expenseMapper.toEntity(request);

        Expense savedExpense = expenseRepository.save(expense);

        return expenseMapper.toResponse(savedExpense);

    }

    @Override
    public List<ExpenseResponse> getAllExpenses() {

        List<Expense> expenses = expenseRepository.findAll();

        return expenseMapper.toResponseList(expenses);

    }

    @Override
    public List<ExpenseResponse> getExpensesByCategory(String category) {

        return expenseMapper.toResponseList(
                expenseRepository.findByCategoryIgnoreCase(category)
        );

    }

    @Override
    public BigDecimal getTotalExpenses() {

        return expenseRepository.findAll()

                .stream()

                .map(Expense::getAmount)

                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    @Override
    public BigDecimal getTotalByCategory(String category) {

        return expenseRepository.findByCategoryIgnoreCase(category)

                .stream()

                .map(Expense::getAmount)

                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    @Override
    public void deleteExpense(Long id) {

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new ExpenseNotFoundException(
                                "Expense with id " + id + " not found"));

        expenseRepository.delete(expense);

    }

}
