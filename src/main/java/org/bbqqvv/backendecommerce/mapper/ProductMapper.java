package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.response.ProductResponse;
import org.bbqqvv.backendecommerce.dto.response.ProductVariantResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductRequest;
import org.bbqqvv.backendecommerce.dto.request.ProductVariantRequest;
import org.bbqqvv.backendecommerce.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "mainImage.imageUrl", target = "mainImageUrl")
    @Mapping(source = "secondaryImages", target = "secondaryImageUrls", qualifiedByName = "mapSecondaryImageUrls")
    @Mapping(source = "descriptionImages", target = "descriptionImageUrls", qualifiedByName = "mapDescriptionImageUrls")
    @Mapping(source = "variants", target = "variants", qualifiedByName = "mapProductVariants")
    ProductResponse toProductResponse(Product product);
    @Named("mapSecondaryImageUrls")
    default List<String> mapSecondaryImageUrls(List<ProductSecondaryImage> images) {
        return mapImageUrls(images);
    }
    @Named("mapDescriptionImageUrls")
    default List<String> mapDescriptionImageUrls(List<ProductDescriptionImage> images) {
        return mapImageUrls(images);
    }
    private List<String> mapImageUrls(List<? extends ProductImage> images) {
        return images == null ? null : images.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
    }
    @Named("mapProductVariants")
    default List<ProductVariantResponse> mapProductVariants(List<ProductVariant> variants) {
        return variants == null ? null : variants.stream()
                .map(variant -> new ProductVariantResponse(
                        variant.getColor(),
                        variant.getSize(),
                        variant.getPrice()))
                .collect(Collectors.toList());
    }
    @Mapping(source = "mainImageUrl", target = "mainImage")  // Ánh xạ URL chính vào trường mainImage
    Product toProduct(ProductRequest productRequest);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mainImage", ignore = true)
    @Mapping(target = "variants", ignore = true)
    void updateEntity(@MappingTarget Product product, ProductRequest productRequest);
    @Mapping(target = "product", ignore = true)
    List<ProductVariant> toProductVariants(List<ProductVariantRequest> productVariantRequests);
    @Named("mapProductVariantRequestToVariant")
    default ProductVariant toProductVariant(ProductVariantRequest productVariantRequest) {
        if (productVariantRequest == null) {
            return null;
        }
        ProductVariant productVariant = new ProductVariant();
        productVariant.setColor(productVariantRequest.getColor());
        productVariant.setSize(productVariantRequest.getSize());
        productVariant.setPrice(productVariantRequest.getPrice());
        return productVariant;
    }
}
