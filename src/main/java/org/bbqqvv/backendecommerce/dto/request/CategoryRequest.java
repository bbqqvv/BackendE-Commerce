package org.bbqqvv.backendecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {
    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 200, message = "Name must be between 1 and 200 characters")
    private String name;
    @NotNull(message = "Slug cannot be null")
    @Size(min = 1, max = 50, message = "Slug must be between 1 and 50 characters")
    private String slug;
    private MultipartFile image;
}
