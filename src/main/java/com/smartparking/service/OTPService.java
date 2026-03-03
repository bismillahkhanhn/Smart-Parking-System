package com.smartparking.service;
import com.smartparking.model.OTP;
import com.smartparking.repository.OTPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OTPService {
    private final OTPRepository otpRepository;
    private final JavaMailSender mailSender;

    @Value("${otp.expiry-minutes:10}")
    private int expiryMinutes;

    public OTP generateAndStore(String email) {
        try {
            String code = String.format("%06d", new Random().nextInt(1_000_000));
            OTP otp = new OTP();
            otp.setEmail(email);
            otp.setCode(code);
            otp.setCreatedAt(LocalDateTime.now());
            otp.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
            otp.setVerified(false);
            OTP saved = otpRepository.save(otp);
            
            // Send OTP email
            sendOtpEmail(email, code);
            
            return saved;
        } catch (Exception e) {
            log.error("Error generating OTP for email {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to generate OTP: " + e.getMessage());
        }
    }

    public boolean verify(String email, String code) {
        try {
            Optional<OTP> opt = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
            if (opt.isEmpty()) {
                log.warn("No OTP found for email: {}", email);
                return false;
            }
            
            OTP otp = opt.get();
            
            // Check if already verified
            if (otp.isVerified()) {
                log.warn("OTP already verified for email: {}", email);
                return false;
            }
            
            // Check if expired
            if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
                log.warn("OTP expired for email: {}", email);
                return false;
            }
            
            // Check if code matches
            if (!otp.getCode().equals(code)) {
                log.warn("Incorrect OTP code for email: {}", email);
                return false;
            }
            
            // Mark as verified
            otp.setVerified(true);
            otpRepository.save(otp);
            log.info("OTP verified successfully for email: {}", email);
            return true;
        } catch (Exception e) {
            log.error("Error verifying OTP for email {}: {}", email, e.getMessage(), e);
            return false;
        }
    }

    private void sendOtpEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom("bismi.15052005@gmail.com");
            message.setSubject("SmartPark OTP - Email Verification");
            message.setText("Your One-Time Password (OTP) for SmartPark registration is:\n\n" +
                    "OTP: " + code + "\n\n" +
                    "This OTP will expire in " + expiryMinutes + " minutes.\n\n" +
                    "Do not share this code with anyone.\n\n" +
                    "If you did not request this OTP, please ignore this email.");
            
            mailSender.send(message);
            log.info("✓ OTP email successfully sent to: {}", email);
        } catch (Exception e) {
            log.error("✗ Failed to send OTP email to {}: {}", email, e.getMessage(), e);
            log.warn("OTP Code for {} = {} (Email sending failed, check email configuration)", email, code);
        }
    }
}
