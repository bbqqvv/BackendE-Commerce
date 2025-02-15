package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.response.FavouriteResponse;

import java.util.List;

public interface FavouriteService {
    FavouriteResponse addFavourite(Long productId);  // Sử dụng ID người dùng và sản phẩm để thêm yêu thích
    FavouriteResponse removeFavourite(Long productId);  // Sử dụng ID người dùng và sản phẩm để xóa yêu thích
    List<FavouriteResponse> getUserFavourites();  // Lấy danh sách yêu thích của người dùng
}
