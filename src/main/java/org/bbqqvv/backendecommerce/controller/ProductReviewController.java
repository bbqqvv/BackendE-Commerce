package org.bbqqvv.backendecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.request.ProductReviewRequest;
import org.bbqqvv.backendecommerce.dto.response.ProductReviewResponse;
import org.bbqqvv.backendecommerce.service.ProductReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products-review")
@RequiredArgsConstructor
public class ProductReviewController {
    private final ProductReviewService productReviewService;

    /**
     * Thêm đánh giá mới hoặc cập nhật đánh giá sản phẩm
     */
    @PostMapping("/add-or-update")
    public ApiResponse<ProductReviewResponse> addOrUpdateReview(@ModelAttribute @Valid ProductReviewRequest reviewRequest) {
        return ApiResponse.<ProductReviewResponse>builder()
                .success(true)
                .data(productReviewService.addOrUpdateReview(reviewRequest))
                .message("Review added/updated successfully")
                .build();
    }

    /**
     * Lấy danh sách đánh giá theo sản phẩm
     */
    @GetMapping("/product/{productId}")
    public ApiResponse<List<ProductReviewResponse>> getReviewsByProduct(@PathVariable Long productId) {
        return ApiResponse.<List<ProductReviewResponse>>builder()
                .success(true)
                .data(productReviewService.getReviewsByProduct(productId))
                .message("Reviews retrieved successfully")
                .build();
    }

    /**
     * Lấy danh sách đánh giá của người dùng hiện tại
     */
    @GetMapping("/user")
    public ApiResponse<List<ProductReviewResponse>> getReviewsByUser() {
        return ApiResponse.<List<ProductReviewResponse>>builder()
                .success(true)
                .data(productReviewService.getReviewsByUser())
                .message("User reviews retrieved successfully")
                .build();
    }

    /**
     * Xóa đánh giá sản phẩm
     */
    @DeleteMapping("/remove/{reviewId}")
    public ApiResponse<String> deleteReview(@PathVariable Long reviewId) {
        productReviewService.deleteReview(reviewId);
        return ApiResponse.<String>builder()
                .success(true)
                .data("Review deleted successfully")
                .message("Review removed")
                .build();
    }
}
