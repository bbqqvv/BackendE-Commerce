package org.bbqqvv.backendecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.request.DiscountPreviewRequest;
import org.bbqqvv.backendecommerce.dto.request.DiscountRequest;
import org.bbqqvv.backendecommerce.dto.response.DiscountPreviewResponse;
import org.bbqqvv.backendecommerce.dto.response.DiscountResponse;
import org.bbqqvv.backendecommerce.service.DiscountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ApiResponse<DiscountResponse> createDiscount(@RequestBody @Valid DiscountRequest request) {
        DiscountResponse discountResponse = discountService.createDiscount(request);
        return ApiResponse.<DiscountResponse>builder()
                .success(true)
                .data(discountResponse)
                .message("Discount created successfully.")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<DiscountResponse> getDiscountById(@PathVariable Long id) {
        DiscountResponse discountResponse = discountService.getDiscountById(id);
        return ApiResponse.<DiscountResponse>builder()
                .success(true)
                .data(discountResponse)
                .message("Discount retrieved successfully.")
                .build();
    }

    @GetMapping
    public ApiResponse<List<DiscountResponse>> getAllDiscounts() {
        List<DiscountResponse> discountResponses = discountService.getAllDiscounts();
        return ApiResponse.<List<DiscountResponse>>builder()
                .success(true)
                .data(discountResponses)
                .message("List of discounts retrieved successfully.")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<DiscountResponse> updateDiscount(@PathVariable Long id, @RequestBody @Valid DiscountRequest request) {
        DiscountResponse discountResponse = discountService.updateDiscount(id, request);
        return ApiResponse.<DiscountResponse>builder()
                .success(true)
                .data(discountResponse)
                .message("Discount updated successfully.")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ApiResponse.<String>builder()
                .success(true)
                .data("Discount deleted successfully.")
                .message("The discount has been removed successfully.")
                .build();
    }

    @DeleteMapping("/{id}/clear")
    public ApiResponse<String> clearUsersAndProducts(@PathVariable Long id) {
        discountService.clearUsersAndProducts(id);
        return ApiResponse.<String>builder()
                .success(true)
                .data("All users and products removed from discount.")
                .message("Users and products removed successfully.")
                .build();
    }

    @DeleteMapping("/{id}/remove-products")
    public ApiResponse<String> removeProductsFromDiscount(@PathVariable Long id, @RequestBody List<Long> productIds) {
        discountService.removeProductsFromDiscount(id, productIds);
        return ApiResponse.<String>builder()
                .success(true)
                .data("Selected products removed from discount.")
                .message("Products removed successfully.")
                .build();
    }

    @DeleteMapping("/{id}/remove-users")
    public ApiResponse<String> removeUsersFromDiscount(@PathVariable Long id, @RequestBody List<Long> userIds) {
        discountService.removeUsersFromDiscount(id, userIds);
        return ApiResponse.<String>builder()
                .success(true)
                .data("Selected users removed from discount.")
                .message("Users removed successfully.")
                .build();
    }

    // 📌 Lấy danh sách mã giảm giá của user hiện tại
    @GetMapping("/user-discounts")
    public ApiResponse<List<String>> getUserDiscountCodes() {
        return ApiResponse.<List<String>>builder()
                .success(true)
                .message("User's discount codes retrieved successfully")
                .data(discountService.getUserDiscountCodes())
                .build();
    }


    // 📌 Xem trước số tiền giảm giá trước khi đặt hàng
    @PostMapping("/preview-discount")
    public ApiResponse<DiscountPreviewResponse> previewDiscount(@RequestBody @Valid DiscountPreviewRequest discountPreviewRequest) {
        return ApiResponse.<DiscountPreviewResponse>builder()
                .success(true)
                .message("Discount preview calculated successfully")
                .data(discountService.previewDiscount(discountPreviewRequest))
                .build();
    }

}
