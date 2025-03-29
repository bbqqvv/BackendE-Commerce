package org.bbqqvv.backendecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bbqqvv.backendecommerce.dto.ApiResponse;
import org.bbqqvv.backendecommerce.dto.request.ChangePasswordRequest;
import org.bbqqvv.backendecommerce.dto.request.UserCreationRequest;
import org.bbqqvv.backendecommerce.dto.request.UserUpdateRequest;
import org.bbqqvv.backendecommerce.dto.response.UserResponse;
import org.bbqqvv.backendecommerce.dto.response.UserUpdateResponse;
import org.bbqqvv.backendecommerce.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Tạo người dùng mới
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User created successfully")
                .data(userResponse)
                .build();
    }
    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Password changed successfully")
                .data("Password updated")
                .build();
    }

    // Lấy người dùng theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(userResponse)
                .build();
    }

    // Lấy tất cả người dùng
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAllUsers();
        return ApiResponse.<List<UserResponse>>builder()

                .success(true)
                .message("User list retrieved successfully")
                .data(userResponses)
                .build();
    }

    // Cập nhật thông tin người dùng
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserCreationRequest request) {
        UserResponse userResponse = userService.updateUser(id, request);
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User updated successfully")
                .data(userResponse)
                .build();
    }
    @PutMapping("/me/update-info")
    public ApiResponse<UserUpdateResponse> updateUserInfo(@RequestBody @Valid UserUpdateRequest request) {
        UserUpdateResponse updatedUser = userService.updateUserInfo(request);
        return ApiResponse.<UserUpdateResponse>builder()
                .success(true)
                .message("User info updated successfully")
                .data(updatedUser)
                .build();
    }

    // Xóa người dùng
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<String>builder()
                .success(true)
                .message("User has been deleted successfully")
                .data("User deleted")
                .build();
    }

// Lấy thông tin user hiện tại từ token
    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser() {
        UserResponse userResponse = userService.getCurrentUser();
        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(userResponse)
                .build();
    }

}
