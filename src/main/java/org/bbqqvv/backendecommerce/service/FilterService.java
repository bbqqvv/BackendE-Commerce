package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface FilterService {
    Map<String, Object> getFilterOptions();
    PageResponse<ProductResponse> filterProducts(Map<String, String> allParams, Pageable pageable);
}
