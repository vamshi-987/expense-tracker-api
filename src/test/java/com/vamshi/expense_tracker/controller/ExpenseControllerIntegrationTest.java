package com.vamshi.expense_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.entity.Expense;
import com.vamshi.expense_tracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ExpenseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExpenseRepository expenseRepository;

    @BeforeEach
    void cleanDatabase() {
        expenseRepository.deleteAll();
    }

    @Test
    void shouldCreateExpense() throws Exception {

        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Lunch");
        request.setAmount(BigDecimal.valueOf(250));
        request.setCategory("Food");
        request.setDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Lunch"))
                .andExpect(jsonPath("$.amount").value(250))
                .andExpect(jsonPath("$.category").value("Food"));
    }

    @Test
    void shouldReturnAllExpenses() throws Exception {

        expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category("Food")
                        .date(LocalDate.now())
                        .build());

        expenseRepository.save(
                Expense.builder()
                        .title("Petrol")
                        .amount(BigDecimal.valueOf(500))
                        .category("Travel")
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(get("/api/v1/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnExpensesByCategory() throws Exception {

        expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category("Food")
                        .date(LocalDate.now())
                        .build());

        expenseRepository.save(
                Expense.builder()
                        .title("Fuel")
                        .amount(BigDecimal.valueOf(400))
                        .category("Travel")
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(get("/api/v1/expenses/category/Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("Food"));
    }

    @Test
    void shouldReturnTotalExpenses() throws Exception {

        expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category("Food")
                        .date(LocalDate.now())
                        .build());

        expenseRepository.save(
                Expense.builder()
                        .title("Fuel")
                        .amount(BigDecimal.valueOf(500))
                        .category("Travel")
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(get("/api/v1/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("750"));
    }

    @Test
    void shouldDeleteExpense() throws Exception {

        Expense savedExpense = expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category("Food")
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(delete("/api/v1/expenses/" + savedExpense.getId()))
                .andExpect(status().isNoContent());

        assertFalse(
                expenseRepository.findById(savedExpense.getId()).isPresent()
        );
    }

    @Test
    void shouldReturn404WhenExpenseNotFound() throws Exception {

        mockMvc.perform(delete("/api/v1/expenses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Expense not found with id: 999"));
    }

    @Test
    void shouldReturnValidationErrors() throws Exception {

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("");
        request.setAmount(BigDecimal.valueOf(-100));
        request.setCategory("");
        request.setDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.title").exists())
                .andExpect(jsonPath("$.validationErrors.amount").exists())
                .andExpect(jsonPath("$.validationErrors.category").exists())
                .andExpect(jsonPath("$.validationErrors.date").exists());
    }
}