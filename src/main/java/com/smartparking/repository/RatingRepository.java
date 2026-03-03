package com.smartparking.repository;
import com.smartparking.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByBookingId(Long bookingId);
}
