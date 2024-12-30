package org.bbqqvv.backendecommerce.controller;

import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.request.AddressRequest;
import org.bbqqvv.backendecommerce.dto.response.AddressResponse;
import org.bbqqvv.backendecommerce.service.AddressService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // Tạo mới một địa chỉ
    @PostMapping
    public ApiResponse<AddressResponse> createAddress(@RequestBody @Valid AddressRequest addressRequest) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.createAddress(addressRequest))
                .build();
    }

    // Lấy danh sách tất cả các địa chỉ của người dùng theo userId
    @GetMapping("/user/{userId}")
    public ApiResponse<List<AddressResponse>> getAddressesByUserId(@PathVariable Long userId) {
        return ApiResponse.<List<AddressResponse>>builder()
                .data(addressService.getAddressesByUserId(userId))
                .build();
    }

    // Lấy địa chỉ theo ID
    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getAddressById(@PathVariable Long id) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.getAddressById(id))
                .build();
    }

    // Cập nhật thông tin địa chỉ theo ID
    @PutMapping("update/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable Long id, @RequestBody @Valid AddressRequest addressRequest) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.updateAddress(id, addressRequest))
                .build();
    }

    // Xóa một địa chỉ theo ID
    @DeleteMapping("delete/{id}")
    public ApiResponse<String> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.<String>builder()
                .data("Address has been deleted")
                .build();
    }

    // Đặt địa chỉ mặc định
    @PutMapping("/{id}/set-default")
    public ApiResponse<AddressResponse> setDefaultAddress(@PathVariable Long id) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.setDefaultAddress(id))
                .build();
    }

}
