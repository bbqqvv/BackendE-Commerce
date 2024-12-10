package org.bbqqvv.backendecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private String name; // Mô tả của danh mục
    private String slug;     // Tên của danh mục
    private MultipartFile image;    // URL của hình ảnh danh mục
}
