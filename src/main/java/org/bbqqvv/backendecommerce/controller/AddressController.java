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
                .success(true)
                .data(addressService.createAddress(addressRequest))
                .build();
    }

    // Lấy danh sách địa chỉ của chính người dùng (Lấy từ Token)
    @GetMapping("/me")
    public ApiResponse<List<AddressResponse>> getAddressesByUser() {
        return ApiResponse.<List<AddressResponse>>builder()
                .success(true)
                .data(addressService.getAddressesByUser())
                .build();
    }

    // Lấy địa chỉ theo ID (Chỉ lấy được địa chỉ của chính mình)
    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> getAddressById(@PathVariable Long id) {
        return ApiResponse.<AddressResponse>builder()
                .success(true)
                .data(addressService.getAddressById(id))
                .build();
    }

    // Cập nhật thông tin địa chỉ theo ID (Chỉ cập nhật địa chỉ của mình)
    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable Long id, @RequestBody @Valid AddressRequest addressRequest) {
        return ApiResponse.<AddressResponse>builder()
                .data(addressService.updateAddress(id, addressRequest))
                .build();
    }

    // Xóa một địa chỉ theo ID (Chỉ xóa địa chỉ của mình)
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.<String>builder()
                .success(true)
                .data("Address has been deleted")
                .build();
    }

    // Đặt địa chỉ mặc định (Chỉ đặt địa chỉ của mình)
    @PutMapping("/{id}/set-default")
    public ApiResponse<AddressResponse> setDefaultAddress(@PathVariable Long id) {
        return ApiResponse.<AddressResponse>builder()
                .success(true)
                .data(addressService.setDefaultAddress(id))
                .build();
    }
}
