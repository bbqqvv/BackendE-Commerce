package org.bbqqvv.backendecommerce.util;

import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
@Component
public class PagingUtil {
    public static <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        List<R> items = page.stream().map(mapper).toList();
        return PageResponse.<R>builder()
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .items(items)
                .build();
    }
}
