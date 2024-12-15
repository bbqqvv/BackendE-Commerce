package org.bbqqvv.backendecommerce.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bbqqvv.backendecommerce.dto.JwtResponse;
import org.bbqqvv.backendecommerce.dto.request.AuthenticationRequest;
import org.bbqqvv.backendecommerce.dto.request.UserCreationRequest;
import org.bbqqvv.backendecommerce.dto.response.UserResponse;
import org.bbqqvv.backendecommerce.service.auth.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // Đăng ký người dùng
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserCreationRequest creationRequest) {
        UserResponse userResponse = authenticationService.register(creationRequest);
        return userResponse != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(userResponse)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new UserResponse("Registration failed"));
    }

    // Đăng nhập người dùng
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            String token = authenticationService.login(authenticationRequest);
            return ResponseEntity.ok(new JwtResponse(token)); // Trả về JWT token
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }
}
