package org.bbqqvv.backendecommerce.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.config.jwt.JwtTokenUtil;
import org.bbqqvv.backendecommerce.entity.AuthProvider;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
        log.info("Logging in with Google token");

        // Gửi request lấy thông tin user từ Google
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            log.error("Failed to fetch user info from Google: {}", e.getMessage());
            throw new RuntimeException("Google authentication failed", e);
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("Google authentication failed: Invalid response");
            throw new RuntimeException("Google authentication failed");
        }

        // Trích xuất thông tin user từ Google
        Map<String, Object> googleUser = response.getBody();
        String email = (String) googleUser.get("email");
        String name = (String) googleUser.get("name");
        String googleId = (String) googleUser.get("sub"); // Google user ID

        if (email == null || googleId == null) {
            log.error("Google response missing required fields");
            throw new RuntimeException("Invalid Google user data");
        }

        // Kiểm tra xem user đã tồn tại chưa
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Nếu user chưa tồn tại, tạo mới từ Google
            log.info("Creating new user from Google login: {}", email);
            user = User.builder()
                    .email(email)
                    .username(name)
                    .provider(AuthProvider.GOOGLE)
                    .providerId(googleId) // Lưu Google ID
                    .build();
            userRepository.save(user);
        } else {
            // Nếu user đã tồn tại nhưng chưa có providerId, cập nhật nó
            if (user.getProviderId() == null) {
                user.setProviderId(googleId);
                userRepository.save(user);
            }

            // Nếu user đăng ký bằng email/password trước, nhưng giờ đăng nhập bằng Google, liên kết tài khoản
            if (user.getProvider() == AuthProvider.LOCAL) {
                log.info("User {} đã đăng ký bằng email/password trước, cập nhật provider thành GOOGLE.", email);
                user.setProvider(AuthProvider.GOOGLE);
                userRepository.save(user);
            }
        }

        // Tạo JWT token cho user
        String token = jwtTokenUtil.generateToken(user.getEmail());
        log.info("Google login successful for email: {}", email);

        return token;
    }


}
