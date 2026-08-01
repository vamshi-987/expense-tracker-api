package com.vamshi.expense_tracker.mapper;

import com.vamshi.expense_tracker.dto.CategoryRequest;
import com.vamshi.expense_tracker.dto.CategoryResponse;
import com.vamshi.expense_tracker.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
