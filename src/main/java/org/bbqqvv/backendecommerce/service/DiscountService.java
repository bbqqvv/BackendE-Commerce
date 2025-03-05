package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.request.DiscountRequest;
import org.bbqqvv.backendecommerce.dto.response.DiscountResponse;

import java.util.List;

public interface DiscountService {
    DiscountResponse createDiscount(DiscountRequest request);
    DiscountResponse getDiscountById(Long id);
    List<DiscountResponse> getAllDiscounts();
    DiscountResponse updateDiscount(Long id, DiscountRequest request);
    void deleteDiscount(Long id);
}
