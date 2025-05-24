package org.bbqqvv.backendecommerce.config;

import org.bbqqvv.backendecommerce.config.jwt.JwtAuthenticationFilter;
import org.bbqqvv.backendecommerce.config.jwt.JwtTokenUtil;
import org.bbqqvv.backendecommerce.service.auth.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // Các URL không yêu cầu xác thực
    private static final String[] WHITE_LIST_URL = {
            "/auth/login",
            "/auth/register",
            "/auth/oauth2/google",
            "/auth/forgot-password",
            "/auth/verify-otp",
            "/auth/reset-password",
            "/api/categories/**",
            "/api/products-review/**",
            "/api/products/**",
            "/api/cart/**",
            "/api/orders/**",
            "/api/search-history/**",
            "api/addresses/**",
            "api/favourites/**",
            "/api/filter/**",

            // ⚠️ Các URL Swagger cần được permitAll
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/swagger-resources/**",
            "/configuration/**",
            "/webjars/**"
    };
    private static final String[] SECURED_URL_PATTERNS = {
            "/api/**",
            "/admin/**",
            "/user/**"
    };

    // Cấu hình SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Cấu hình CORS
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho API REST
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(WHITE_LIST_URL).permitAll() // ✅ Các API trong danh sách này không cần xác thực
                        .requestMatchers(SECURED_URL_PATTERNS).authenticated() // Các phương thức POST, PUT, DELETE yêu cầu xác thực
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class); // Thêm filter JWT vào chuỗi bảo mật

        return http.build();
    }

    // Cấu hình AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }


    // Cấu hình CORS
    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000"
        ));
        config.addAllowedOriginPattern("*"); //Cấu hình tạm thời
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}