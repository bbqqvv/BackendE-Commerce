package org.bbqqvv.backendecommerce.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.jwt.JwtTokenUtil;
import org.bbqqvv.backendecommerce.entity.AuthProvider;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String googleUserInfoUrl;

    public OAuth2Service(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Transactional
    public String loginWithGoogle(String googleToken) {
        log.info("Validating Google token...");

        // Gửi request xác minh token với Google
        String validationUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + googleToken;
        ResponseEntity<Map> validationResponse;

        try {
            validationResponse = restTemplate.getForEntity(validationUrl, Map.class);
        } catch (Exception e) {
            log.error("Failed to validate Google token: {}", e.getMessage());
            throw new RuntimeException("Invalid Google token", e);
        }

        // Kiểm tra phản hồi
        if (!validationResponse.getStatusCode().is2xxSuccessful() || validationResponse.getBody() == null) {
            log.error("Google token validation failed");
            throw new RuntimeException("Invalid Google token");
        }

        // Lấy thông tin user từ token
        Map<String, Object> googleUser = validationResponse.getBody();
        String email = (String) googleUser.get("email");
        String name = (String) googleUser.get("name");
        String googleId = (String) googleUser.get("sub"); // Google user ID

        if (email == null || googleId == null) {
            log.error("Google token missing required fields");
            throw new RuntimeException("Invalid Google user data");
        }

        // Kiểm tra user trong DB hoặc tạo mới
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.info("Creating new user from Google login: {}", email);
            user = User.builder()
                    .email(email)
                    .username(name)
                    .provider(AuthProvider.GOOGLE)
                    .providerId(googleId)
                    .build();
            userRepository.save(user);
        } else {
            if (user.getProviderId() == null) {
                user.setProviderId(googleId);
                userRepository.save(user);
            }
            if (user.getProvider() == AuthProvider.LOCAL) {
                log.info("User {} đã đăng ký bằng email/password trước, cập nhật provider thành GOOGLE.", email);
                user.setProvider(AuthProvider.GOOGLE);
                userRepository.save(user);
            }
        }

        // Tạo JWT token và trả về
        String token = jwtTokenUtil.generateToken(user.getEmail());
        log.info("Google login successful for email: {}", email);
        return token;
    }

}
