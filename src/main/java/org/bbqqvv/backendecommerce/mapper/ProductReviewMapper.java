package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.request.ProductReviewRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductReviewResponse;
import org.bbqqvv.backendecommerce.entity.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {

    // Chuyển DTO -> Entity (Dùng khi tạo mới ProductReview)
    @Mapping(target = "id", ignore = true)  // ID được tự động tạo
    @Mapping(target = "product", ignore = true) // Product được lấy từ service
    @Mapping(target = "user", ignore = true)  // User được lấy từ security context
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    ProductReview toEntity(ProductReviewRequest request);

    // Chuyển Entity -> DTO (Dùng khi trả response)
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.username")
    ProductReviewResponse toResponse(ProductReview review);
}
