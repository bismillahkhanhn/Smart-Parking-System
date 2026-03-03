package com.smartparking.repository;
import com.smartparking.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ParkingRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByAvailableTrue();
    List<ParkingSlot> findByOwnerId(Long ownerId);
}
