package com.smartparking.service;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.User;
import com.smartparking.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class ParkingService {
    private final ParkingRepository parkingRepository;
    private final UserService userService;

    public ParkingSlot addSlot(ParkingSlot slot) {
        if (slot.getOwner() == null || slot.getOwner().getId() == null)
            throw new IllegalArgumentException("Owner id required");
        User owner = userService.getById(slot.getOwner().getId());
        if (owner.getRole() != User.Role.OWNER)
            throw new IllegalArgumentException("Only OWNER users can add slots");
        slot.setOwner(owner); slot.setAvailable(true);
        return parkingRepository.save(slot);
    }

    public List<ParkingSlot> getAvailableSlots() { return parkingRepository.findByAvailableTrue(); }
    public List<ParkingSlot> getSlotsByOwner(Long ownerId) { return parkingRepository.findByOwnerId(ownerId); }
    public ParkingSlot getSlot(Long id) {
        return parkingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + id));
    }
    public ParkingSlot save(ParkingSlot slot) { return parkingRepository.save(slot); }

    public ParkingSlot updateSlot(Long slotId, Long ownerId, String location, Double price, Boolean available) {
        ParkingSlot slot = getSlot(slotId);
        if (!slot.getOwner().getId().equals(ownerId))
            throw new IllegalArgumentException("Not authorized to update this slot");
        if (location != null && !location.isBlank()) slot.setLocation(location);
        if (price != null) slot.setPrice(price);
        if (available != null) slot.setAvailable(available);
        return parkingRepository.save(slot);
    }

    public void deleteSlot(Long slotId, Long ownerId) {
        ParkingSlot slot = getSlot(slotId);
        if (!slot.getOwner().getId().equals(ownerId))
            throw new IllegalArgumentException("Not authorized to delete this slot");
        parkingRepository.delete(slot);
    }
}
