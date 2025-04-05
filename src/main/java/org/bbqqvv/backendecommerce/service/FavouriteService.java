package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.PageResponse;
import org.bbqqvv.backendecommerce.dto.response.FavouriteResponse;
import org.springframework.data.domain.Pageable;

public interface FavouriteService {
    FavouriteResponse addFavourite(Long productId);
    FavouriteResponse removeFavourite(Long favouriteId);
    PageResponse<FavouriteResponse> getUserFavourites(Pageable pageable);
}
