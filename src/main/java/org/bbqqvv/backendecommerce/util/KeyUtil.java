package org.bbqqvv.backendecommerce.util;


import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class KeyUtil {
	/**
     * Tạo SecretKey từ chuỗi base64 và kiểm tra kích thước khóa.
     * @param base64Key chuỗi base64 của khóa bí mật
     * @return SecretKey
     * @throws IllegalArgumentException nếu khóa không có kích thước hợp lệ
     */
    public static SecretKey createSecretKeyFromBase64(String base64Key) {
        // Giải mã chuỗi base64 thành byte array
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);

        // Kiểm tra kích thước khóa (phải >= 256 bit, tức là 32 byte)
        if (decodedKey.length < 32) {
            throw new IllegalArgumentException("Khóa của bạn phải có ít nhất 256 bit.");
        }

        // Tạo SecretKey từ khóa đã giải mã
        SecretKey secretKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());

        // Kiểm tra xem khóa có kích thước đúng 256 bit (32 byte) không
        if (secretKey.getEncoded().length != 32) {
            throw new IllegalArgumentException("Khóa của bạn phải có kích thước 256 bit.");
        }

        return secretKey;
    }
}
