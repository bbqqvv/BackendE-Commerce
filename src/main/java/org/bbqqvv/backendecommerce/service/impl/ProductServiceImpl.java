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
import org.bbqqvv.backendecommerce.repository.CategoryRepository;
import org.bbqqvv.backendecommerce.repository.ProductRepository;
import org.bbqqvv.backendecommerce.service.ProductService;
import org.bbqqvv.backendecommerce.service.img.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ImgBBConfig imgBBConfig;
    private final FileStorageService fileStorageService;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper,
                              ImgBBConfig imgBBConfig,
                              FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.imgBBConfig = imgBBConfig;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Kiểm tra nếu product code đã tồn tại
        if (productRepository.existsByProductCode(productRequest.getProductCode())) {
            throw new AppException(ErrorCode.DUPLICATE_PRODUCT_CODE);
        }

        Category category = getCategoryById(productRequest.getCategoryId());
        Product product = createOrUpdateProductEntity(productRequest, category);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toProductResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)); // Dùng AppException với ErrorCode
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
        // Kiểm tra sản phẩm có tồn tại hay không
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND); // Dùng AppException với ErrorCode
        }

        Category category = getCategoryById(productRequest.getCategoryId());
        Product product = createOrUpdateProductEntity(productRequest, category);
        product.setId(id);  // Đảm bảo ID không thay đổi khi cập nhật
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND); // Dùng AppException với ErrorCode
        }
        productRepository.deleteById(id);
        return true;
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)); // Dùng AppException với ErrorCode
    }

    @Override
    public List<ProductResponse> findProductByCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);
        return productRepository.findProductByCategory(category).stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    private Product createOrUpdateProductEntity(ProductRequest productRequest, Category category) {
        Product product = productMapper.toProduct(productRequest);
        product.setCategory(category);
        handleImageUrls(product, productRequest);
        if (productRequest.getVariants() != null) {
            List<ProductVariant> variants = productMapper.toProductVariants(productRequest.getVariants());
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
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED); // Dùng AppException khi có lỗi trong xử lý ảnh
        }
    }
}
