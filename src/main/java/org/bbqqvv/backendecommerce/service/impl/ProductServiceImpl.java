package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductRequest;
import org.bbqqvv.backendecommerce.dto.request.ProductVariantRequest;
import org.bbqqvv.backendecommerce.dto.request.SizeProductRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductResponse;
import org.bbqqvv.backendecommerce.entity.*;
import org.bbqqvv.backendecommerce.exception.AppException;
import org.bbqqvv.backendecommerce.exception.ErrorCode;
import org.bbqqvv.backendecommerce.mapper.ProductMapper;
import org.bbqqvv.backendecommerce.mapper.SizeProductMapper;
import org.bbqqvv.backendecommerce.mapper.VariantMapper;
import org.bbqqvv.backendecommerce.repository.CategoryRepository;
import org.bbqqvv.backendecommerce.repository.ProductRepository;
import org.bbqqvv.backendecommerce.repository.SizeProductRepository;
import org.bbqqvv.backendecommerce.service.ProductService;
import org.bbqqvv.backendecommerce.service.img.FileStorageService;
import org.bbqqvv.backendecommerce.util.SlugUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    SizeProductRepository sizeProductRepository;
    ProductMapper productMapper;
    VariantMapper variantMapper;
    SizeProductMapper sizeProductMapper;
    FileStorageService fileStorageService;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper,
                              VariantMapper variantMapper,
                              FileStorageService fileStorageService,
                              SizeProductRepository sizeProductRepository,
                              SizeProductMapper sizeProductMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.variantMapper = variantMapper;
        this.fileStorageService = fileStorageService;
        this.sizeProductRepository = sizeProductRepository;
        this.sizeProductMapper = sizeProductMapper;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        if (productRepository.existsByProductCode(productRequest.getProductCode())) {
            throw new AppException(ErrorCode.DUPLICATE_PRODUCT_CODE);
        }

        Category category = getCategoryById(productRequest.getCategoryId());
        Product product = createOrUpdateProductEntity(productRequest, category);

        // Generate unique slug
        String slug = generateUniqueSlug(productRequest.getName());
        product.setSlug(slug);

        Product savedProduct = productRepository.save(product);
        return toFullProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toFullProductResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return toPageResponse(productPage);
    }

    @Override
    public PageResponse<ProductResponse> findProductByCategorySlug(String slug, Pageable pageable) {
        Category category = categoryRepository.findBySlug(slug);
        if (category == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        Page<Product> productPage = productRepository.findProductByCategory(category, pageable);
        return toPageResponse(productPage);
    }

    @Override
    public ProductResponse getProductBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .map(this::toFullProductResponse)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Category category = getCategoryById(productRequest.getCategoryId());
        Product product = createOrUpdateProductEntity(productRequest, category);
        product.setId(id);

        // Update slug if the name has changed
        if (!existingProduct.getName().equals(productRequest.getName())) {
            String uniqueSlug = generateUniqueSlug(productRequest.getName());
            product.setSlug(uniqueSlug);
        } else {
            product.setSlug(existingProduct.getSlug());
        }

        Product savedProduct = productRepository.save(product);
        return toFullProductResponse(savedProduct);
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(id);
        return true;
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Product createOrUpdateProductEntity(ProductRequest productRequest, Category category) {
        Product product = productMapper.toProduct(productRequest);
        product.setCategory(category);

        // Xử lý hình ảnh sản phẩm
        handleImageUrls(product, productRequest);

        if (productRequest.getVariants() != null) {
            List<ProductVariant> variants = new ArrayList<>();
            for (ProductVariantRequest variantRequest : productRequest.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setColor(variantRequest.getColor());
                variant.setProduct(product);

                // Upload hình ảnh cho variant
                if (variantRequest.getImageUrl() != null) {
                    String imageUrl = fileStorageService.storeImage(variantRequest.getImageUrl());
                    variant.setImageUrl(imageUrl);
                }

                // Xử lý các kích thước cho từng variant
                if (variantRequest.getSizes() != null) {
                    List<SizeProductVariant> sizeVariants = new ArrayList<>();
                    for (SizeProductRequest sizeRequest : variantRequest.getSizes()) {
                        SizeProduct sizeProduct = sizeProductRepository.findBySizeName(sizeRequest.getSizeName())
                                .orElseGet(() -> {
                                    SizeProduct newSize = new SizeProduct();
                                    newSize.setSizeName(sizeRequest.getSizeName());
                                    newSize.setPrice(sizeRequest.getPrice());
                                    BigDecimal priceAfterDiscount = calculatePriceAfterDiscount(
                                            sizeRequest.getPrice(), productRequest.getSalePercentage());
                                    newSize.setPriceAfterDiscount(priceAfterDiscount);
                                    return sizeProductRepository.save(newSize);
                                });

                        SizeProductVariant sizeVariant = new SizeProductVariant();
                        sizeVariant.setSizeProduct(sizeProduct);
                        sizeVariant.setProductVariant(variant);
                        sizeVariant.setStock(sizeRequest.getStock());
                        sizeVariants.add(sizeVariant);
                    }
                    variant.setProductVariantSizes(sizeVariants);
                }
                variants.add(variant);
            }
            product.setVariants(variants);
        }

        return product;
    }

    private void handleImageUrls(Product product, ProductRequest productRequest) {
        try {
            if (productRequest.getMainImageUrl() != null) {
                String mainImage = fileStorageService.storeImage(productRequest.getMainImageUrl());
                if (mainImage != null) {
                    product.setMainImage(ProductMainImage.builder()
                            .imageUrl(mainImage)
                            .product(product)
                            .build());
                }
            }
            if (productRequest.getSecondaryImageUrls() != null && !productRequest.getSecondaryImageUrls().isEmpty()) {
                List<String> secondaryImages = fileStorageService.storeImages(productRequest.getSecondaryImageUrls());
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
                List<String> descriptionImages = fileStorageService.storeImages(productRequest.getDescriptionImageUrls());
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

    private BigDecimal calculatePriceAfterDiscount(BigDecimal price, int salePercentage) {
        if (price != null && salePercentage > 0) {
            BigDecimal discount = price.multiply(BigDecimal.valueOf(salePercentage)).divide(BigDecimal.valueOf(100));
            return price.subtract(discount);
        }
        return price;
    }

    private PageResponse<ProductResponse> toPageResponse(Page<Product> productPage) {
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::toFullProductResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .currentPage(productPage.getNumber())
                .totalPages(productPage.getTotalPages())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .items(productResponses)
                .build();
    }

    private ProductResponse toFullProductResponse(Product product) {
        return productMapper.toProductResponse(product);
    }
}
