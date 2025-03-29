package org.bbqqvv.backendecommerce.repository;

import org.bbqqvv.backendecommerce.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    // Lấy 5 từ khóa tìm kiếm gần nhất của user
    List<SearchHistory> findTop5ByOrderByCreatedAtDesc();

    // Gợi ý tìm kiếm theo từ khóa (giống LIKE '%query%')
    List<SearchHistory> findTop5BySearchQueryContainingIgnoreCaseOrderByCreatedAtDesc(String query);
}
