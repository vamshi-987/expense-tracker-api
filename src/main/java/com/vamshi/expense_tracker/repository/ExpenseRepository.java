package com.vamshi.expense_tracker.repository;

import com.vamshi.expense_tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategory_NameIgnoreCase(String categoryName);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    BigDecimal sumTotalExpenses();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE LOWER(e.category.name) = LOWER(:categoryName)")
    BigDecimal sumTotalByCategory(@Param("categoryName") String categoryName);
}
