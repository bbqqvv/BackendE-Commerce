package org.bbqqvv.backendecommerce.controller;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bbqqvv.backendecommerce.dto.JwtResponse;
import org.bbqqvv.backendecommerce.dto.LoginUserDto;
import org.bbqqvv.backendecommerce.dto.RegisterUserDto;
import org.bbqqvv.backendecommerce.entity.User;
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
	
    private AuthenticationService authenticationService;
    
    public AuthController(AuthenticationService authenticationService) {
    	this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User user = authenticationService.register(registerUserDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto loginUserDto) {
    	try {
            // Generate JWT token
            String token = authenticationService.login(loginUserDto);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
