package org.bbqqvv.backendecommerce.service.impl;

import io.jsonwebtoken.io.IOException;
import org.bbqqvv.backendecommerce.config.ImgBBConfig;
import org.bbqqvv.backendecommerce.dto.CategoryDto;
import org.bbqqvv.backendecommerce.entity.Category;
import org.bbqqvv.backendecommerce.exception.CategoryNotFoundException;
import org.bbqqvv.backendecommerce.repository.CategoryRepository;
import org.bbqqvv.backendecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ImgBBConfig imgBBConfig;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ImgBBConfig imgBBConfig) {
        this.categoryRepository = categoryRepository;
        this.imgBBConfig = imgBBConfig;
    }

    @Override
    public Category createCategory(CategoryDto categoryDto) {
        try {
            Category category = new Category();
            category.setSlug(categoryDto.getSlug());
            category.setName(categoryDto.getName());
            // Upload ảnh lên ImgBB và lưu URL
            if (categoryDto.getImage() != null && !categoryDto.getImage().isEmpty()) {
                String imageUrl = imgBBConfig.uploadImage(categoryDto.getImage());
                category.setImage(imageUrl);
            }
            return categoryRepository.save(category);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Long id, CategoryDto categoryDto) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + id));
            category.setSlug(categoryDto.getSlug());
            category.setName(categoryDto.getName());

            // Upload ảnh mới (nếu có)
            if (categoryDto.getImage() != null && !categoryDto.getImage().isEmpty()) {
                String imageUrl = imgBBConfig.uploadImage(categoryDto.getImage());
                category.setImage(imageUrl);
            }

            return categoryRepository.save(category);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }

    @Override
    public boolean deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + id));
        categoryRepository.delete(category);
        return true;
    }
}
