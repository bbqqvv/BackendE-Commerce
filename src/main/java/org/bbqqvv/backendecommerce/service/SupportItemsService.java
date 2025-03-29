package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.request.SupportItemRequest;
import org.bbqqvv.backendecommerce.dto.response.SupportItemResponse;

import java.util.List;

public interface SupportItemsService {
    List<SupportItemResponse> getAllSupportItems();
    SupportItemResponse getSupportItemById(Long id);
    SupportItemResponse createSupportItem(SupportItemRequest request);
    SupportItemResponse updateSupportItem(Long id, SupportItemRequest request);
    void deleteSupportItem(Long id);
}
