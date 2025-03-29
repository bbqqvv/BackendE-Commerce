package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.request.SearchHistoryRequest;
import org.bbqqvv.backendecommerce.dto.response.SearchHistoryResponse;

import java.util.List;

public interface SearchHistoryService {
    SearchHistoryResponse saveSearchQuery(SearchHistoryRequest request);
    List<SearchHistoryResponse> getUserSearchHistory();
    List<SearchHistoryResponse> getSearchSuggestions(String query);
}
