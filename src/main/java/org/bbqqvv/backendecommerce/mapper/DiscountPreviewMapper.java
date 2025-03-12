package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.response.DiscountPreviewResponse;
import org.bbqqvv.backendecommerce.entity.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiscountPreviewMapper {


    @Mapping(target = "discountCode", source = "code")
    @Mapping(target = "originalTotalAmount", ignore = true)
    @Mapping(target = "finalAmount", ignore = true)
    @Mapping(target = "valid", ignore = true)
    @Mapping(target = "message", ignore = true)
    DiscountPreviewResponse toResponse(Discount discount);
}
