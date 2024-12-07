package org.bbqqvv.backendecommerce.service.auth;



import org.bbqqvv.backendecommerce.config.jwt.JwtTokenUtil;
import org.bbqqvv.backendecommerce.dto.LoginUserDto;
import org.bbqqvv.backendecommerce.dto.RegisterUserDto;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.service.UserService;
import org.bbqqvv.backendecommerce.util.ValidateUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
	
    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;
    
    private JwtTokenUtil jwtTokenUtil;
    
    public AuthenticationService(UserService userService, 
    		PasswordEncoder passwordEncoder, 
    		AuthenticationManager authenticationManager,
    		JwtTokenUtil jwtTokenUtil) {
    	this.userService = userService;
    	this.passwordEncoder = passwordEncoder;
    	this.authenticationManager = authenticationManager;
    	this.jwtTokenUtil = jwtTokenUtil;
    }

    public User register(RegisterUserDto registerUserDto) {
    	ValidateUtils.validateUsername(registerUserDto.getUsername());
    	if (userService.existsByUsername(registerUserDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        String encodedPassword = passwordEncoder.encode(registerUserDto.getPassword());
        User user = new User(registerUserDto.getUsername(), encodedPassword, registerUserDto.getEmail());
        return userService.createUser(user);
    }

    public String login(LoginUserDto loginUserDto) {
    	Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginUserDto.getUsername(),
            		loginUserDto.getPassword())
        );
        // Generate JWT token
    	return jwtTokenUtil.generateToken(((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername());
    }
}
