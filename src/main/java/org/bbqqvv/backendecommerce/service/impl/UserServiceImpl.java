package org.bbqqvv.backendecommerce.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.dto.request.UserCreationRequest;
import org.bbqqvv.backendecommerce.dto.response.UserResponse;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.mapper.UserMapper;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.bbqqvv.backendecommerce.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse createUser(UserCreationRequest request) {
        // Map RegisterUserRequest -> User entity
        User user = userMapper.toUser(request);

        // Lưu vào DB
        User savedUser = userRepository.save(user);

        // Map User entity -> UserResponse
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null; // Hoặc throw exception tùy theo yêu cầu.
        }
        return userMapper.toUserResponse(user);
    }

    @Override
    public User getUserByUsernameEntity(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null; // Hoặc throw exception tùy theo yêu cầu.
        }
        return userRepository.findByUsername(username); // Trả về User entity chứa mật khẩu
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserCreationRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null; // Hoặc throw exception tùy theo yêu cầu.
        }

        // Cập nhật thông tin từ DTO
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());

        // Lưu lại
        User updatedUser = userRepository.save(user);

        // Map User entity -> UserResponse
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
