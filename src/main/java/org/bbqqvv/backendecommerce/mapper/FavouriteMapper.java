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
    @Mapping(target = "imageUrl", source = "product.mainImage.imageUrl")
    FavouriteResponse toFavouriteResponse(Favourite favourite);
}
