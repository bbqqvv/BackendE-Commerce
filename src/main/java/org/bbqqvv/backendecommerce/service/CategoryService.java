package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.CategoryDto;
import org.bbqqvv.backendecommerce.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryDto categoryDto);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    Category updateCategory(Long id, CategoryDto categoryDto);
    boolean deleteCategory(Long id);
}
