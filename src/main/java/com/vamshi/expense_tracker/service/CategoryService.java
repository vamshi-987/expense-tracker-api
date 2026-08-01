package com.vamshi.expense_tracker.service;

import com.vamshi.expense_tracker.dto.CategoryRequest;
import com.vamshi.expense_tracker.dto.CategoryResponse;
import com.vamshi.expense_tracker.dto.DeleteCategoryResponse;

import java.util.List;

/**
 * Service interface for managing category operations.
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param request category payload
     * @return created category response
     */
    CategoryResponse addCategory(CategoryRequest request);

    /**
     * Retrieves all categories.
     *
     * @return list of category responses
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Retrieves a category by ID.
     *
     * @param id category ID
     * @return category response
     */
    CategoryResponse getCategoryById(Long id);

    /**
     * Deletes a category by ID along with all associated expenses.
     *
     * @param id category ID
     * @return delete category response with confirmation message and deleted category details
     */
    DeleteCategoryResponse deleteCategory(Long id);
}
