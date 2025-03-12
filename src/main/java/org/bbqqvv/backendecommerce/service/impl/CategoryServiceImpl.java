package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.ImgBBConfig;
import org.bbqqvv.backendecommerce.dto.request.CategoryRequest;
import org.bbqqvv.backendecommerce.dto.response.CategoryResponse;
import org.bbqqvv.backendecommerce.entity.Category;
import org.bbqqvv.backendecommerce.entity.SizeCategory;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.CategoryMapper;
import org.bbqqvv.backendecommerce.mapper.SizeMapper;
import org.bbqqvv.backendecommerce.repository.CategoryRepository;
import org.bbqqvv.backendecommerce.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    SizeMapper sizeMapper;
    ImgBBConfig imgBBConfig;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ImgBBConfig imgBBConfig, CategoryMapper categoryMapper, SizeMapper sizeMapper) {
        this.categoryRepository = categoryRepository;
        this.imgBBConfig = imgBBConfig;
        this.categoryMapper = categoryMapper;
        this.sizeMapper = sizeMapper;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        try {
            if (categoryRepository.existsCategoriesByName(categoryRequest.getName())) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            // Xử lý upload ảnh
            String imageUrl = handleImageUpload(categoryRequest.getImage());

            // Chuyển từ CategoryRequest sang Category
            Category category = categoryMapper.categoryRequestToCategory(categoryRequest);
            category.setImage(imageUrl); // Gán URL ảnh

            if (categoryRequest.getSizes() != null && !categoryRequest.getSizes().isEmpty()) {
                List<SizeCategory> sizeCategories = categoryRequest.getSizes().stream()
                        .map(sizeMapper::toSize)
                        .peek(size -> size.setCategory(category))
                        .collect(Collectors.toList());
                category.setSizeCategories(sizeCategories);
            }

            // Lưu thực thể Category
            Category savedCategory = categoryRepository.save(category);

            // Chuyển sang DTO để trả về
            return categoryMapper.categoryToCategoryResponse(savedCategory);
        } catch (IOException e) {
            log.error("Lỗi khi tải ảnh", e);
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }


    @Override
    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::categoryToCategoryResponse)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::categoryToCategoryResponse)
                .collect(Collectors.toList());
    }
    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)); // Dùng AppException với ErrorCode

            // Xử lý upload ảnh mới
            String imageUrl = handleImageUpload(categoryRequest.getImage());

            // Cập nhật thông tin danh mục
            category.setSlug(categoryRequest.getSlug());
            category.setName(categoryRequest.getName());
            category.setImage(imageUrl); // Gán URL ảnh mới

            // Xử lý danh sách sizes
            if (categoryRequest.getSizes() != null && !categoryRequest.getSizes().isEmpty()) {
                List<SizeCategory> sizeCategories = categoryRequest.getSizes().stream()
                        .map(sizeMapper::toSize)
                        .peek(size -> size.setCategory(category)) // Gán Category cho Size
                        .collect(Collectors.toList());
                category.setSizeCategories(sizeCategories); // Gán danh sách sizes vào category
            }

            // Lưu lại danh mục đã cập nhật
            Category updatedCategory = categoryRepository.save(category);

            // Chuyển sang DTO để trả về
            return categoryMapper.categoryToCategoryResponse(updatedCategory);
        } catch (IOException e) {
            log.error("Lỗi khi tải ảnh", e);
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED); // Dùng AppException khi lỗi upload ảnh
        }
    }


    @Override
    public boolean deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)); // Dùng AppException với ErrorCode
        categoryRepository.delete(category);
        return true;
    }
    private List<SizeCategory> mapSizeCategories(CategoryRequest categoryRequest, Category category) {
        return categoryRequest.getSizes() == null ? List.of() : categoryRequest.getSizes().stream()
                .map(sizeMapper::toSize)
                .peek(size -> size.setCategory(category))
                .collect(Collectors.toList());
    }
    private String handleImageUpload(MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            return imgBBConfig.uploadImage(image);
        }
        return null;
    }
}
