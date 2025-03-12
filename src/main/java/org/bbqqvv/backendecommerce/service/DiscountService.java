package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.request.DiscountPreviewRequest;
import org.bbqqvv.backendecommerce.dto.request.DiscountRequest;
import org.bbqqvv.backendecommerce.dto.response.DiscountPreviewResponse;
import org.bbqqvv.backendecommerce.dto.response.DiscountResponse;

import java.util.List;

public interface DiscountService {
    DiscountResponse createDiscount(DiscountRequest request);
    DiscountResponse getDiscountById(Long id);
    List<DiscountResponse> getAllDiscounts();
    DiscountResponse updateDiscount(Long id, DiscountRequest request);
    void deleteDiscount(Long id);
    void clearUsersAndProducts(Long id);

    void removeProductsFromDiscount(Long id, List<Long> productIds);

    void removeUsersFromDiscount(Long id, List<Long> userIds);

    List<String> getUserDiscountCodes();

    DiscountPreviewResponse previewDiscount(DiscountPreviewRequest discountPreviewRequest);
}
