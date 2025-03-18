package org.bbqqvv.backendecommerce.service;

public interface OtpService {
    String sendOtp(String email);

    String verifyOtp(String email, String otp);

    String resetPassword(String email, String newPassword);
}
