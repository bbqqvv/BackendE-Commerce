package org.bbqqvv.backendecommerce.controller;

import org.bbqqvv.backendecommerce.dto.CategoryDto;
import org.bbqqvv.backendecommerce.entity.Category;
import org.bbqqvv.backendecommerce.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Tạo mới một danh mục
    @PostMapping
    public ResponseEntity<Category> createCategory(@ModelAttribute CategoryDto categoryDto) {
        Category createdCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    // Lấy danh sách tất cả các danh mục
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Lấy danh mục theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    // Cập nhật thông tin danh mục theo ID
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @ModelAttribute CategoryDto categoryDto) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDto);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // Xóa một danh mục theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        boolean isDeleted = categoryService.deleteCategory(id);
        return new ResponseEntity<>(isDeleted ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
    }
}
