package com.vamshi.expense_tracker.mapper;

import com.vamshi.expense_tracker.dto.ExpenseRequest;
import com.vamshi.expense_tracker.dto.ExpenseResponse;
import com.vamshi.expense_tracker.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Expense toEntity(ExpenseRequest request);

    @Mapping(source = "category.name", target = "category")
    @Mapping(source = "category.id", target = "categoryId")
    ExpenseResponse toResponse(Expense expense);

    List<ExpenseResponse> toResponseList(List<Expense> expenses);
}
