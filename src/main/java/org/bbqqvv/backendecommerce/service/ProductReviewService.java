package org.bbqqvv.backendecommerce.service;

import jakarta.validation.Valid;
import org.bbqqvv.backendecommerce.dto.request.ProductReviewRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductReviewResponse;

import java.util.List;

public interface ProductReviewService {
    ProductReviewResponse addOrUpdateReview(@Valid ProductReviewRequest reviewRequest);
    List<ProductReviewResponse> getReviewsByProduct(Long productId);
    List<ProductReviewResponse> getReviewsByUser();
    void deleteReview(Long reviewId);
}
