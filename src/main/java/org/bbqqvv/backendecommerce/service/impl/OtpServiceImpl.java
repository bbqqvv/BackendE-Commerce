package org.bbqqvv.backendecommerce.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.bbqqvv.backendecommerce.entity.Otp;
import org.bbqqvv.backendecommerce.entity.User;
import org.bbqqvv.backendecommerce.repository.OtpRepository;
import org.bbqqvv.backendecommerce.repository.UserRepository;
import org.bbqqvv.backendecommerce.service.OtpService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    public OtpServiceImpl(OtpRepository otpRepository, UserRepository userRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Gửi OTP đến email
     */
    @Override
    @Transactional
    public String sendOtp(String email) {
        // Xóa OTP cũ nếu có
        otpRepository.findByEmail(email).ifPresent(otpRepository::delete);

        // Tạo OTP mới
        String otp = generateOtp();
        Otp otpEntity = new Otp(email, otp, LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        otpRepository.save(otpEntity);

        try {
            sendOtpEmail(email, otp);
            return "OTP sent successfully!";
        } catch (MessagingException e) {
            log.error("Error sending OTP email: {}", e.getMessage());
            return "Failed to send OTP!";
        }
    }

    /**
     * Xác thực OTP
     */
    @Override
    @Transactional
    public String verifyOtp(String email, String otp) {
        Optional<Otp> otpEntityOptional = otpRepository.findByEmail(email);

        if (otpEntityOptional.isEmpty()) {
            return "OTP not found!";
        }

        Otp otpEntity = otpEntityOptional.get();

        if (!otpEntity.getOtp().equals(otp)) {
            return "Invalid OTP!";
        }

        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            return "OTP expired!";
        }

        otpRepository.delete(otpEntity);
        return "OTP verified successfully!";
    }

    /**
     * Đặt lại mật khẩu sau khi xác thực OTP thành công
     */
    @Override
    @Transactional
    public String resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password reset successful!";
    }

    /**
     * Tạo mã OTP ngẫu nhiên gồm 6 chữ số
     */
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * Gửi email chứa mã OTP
     */
    private void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("[ROWAY] Mã xác thực OTP của bạn");

        // Nội dung HTML
        String content = String.format(
                "<div style='font-family:Arial,sans-serif; text-align:center; color:#333;'>" +
                        "    <h2 style='color:#ff4081;'>💖 Xin chào,</h2>" +
                        "    <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu từ bạn.</p>" +
                        "    <h3 style='color:#ff4081;'>🔐 Mã OTP của bạn: <strong>%s</strong></h3>" +
                        "    <p>Mã này có hiệu lực trong %d phút.</p>" +
                        "    <img src='cid:logo' style='width:200px; margin:20px auto;'/>" +
                        "</div>",
                otp, OTP_EXPIRATION_MINUTES
        );

        helper.setText(content, true);
        helper.addInline("logo", new ClassPathResource("images/roway.png"));

        mailSender.send(message);
    }
}
