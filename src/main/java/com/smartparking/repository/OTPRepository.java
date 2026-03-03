package com.smartparking.repository;
import com.smartparking.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findTopByEmailOrderByCreatedAtDesc(String email);
}
