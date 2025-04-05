package org.bbqqvv.backendecommerce.service;

import jakarta.validation.Valid;
import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductReviewRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ProductReviewService {
    ProductReviewResponse addOrUpdateReview(@Valid ProductReviewRequest reviewRequest);
    PageResponse<ProductReviewResponse> getReviewsByProduct(Long productId, Pageable pageable);
    PageResponse<ProductReviewResponse> getReviewsByUser(Pageable pageable);
    void deleteReview(Long reviewId);
}
