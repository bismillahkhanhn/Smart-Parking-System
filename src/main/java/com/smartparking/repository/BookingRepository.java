package com.smartparking.repository;
import com.smartparking.model.Booking;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBySlotAndEndTimeAfterAndStartTimeBefore(ParkingSlot slot, LocalDateTime start, LocalDateTime end);
    List<Booking> findByUser(User user);
    List<Booking> findBySlotOwnerId(Long ownerId);
    long countBySlotOwnerIdAndStatus(Long ownerId, Booking.Status status);
}
