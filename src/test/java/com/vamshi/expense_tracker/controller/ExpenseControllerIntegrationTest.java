package com.vamshi.expense_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.entity.Category;
import com.vamshi.expense_tracker.entity.Expense;
import com.vamshi.expense_tracker.repository.CategoryRepository;
import com.vamshi.expense_tracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private Category getOrCreateCategory(String name) {
        String normalizedName = name.trim().toLowerCase();
        return categoryRepository.findByNameIgnoreCase(normalizedName)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(normalizedName).build()));
    }

    @Test
    void shouldCreateExpenseSuccessfully() throws Exception {
        getOrCreateCategory("Food");

        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Lunch");
        request.setAmount(BigDecimal.valueOf(250.50));
        request.setCategory("Food");
        request.setDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Lunch"))
                .andExpect(jsonPath("$.amount").value(250.50))
                .andExpect(jsonPath("$.category").value("food"));
    }

    @Test
    void shouldReturn404WhenAddingExpenseWithNonExistentCategory() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Lunch");
        request.setAmount(BigDecimal.valueOf(250));
        request.setCategory("UnknownCategory");
        request.setDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Category not found: UnknownCategory"))
                .andExpect(jsonPath("$.validationErrors.category")
                        .value("Category not found: UnknownCategory"));
    }

    @Test
    void shouldReturn404WhenAddingExpenseWithCategoryNameAlias() throws Exception {
        String jsonRequest = """
                {
                  "title": "Lunch",
                  "amount": 250,
                  "categoryName": "UnknownCategory",
                  "date": "2026-08-01"
                }
                """;

        mockMvc.perform(post("/api/v1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Category not found: UnknownCategory"))
                .andExpect(jsonPath("$.validationErrors.category")
                        .value("Category not found: UnknownCategory"));
    }

    @Test
    void shouldReturnAllExpenses() throws Exception {
        expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category(getOrCreateCategory("Food"))
                        .date(LocalDate.now())
                        .build());

        expenseRepository.save(
                Expense.builder()
                        .title("Petrol")
                        .amount(BigDecimal.valueOf(500))
                        .category(getOrCreateCategory("Travel"))
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(get("/api/v1/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnPagedExpenses() throws Exception {
        expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category(getOrCreateCategory("Food"))
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(get("/api/v1/expenses/paged?page=0&size=10&sortBy=amount&sortDir=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void shouldSearchExpensesByKeyword() throws Exception {
        expenseRepository.save(
                Expense.builder()
                        .title("Flight Ticket")
                        .amount(BigDecimal.valueOf(5000))
                        .category(getOrCreateCategory("Travel"))
                        .date(LocalDate.now())
                        .build());

        expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category(getOrCreateCategory("Food"))
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(get("/api/v1/expenses/search?query=Flight"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Flight Ticket"));
    }

    @Test
    void shouldReturnExpensesByCategoryCaseInsensitively() throws Exception {
        expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category(getOrCreateCategory("Food"))
                        .date(LocalDate.now())
                        .build());

        expenseRepository.save(
                Expense.builder()
                        .title("Fuel")
                        .amount(BigDecimal.valueOf(400))
                        .category(getOrCreateCategory("Travel"))
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(get("/api/v1/expenses/category/FOOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("food"));
    }

    @Test
    void shouldReturnTotalExpenses() throws Exception {
        expenseRepository.save(
                Expense.builder()
                        .title("Lunch")
                        .amount(BigDecimal.valueOf(250))
                        .category(getOrCreateCategory("Food"))
                        .date(LocalDate.now())
                        .build());

        expenseRepository.save(
                Expense.builder()
                        .title("Fuel")
                        .amount(BigDecimal.valueOf(500))
                        .category(getOrCreateCategory("Travel"))
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(get("/api/v1/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(750));
    }

    @Test
    void shouldDeleteExpense() throws Exception {
        Expense expense = expenseRepository.save(
                Expense.builder()
                        .title("Movie")
                        .amount(BigDecimal.valueOf(300))
                        .category(getOrCreateCategory("Entertainment"))
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(delete("/api/v1/expenses/" + expense.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Expense deleted successfully"))
                .andExpect(jsonPath("$.deletedExpense.id").value(expense.getId()))
                .andExpect(jsonPath("$.deletedExpense.title").value("Movie"))
                .andExpect(jsonPath("$.deletedExpense.amount").value(300))
                .andExpect(jsonPath("$.deletedExpense.category").value("entertainment"));
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

    @Test
    void shouldDeleteCategoryAndAllJoinedExpenses() throws Exception {
        Category category = getOrCreateCategory("Entertainment");

        expenseRepository.save(
                Expense.builder()
                        .title("Movie Ticket")
                        .amount(BigDecimal.valueOf(15))
                        .category(category)
                        .date(LocalDate.now())
                        .build());

        expenseRepository.save(
                Expense.builder()
                        .title("Concert Ticket")
                        .amount(BigDecimal.valueOf(100))
                        .category(category)
                        .date(LocalDate.now())
                        .build());

        mockMvc.perform(delete("/api/v1/categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category and all its associated expenses deleted successfully"))
                .andExpect(jsonPath("$.deletedCategory.id").value(category.getId()))
                .andExpect(jsonPath("$.deletedCategory.name").value("entertainment"));

        org.junit.jupiter.api.Assertions.assertFalse(categoryRepository.existsById(category.getId()));
        org.junit.jupiter.api.Assertions.assertEquals(0, expenseRepository.findByCategory_NameIgnoreCase("Entertainment").size());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCategory() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Category not found with id: 9999"));
    }
}