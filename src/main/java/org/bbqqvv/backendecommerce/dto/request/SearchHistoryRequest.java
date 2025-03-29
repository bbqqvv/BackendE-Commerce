package org.bbqqvv.backendecommerce.dto.request;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchHistoryRequest {
    private String searchQuery;
}
