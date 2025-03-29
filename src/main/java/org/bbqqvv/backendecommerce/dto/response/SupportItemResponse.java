package org.bbqqvv.backendecommerce.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportItemResponse {
    private Long id;
    private String img;
    private String title;
    private String hours;
    private String contact;
    private String link;
    private String bgColor;
}
