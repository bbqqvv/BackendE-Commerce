package org.bbqqvv.backendecommerce.mapper;

import org.bbqqvv.backendecommerce.dto.request.FavouriteRequest;
import org.bbqqvv.backendecommerce.dto.response.FavouriteResponse;
import org.bbqqvv.backendecommerce.entity.Favourite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavouriteMapper {
    @Mapping(target = "product.id", source = "productId")
    Favourite toFavourite(FavouriteRequest favouriteRequest);
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "nameProduct", source = "product.name")
    @Mapping(target = "productUrl", source = "product.slug")
    @Mapping(target = "stockStatus", ignore = true)
    @Mapping(target = "imageUrl", source = "product.mainImage.imageUrl")
    @Mapping(target = "price", expression =
            "java(favourite.getProduct() != null && !favourite.getProduct().getVariants().isEmpty() " +
                    "? (!favourite.getProduct().getVariants().get(0).getProductVariantSizes().isEmpty() " +
                    "? favourite.getProduct().getVariants().get(0).getProductVariantSizes().get(0).getSizeProduct().getPrice() " +
                    ": java.math.BigDecimal.ZERO) : java.math.BigDecimal.ZERO)")
    FavouriteResponse toFavouriteResponse(Favourite favourite);
}
