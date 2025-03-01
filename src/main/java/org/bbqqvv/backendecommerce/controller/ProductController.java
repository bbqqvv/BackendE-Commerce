package org.bbqqvv.backendecommerce.controller;

import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductResponse;
import org.bbqqvv.backendecommerce.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Tạo mới một sản phẩm
    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@ModelAttribute ProductRequest productRequest) {
        ProductResponse product = productService.createProduct(productRequest);
        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product created successfully")
                .data(product)
                .build();
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product retrieved successfully")
                .data(product)
                .build();
    }

    // Lấy sản phẩm theo Slug
    @GetMapping("slug/{slug}")
    public ApiResponse<ProductResponse> getProductBySlug(@PathVariable String slug) {
        ProductResponse product = productService.getProductBySlug(slug);
        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product retrieved successfully")
                .data(product)
                .build();
    }

    // Lấy danh sách tất cả sản phẩm với phân trang
    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(@PageableDefault(size = 10) Pageable pageable) {
        PageResponse<ProductResponse> productPage = productService.getAllProducts(pageable);
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .success(true)
                .message("Product list retrieved successfully")
                .data(productPage)
                .build();
    }

    // Lấy sản phẩm theo danh mục với phân trang
    @GetMapping("/find-by-category-slug/{slug}")
    public ApiResponse<PageResponse<ProductResponse>> findProductByCategorySlug(
            @PathVariable String slug,
            @PageableDefault(size = 10) Pageable pageable) {
        PageResponse<ProductResponse> productPage = productService.findProductByCategorySlug(slug, pageable);
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .success(true)
                .message("Products retrieved by category successfully")
                .data(productPage)
                .build();
    }

    // Cập nhật thông tin sản phẩm
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @ModelAttribute ProductRequest productRequest) {
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
        return ApiResponse.<ProductResponse>builder()
                .success(true)
                .message("Product updated successfully")
                .data(updatedProduct)
                .build();
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return ApiResponse.<String>builder()
                .success(true)
                .message(deleted ? "Product deleted successfully" : "Product not found")
                .data(deleted ? "Deleted" : "Not found")
                .build();
    }
}
