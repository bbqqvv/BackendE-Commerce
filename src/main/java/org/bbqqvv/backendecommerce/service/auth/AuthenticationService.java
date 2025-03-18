package org.bbqqvv.backendecommerce.service.auth;

import org.bbqqvv.backendecommerce.config.jwt.JwtTokenUtil;
import org.bbqqvv.backendecommerce.dto.request.AuthenticationRequest;
import org.bbqqvv.backendecommerce.dto.request.UserCreationRequest;
import org.bbqqvv.backendecommerce.dto.response.UserResponse;
import org.bbqqvv.backendecommerce.entity.AuthProvider;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.mapper.UserMapper;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.bbqqvv.backendecommerce.service.UserService;
import org.bbqqvv.backendecommerce.util.ValidateUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    public AuthenticationService(UserService userService, UserMapper userMapper,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }
    public UserResponse register(UserCreationRequest registerUserDto) {
        ValidateUtils.validateUsername(registerUserDto.getUsername());

        // Kiểm tra xem email đã tồn tại chưa
        Optional<User> existingUser = userRepository.findByEmail(registerUserDto.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Nếu tài khoản đã đăng ký bằng Google, không cho phép đăng ký lại bằng email/password
            if (user.getProvider() == AuthProvider.GOOGLE) {
                throw new RuntimeException("Tài khoản đã được tạo bằng Google. Vui lòng đăng nhập bằng Google.");
            }

            throw new RuntimeException("Email này đã được đăng ký.");
        }

        // Mã hóa mật khẩu trước khi lưu vào database
        String encodedPassword = passwordEncoder.encode(registerUserDto.getPassword());

        // Tạo user mới với provider mặc định là LOCAL (email/password)
        User newUser = User.builder()
                .username(registerUserDto.getUsername())
                .password(encodedPassword)
                .email(registerUserDto.getEmail())
                .provider(AuthProvider.LOCAL)
                .build();

        // Lưu user vào database
        userRepository.save(newUser);

        return  userMapper.toUserResponse(newUser);
    }
    public String login(AuthenticationRequest loginUserDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getUsername(), loginUserDto.getPassword())
        );
        return jwtTokenUtil.generateToken(((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername());
    }
}
