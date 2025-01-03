package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.response.ProductResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductRequest;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse getProductById(Long id);
    ProductResponse getProductBySlug(String slug);
    List<ProductResponse> getAllProducts();
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    boolean deleteProduct(Long id);
    List<ProductResponse> findProductByCategorySlug(String slug);
}
