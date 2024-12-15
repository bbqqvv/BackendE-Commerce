package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.ImgBBConfig;
import org.bbqqvv.backendecommerce.dto.request.CategoryRequest;
import org.bbqqvv.backendecommerce.dto.response.CategoryResponse;
import org.bbqqvv.backendecommerce.entity.Category;
import org.bbqqvv.backendecommerce.exception.CategoryNotFoundException;
import org.bbqqvv.backendecommerce.mapper.CategoryMapper;
import org.bbqqvv.backendecommerce.repository.CategoryRepository;
import org.bbqqvv.backendecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; // Sửa import đúng
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    ImgBBConfig imgBBConfig;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ImgBBConfig imgBBConfig, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.imgBBConfig = imgBBConfig;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        try {
            // Xử lý upload ảnh
            String imageUrl = handleImageUpload(categoryRequest.getImage());

            // Chuyển từ CategoryRequest sang Category
            Category category = categoryMapper.categoryRequestDTOToCategory(categoryRequest);
            category.setImage(imageUrl); // Gán URL ảnh

            // Lưu thực thể Category
            Category savedCategory = categoryRepository.save(category);

            // Chuyển sang DTO để trả về
            return categoryMapper.categoryToCategoryResponseDTO(savedCategory);
        } catch (IOException e) {
            log.error("Lỗi khi tải ảnh", e);
            throw new RuntimeException("Không thể tải ảnh", e);
        }
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Không tìm thấy danh mục với ID: " + id));
        return categoryMapper.categoryToCategoryResponseDTO(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::categoryToCategoryResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException("Không tìm thấy danh mục với ID: " + id));

            // Xử lý upload ảnh mới
            String imageUrl = handleImageUpload(categoryRequest.getImage());

            // Cập nhật thông tin danh mục
            category.setSlug(categoryRequest.getSlug());
            category.setName(categoryRequest.getName());
            category.setImage(imageUrl); // Gán URL ảnh mới

            // Lưu lại danh mục đã cập nhật
            Category updatedCategory = categoryRepository.save(category);

            // Chuyển sang DTO để trả về
            return categoryMapper.categoryToCategoryResponseDTO(updatedCategory);
        } catch (IOException e) {
            log.error("Lỗi khi tải ảnh", e);
            throw new RuntimeException("Không thể tải ảnh", e);
        }
    }

    @Override
    public boolean deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Không tìm thấy danh mục với ID: " + id));
        categoryRepository.delete(category);
        return true;
    }

    private String handleImageUpload(MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            return imgBBConfig.uploadImage(image); // Thực hiện upload ảnh qua ImgBBConfig
        }
        return null; // Trả về null nếu không có ảnh
    }
}
