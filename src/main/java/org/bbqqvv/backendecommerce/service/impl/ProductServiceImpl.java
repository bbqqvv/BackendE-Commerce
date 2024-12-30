package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.ImgBBConfig;
import org.bbqqvv.backendecommerce.dto.response.ProductResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductRequest;
import org.bbqqvv.backendecommerce.entity.*;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.ProductMapper;
import org.bbqqvv.backendecommerce.mapper.VariantMapper;
import org.bbqqvv.backendecommerce.repository.CategoryRepository;
import org.bbqqvv.backendecommerce.repository.ProductRepository;
import org.bbqqvv.backendecommerce.service.ProductService;
import org.bbqqvv.backendecommerce.service.img.FileStorageService;
import org.bbqqvv.backendecommerce.util.SlugUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;
    VariantMapper variantMapper;
    ImgBBConfig imgBBConfig;
    FileStorageService fileStorageService;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper,
                              VariantMapper variantMapper,
                              ImgBBConfig imgBBConfig,
                              FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.variantMapper = variantMapper;
        this.imgBBConfig = imgBBConfig;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        if (productRepository.existsByProductCode(productRequest.getProductCode())) {
            throw new AppException(ErrorCode.DUPLICATE_PRODUCT_CODE);
        }
        Category category = getCategoryById(productRequest.getCategoryId());
        Product product = createOrUpdateProductEntity(productRequest, category);

        // Tạo slug duy nhất
        String slug = generateUniqueSlug(productRequest.getName());
        product.setSlug(slug);

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Category category = getCategoryById(productRequest.getCategoryId());
        Product product = createOrUpdateProductEntity(productRequest, category);
        product.setId(id);

        // Cập nhật slug nếu tên thay đổi
        if (!existingProduct.getName().equals(productRequest.getName())) {
            String uniqueSlug = generateUniqueSlug(productRequest.getName());
            product.setSlug(uniqueSlug);
        } else {
            product.setSlug(existingProduct.getSlug());
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(id);
        return true;
    }

    @Override
    public List<ProductResponse> findProductByCategorySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug);
        if (category == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        // Lấy danh sách sản phẩm của danh mục và chuyển sang ProductResponse
        return productRepository.findProductByCategory(category).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Product createOrUpdateProductEntity(ProductRequest productRequest, Category category) {
        Product product = productMapper.toProduct(productRequest);
        product.setCategory(category);
        handleImageUrls(product, productRequest);

        if (productRequest.getVariants() != null) {
            List<ProductVariant> variants = variantMapper.toProductVariants(productRequest.getVariants());
            variants.forEach(variant -> variant.setProduct(product));
            product.setVariants(variants);
        }
        return product;
    }

    private void handleImageUrls(Product product, ProductRequest productRequest) {
        try {
            if (productRequest.getMainImageUrl() != null) {
                String mainImage = fileStorageService.storeMainImage(productRequest.getMainImageUrl());
                if (mainImage != null) {
                    product.setMainImage(ProductMainImage.builder()
                            .imageUrl(mainImage)
                            .product(product)
                            .build());
                }
            }
            if (productRequest.getSecondaryImageUrls() != null && !productRequest.getSecondaryImageUrls().isEmpty()) {
                List<String> secondaryImages = fileStorageService.storeSecondaryImages(productRequest.getSecondaryImageUrls());
                if (!secondaryImages.isEmpty()) {
                    product.setSecondaryImages(
                            secondaryImages.stream()
                                    .map(url -> ProductSecondaryImage.builder()
                                            .imageUrl(url)
                                            .product(product)
                                            .build())
                                    .collect(Collectors.toList())
                    );
                }
            }
            if (productRequest.getDescriptionImageUrls() != null && !productRequest.getDescriptionImageUrls().isEmpty()) {
                List<String> descriptionImages = fileStorageService.storeDescriptionImages(productRequest.getDescriptionImageUrls());
                if (!descriptionImages.isEmpty()) {
                    product.setDescriptionImages(
                            descriptionImages.stream()
                                    .map(url -> ProductDescriptionImage.builder()
                                            .imageUrl(url)
                                            .product(product)
                                            .build())
                                    .collect(Collectors.toList())
                    );
                }
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
    // Hàm tạo slug duy nhất
    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String slug = baseSlug;
        int counter = 1;

        while (productRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

}
