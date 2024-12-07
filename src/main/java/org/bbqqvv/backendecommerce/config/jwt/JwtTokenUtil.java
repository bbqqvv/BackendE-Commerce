package org.bbqqvv.backendecommerce.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bbqqvv.backendecommerce.util.KeyUtil;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenUtil {
	
	private final SecretKey secretKey;
    private final long expiration;
    
    public JwtTokenUtil(JwtConfigProperties jwtConfigProperties) {
    	// Giải mã và tạo khóa bảo mật từ chuỗi base64
        this.secretKey = KeyUtil.createSecretKeyFromBase64(jwtConfigProperties.getSecretKey());
        // Đặt thời gian hết hạn
        this.expiration = jwtConfigProperties.getExpiration();
    }

    public String generateToken(String username) {
    	return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)  // Sử dụng secretKey dưới dạng byte[]
                .compact();
    }
    
    
    // Trích xuất username từ JWT
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Trích xuất Claims từ JWT
    private Claims extractClaims(String token) {
    	return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Kiểm tra token có hết hạn chưa
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Kiểm tra tính hợp lệ của token
    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
