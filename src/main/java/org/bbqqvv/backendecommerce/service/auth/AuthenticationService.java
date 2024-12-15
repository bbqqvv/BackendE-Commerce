package org.bbqqvv.backendecommerce.service.auth;

import org.bbqqvv.backendecommerce.config.jwt.JwtTokenUtil;
import org.bbqqvv.backendecommerce.dto.request.AuthenticationRequest;
import org.bbqqvv.backendecommerce.dto.request.UserCreationRequest;
import org.bbqqvv.backendecommerce.dto.response.UserResponse;
import org.bbqqvv.backendecommerce.service.UserService;
import org.bbqqvv.backendecommerce.util.ValidateUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticationService(UserService userService,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    public UserResponse register(UserCreationRequest registerUserDto) {
        ValidateUtils.validateUsername(registerUserDto.getUsername());
        if (userService.existsByUsername(registerUserDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        String encodedPassword = passwordEncoder.encode(registerUserDto.getPassword());
        UserCreationRequest userRequest = UserCreationRequest.builder()
                .username(registerUserDto.getUsername())
                .password(encodedPassword)
                .email(registerUserDto.getEmail())
                .build();
        return userService.createUser(userRequest);
    }

    public String login(AuthenticationRequest loginUserDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getUsername(), loginUserDto.getPassword())
        );
        return jwtTokenUtil.generateToken(((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername());
    }
}
