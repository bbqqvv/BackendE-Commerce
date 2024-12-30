package org.bbqqvv.backendecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.request.FavouriteRequest;
import org.bbqqvv.backendecommerce.dto.response.FavouriteResponse;
import org.bbqqvv.backendecommerce.service.FavouriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;

    @PostMapping
    public ApiResponse<FavouriteResponse> addFavourite(@RequestBody @Valid FavouriteRequest favouriteRequest) {
        FavouriteResponse favouriteResponse = favouriteService.addFavourite(favouriteRequest.getProductId());
        return ApiResponse.<FavouriteResponse>builder()
                .data(favouriteResponse)
                .message("Product added to favourites successfully.")
                .build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<String> removeFavourite(@PathVariable Long productId) {
        favouriteService.removeFavourite(productId);
        return ApiResponse.<String>builder()
                .data("Favourite successfully removed.")
                .message("The product has been removed from favourites.")
                .build();
    }

    @GetMapping
    public ApiResponse<List<FavouriteResponse>> getUserFavourites() {
        List<FavouriteResponse> favouriteResponses = favouriteService.getUserFavourites();
        return ApiResponse.<List<FavouriteResponse>>builder()
                .data(favouriteResponses)
                .message("List of user favourites retrieved successfully.")
                .build();
    }
}
