package org.bbqqvv.backendecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.request.DiscountRequest;
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
}
