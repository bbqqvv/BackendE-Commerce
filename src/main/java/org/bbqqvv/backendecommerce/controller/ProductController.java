package org.bbqqvv.backendecommerce.controller;

import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductResponse;
import org.bbqqvv.backendecommerce.service.ProductService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Tạo mới một sản phẩm
    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@ModelAttribute ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .data(productService.createProduct(productRequest))
                .build();
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return ApiResponse.<ProductResponse>builder()
                .data(productService.getProductById(id))
                .build();
    }
    // Lấy sản phẩm theo Slug
    @GetMapping("/slug/{slug}")
    public ApiResponse<ProductResponse> getProductBySlug(@PathVariable String slug) {
        ProductResponse product = productService.getProductBySlug(slug);
        return ApiResponse.<ProductResponse>builder()
                .data(product)
                .build();
    }

    // Lấy danh sách tất cả sản phẩm
    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .data(productService.getAllProducts())
                .build();
    }

//    // Lấy sản phẩm theo danh mục
//    @GetMapping("/find-by-category/{categoryId}")
//    public ApiResponse<List<ProductResponse>> getProductByCategory(@PathVariable Long categoryId) {
//        return ApiResponse.<List<ProductResponse>>builder()
//                .data(productService.findProductByCategory(categoryId))
//                .build();
//    }
    @GetMapping("/find-by-category-slug/{slug}")
    public ApiResponse<List<ProductResponse>> findProductByCategorySlug(@PathVariable String slug) {
        List<ProductResponse> products = productService.findProductByCategorySlug(slug);
        return ApiResponse.<List<ProductResponse>>builder()
                .data(products)
                .build();
    }


    // Cập nhật thông tin sản phẩm
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @ModelAttribute ProductRequest productRequest) {
        return ApiResponse.<ProductResponse>builder()
                .data(productService.updateProduct(id, productRequest))
                .build();
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        return ApiResponse.<String>builder()
                .data(deleted ? "Product has been deleted" : "Product not found")
                .build();
    }
}
