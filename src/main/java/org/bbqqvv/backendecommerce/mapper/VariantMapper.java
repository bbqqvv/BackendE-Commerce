package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.response.ProductVariantResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductVariantRequest;
import org.bbqqvv.backendecommerce.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VariantMapper {
    ProductVariantResponse toProductVariantResponse(ProductVariant variant);
    List<ProductVariantResponse> toProductVariantResponses(List<ProductVariant> variants);
    @Mapping(target = "product", ignore = true) // Ignore the product field to avoid circular references
    ProductVariant toProductVariant(ProductVariantRequest request);
    List<ProductVariant> toProductVariants(List<ProductVariantRequest> requests);
}
