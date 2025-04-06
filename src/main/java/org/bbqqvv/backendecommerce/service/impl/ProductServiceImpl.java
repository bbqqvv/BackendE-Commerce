package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductRequest;
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
import org.bbqqvv.backendecommerce.repository.TagRepository;
import org.bbqqvv.backendecommerce.service.ProductService;
import org.bbqqvv.backendecommerce.service.img.FileStorageService;
import org.bbqqvv.backendecommerce.util.SlugUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    SizeProductRepository sizeProductRepository;
    ProductMapper productMapper;
    FileStorageService fileStorageService;
    TagRepository tagRepository;
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper,
                              VariantMapper variantMapper,
                              FileStorageService fileStorageService,
                              SizeProductRepository sizeProductRepository,
                              SizeProductMapper sizeProductMapper, TagRepository tagRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.fileStorageService = fileStorageService;
        this.sizeProductRepository = sizeProductRepository;
        this.tagRepository = tagRepository;
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

    @Cacheable(value = "products", key = "#id")
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
    @CacheEvict(value = "products", key = "#id")
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

    @Override
    public PageResponse<ProductResponse> searchProductsByName(String name, Pageable pageable) {
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        List<ProductResponse> productResponses = products.stream()
                .map(this::toFullProductResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductResponse>builder()
                .currentPage(products.getNumber())
                .totalPages(products.getTotalPages())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .items(productResponses)
                .build();
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Product createOrUpdateProductEntity(ProductRequest productRequest, Category category) {
        Product product = productMapper.toProduct(productRequest);
        product.setCategory(category);

        // ✅ Xử lý hình ảnh
        handleImageUrls(product, productRequest);

        // ✅ Xử lý tags từ productRequest
        if (productRequest.getTags() != null) {
            Set<Tag> tags = productRequest.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName)))) // Tạo nếu chưa có
                    .collect(Collectors.toSet());

            product.setTags(tags);
        }

        // ✅ Xử lý variants và size
        if (productRequest.getVariants() != null) {
            List<ProductVariant> variants = productRequest.getVariants().stream().map(variantRequest -> {
                ProductVariant variant = new ProductVariant();
                variant.setColor(variantRequest.getColor());
                variant.setProduct(product);

                // Upload hình ảnh cho variant
                Optional.ofNullable(variantRequest.getImageUrl())
                        .map(fileStorageService::storeImage)
                        .ifPresent(variant::setImageUrl);

                // Xử lý kích thước cho từng variant
                List<SizeProductVariant> sizeVariants = Optional.ofNullable(variantRequest.getSizes())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(sizeRequest -> {
                            SizeProduct sizeProduct = sizeProductRepository.findBySizeName(sizeRequest.getSizeName())
                                    .orElseGet(() -> {
                                        SizeProduct newSize = new SizeProduct();
                                        newSize.setSizeName(sizeRequest.getSizeName());
                                        newSize.setPrice(sizeRequest.getPrice());
                                        newSize.setPriceAfterDiscount(
                                                calculatePriceAfterDiscount(sizeRequest.getPrice(), productRequest.getSalePercentage()));
                                        return sizeProductRepository.save(newSize);
                                    });

                            SizeProductVariant sizeVariant = new SizeProductVariant();
                            sizeVariant.setSizeProduct(sizeProduct);
                            sizeVariant.setProductVariant(variant);
                            sizeVariant.setStock(sizeRequest.getStock());
                            return sizeVariant;
                        })
                        .collect(Collectors.toList());

                variant.setProductVariantSizes(sizeVariants);
                return variant;
            }).collect(Collectors.toList());

            product.setVariants(variants);
        }

        return product;
    }

    private void handleImageUrls(Product product, ProductRequest productRequest) {
        try {
            Optional.ofNullable(productRequest.getMainImageUrl())
                    .map(fileStorageService::storeImage)
                    .ifPresent(imageUrl -> product.setMainImage(ProductMainImage.builder()
                            .imageUrl(imageUrl)
                            .product(product)
                            .build()));

            Optional.ofNullable(productRequest.getSecondaryImageUrls())
                    .filter(urls -> !urls.isEmpty())
                    .map(fileStorageService::storeImages)
                    .ifPresent(images -> product.setSecondaryImages(images.stream()
                            .map(url -> ProductSecondaryImage.builder()
                                    .imageUrl(url)
                                    .product(product)
                                    .build())
                            .collect(Collectors.toList())));

            Optional.ofNullable(productRequest.getDescriptionImageUrls())
                    .filter(urls -> !urls.isEmpty())
                    .map(fileStorageService::storeImages)
                    .ifPresent(images -> product.setDescriptionImages(images.stream()
                            .map(url -> ProductDescriptionImage.builder()
                                    .imageUrl(url)
                                    .product(product)
                                    .build())
                            .collect(Collectors.toList())));

        } catch (Exception e) {
            throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.toSlug(name);
        String slug = baseSlug;
        int counter = 1;

        while (productRepository.existsBySlug(slug)) {
            slug = String.format("%s-%d", baseSlug, counter++);
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
