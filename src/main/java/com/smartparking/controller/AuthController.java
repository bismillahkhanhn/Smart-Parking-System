package com.smartparking.controller;
import com.smartparking.dto.LoginRequest;
import com.smartparking.dto.RegisterRequest;
import com.smartparking.model.OTP;
import com.smartparking.model.User;
import com.smartparking.service.OTPService;
import com.smartparking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;
    private final OTPService otpService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            log.info("Registration request for email: {}", req.getEmail());
            
            User user = new User();
            user.setName(req.getName());
            user.setEmail(req.getEmail());
            user.setPassword(req.getPassword());
            user.setRole(User.Role.valueOf(req.getRole().toUpperCase()));
            userService.register(user);
            
            OTP otp = otpService.generateAndStore(req.getEmail());
            
            log.info("User registered and OTP sent: {}", req.getEmail());
            return ResponseEntity.ok(Map.of(
                "message", "Registration successful! OTP sent to " + req.getEmail(),
                "expiresAt", otp.getExpiresAt().toString()
            ));
        } catch (IllegalArgumentException e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Registration failed", e);
            return ResponseEntity.status(500).body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            log.info("Login attempt for email: {}", req.getEmail());
            return userService.login(req.getEmail(), req.getPassword())
                    .<ResponseEntity<?>>map(user -> {
                        user.setPassword(null);
                        log.info("Login successful: {}", req.getEmail());
                        return ResponseEntity.ok(user);
                    })
                    .orElseGet(() -> {
                        log.warn("Login failed - invalid credentials or account not verified: {}", req.getEmail());
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Invalid credentials or account not verified yet"));
                    });
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.status(500).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        try {
            log.info("OTP request for email: {}", email);
            OTP otp = otpService.generateAndStore(email);
            return ResponseEntity.ok(Map.of(
                "message", "OTP sent successfully",
                "expiresAt", otp.getExpiresAt().toString()
            ));
        } catch (Exception e) {
            log.error("Failed to send OTP", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String code) {
        try {
            log.info("OTP verification attempt for email: {}", email);
            
            if (!otpService.verify(email, code)) {
                log.warn("OTP verification failed for email: {}", email);
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
            }
            
            userService.activateUserByEmail(email);
            log.info("User verified and activated: {}", email);
            
            return ResponseEntity.ok(Map.of("message", "Account verified! You can now login."));
        } catch (Exception e) {
            log.error("OTP verification error", e);
            return ResponseEntity.status(500).body(Map.of("error", "Verification failed: " + e.getMessage()));
        }
    }
}
