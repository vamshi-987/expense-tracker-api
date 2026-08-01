package com.vamshi.expense_tracker.service.impl;

import com.vamshi.expense_tracker.dto.CategoryRequest;
import com.vamshi.expense_tracker.dto.CategoryResponse;
import com.vamshi.expense_tracker.dto.DeleteCategoryResponse;
import com.vamshi.expense_tracker.entity.Category;
import com.vamshi.expense_tracker.exception.CategoryAlreadyExistsException;
import com.vamshi.expense_tracker.exception.CategoryNotFoundException;
import com.vamshi.expense_tracker.mapper.CategoryMapper;
import com.vamshi.expense_tracker.repository.CategoryRepository;
import com.vamshi.expense_tracker.repository.ExpenseRepository;
import com.vamshi.expense_tracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing Category entities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Creates a new category entity.
     *
     * @param request category creation details
     * @return created category response
     * @throws CategoryAlreadyExistsException if a category with the same name already exists
     */
    @Override
    @Transactional
    public CategoryResponse addCategory(CategoryRequest request) {
        log.info("Creating new category with name: '{}'", request.getName());
        String normalizedName = request.getName().trim().toLowerCase();

        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            log.warn("Category creation failed: Category already exists with name '{}'", request.getName());
            throw new CategoryAlreadyExistsException("Category already exists with name: " + request.getName());
        }

        Category category = Category.builder()
                .name(normalizedName)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Successfully created category with ID: {} and name: '{}'", savedCategory.getId(), savedCategory.getName());

        return categoryMapper.toResponse(savedCategory);
    }

    /**
     * Retrieves all recorded categories.
     *
     * @return list of all category responses
     */
    @Override
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id category ID
     * @return matching category response
     * @throws CategoryNotFoundException if category is not found
     */
    @Override
    public CategoryResponse getCategoryById(Long id) {
        log.info("Fetching category by ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

    /**
     * Deletes a category by ID along with all joined expenses.
     *
     * @param id category ID
     * @return delete category response with confirmation message and deleted category details
     * @throws CategoryNotFoundException if category is not found
     */
    @Override
    @Transactional
    public DeleteCategoryResponse deleteCategory(Long id) {
        log.info("Attempting to delete category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to delete category: Category not found with ID: {}", id);
                    return new CategoryNotFoundException("Category not found with id: " + id);
                });

        CategoryResponse categoryResponse = categoryMapper.toResponse(category);
        expenseRepository.deleteByCategory_Id(id);
        categoryRepository.delete(category);
        log.info("Successfully deleted category with ID: {} and all joined expenses", id);

        return DeleteCategoryResponse.builder()
                .message("Category and all its associated expenses deleted successfully")
                .deletedCategory(categoryResponse)
                .build();
    }
}
