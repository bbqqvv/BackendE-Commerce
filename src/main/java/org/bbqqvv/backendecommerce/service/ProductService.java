package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(Long id);
    PageResponse<ProductResponse> getAllProducts(Pageable pageable);
    PageResponse<ProductResponse> findProductByCategorySlug(String slug, Pageable pageable);
    ProductResponse getProductBySlug(String slug);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    boolean deleteProduct(Long id);
    PageResponse<ProductResponse> searchProductsByName(String name, Pageable pageable);
    PageResponse<ProductResponse> filterProducts(Map<String, String> allParams, Pageable pageable);
}
