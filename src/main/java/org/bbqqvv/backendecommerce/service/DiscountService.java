package org.bbqqvv.backendecommerce.service;

import jakarta.validation.Valid;
import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.request.DiscountPreviewRequest;
import org.bbqqvv.backendecommerce.dto.request.DiscountRequest;
import org.bbqqvv.backendecommerce.dto.response.DiscountPreviewResponse;
import org.bbqqvv.backendecommerce.dto.response.DiscountResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiscountService {
    DiscountResponse createDiscount(DiscountRequest request);
    DiscountResponse getDiscountById(Long id);
    PageResponse<DiscountResponse> getAllDiscounts(Pageable pageable);
    DiscountResponse updateDiscount(Long id, DiscountRequest request);
    void deleteDiscount(Long id);
    void clearUsersAndProducts(Long id);

    void removeProductsFromDiscount(Long id, List<Long> productIds);

    void removeUsersFromDiscount(Long id, List<Long> userIds);

    PageResponse<DiscountResponse> getCurrentUserDiscount(Pageable pageable);

    DiscountPreviewResponse previewDiscount(DiscountPreviewRequest discountPreviewRequest);
    void saveDiscount(@Valid String discountCode);
}
