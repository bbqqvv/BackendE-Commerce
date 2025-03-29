package org.bbqqvv.backendecommerce.service;

import org.bbqqvv.backendecommerce.dto.request.ChangePasswordRequest;
import org.bbqqvv.backendecommerce.dto.request.UserCreationRequest;
import org.bbqqvv.backendecommerce.dto.request.UserUpdateRequest;
import org.bbqqvv.backendecommerce.dto.response.UserResponse;
import org.bbqqvv.backendecommerce.dto.response.UserUpdateResponse;
import org.bbqqvv.backendecommerce.entity.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserCreationRequest request);

    void deleteUser(Long id);

    User getUserByUsernameEntity(String username);

    boolean existsByUsername(String username);
//
    UserUpdateResponse updateUserInfo(UserUpdateRequest request);

    void changePassword(ChangePasswordRequest request);

    UserResponse getCurrentUser();
}
