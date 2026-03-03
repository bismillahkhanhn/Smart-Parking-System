package com.smartparking.service;
import com.smartparking.dto.BookingRequest;
import com.smartparking.model.*;
import com.smartparking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ParkingService parkingService;
    private final UserService userService;

    public Booking createBooking(BookingRequest req) {
        if (!req.getEndTime().isAfter(req.getStartTime()))
            throw new IllegalArgumentException("End time must be after start time");
        User user = userService.getById(req.getUserId());
        ParkingSlot slot = parkingService.getSlot(req.getSlotId());
        if (!slot.isAvailable()) throw new IllegalArgumentException("Slot is not available");
        if (!bookingRepository.findBySlotAndEndTimeAfterAndStartTimeBefore(slot, req.getStartTime(), req.getEndTime()).isEmpty())
            throw new IllegalArgumentException("Slot already booked for that time");
        Booking b = new Booking();
        b.setUser(user); b.setSlot(slot);
        b.setStartTime(req.getStartTime()); b.setEndTime(req.getEndTime());
        b.setStatus(Booking.Status.PENDING);
        return bookingRepository.save(b);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUser(userService.getById(userId));
    }

    public Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
    }

    public Booking cancelBooking(Long id) {
        Booking b = getBooking(id);
        if (b.getStatus() == Booking.Status.COMPLETED)
            throw new IllegalArgumentException("Cannot cancel completed booking");
        b.setStatus(Booking.Status.CANCELLED);
        return bookingRepository.save(b);
    }

    public Booking save(Booking b) { return bookingRepository.save(b); }

    public List<Booking> getOwnerBookings(Long ownerId) {
        return bookingRepository.findBySlotOwnerId(ownerId);
    }

    public Booking updateBookingStatusByOwner(Long bookingId, Long ownerId, Booking.Status status) {
        Booking b = getBooking(bookingId);
        if (!b.getSlot().getOwner().getId().equals(ownerId))
            throw new IllegalArgumentException("Not authorized");
        if (status != Booking.Status.APPROVED && status != Booking.Status.REJECTED)
            throw new IllegalArgumentException("Owner can only APPROVE or REJECT");
        b.setStatus(status);
        return bookingRepository.save(b);
    }

    public Map<String, Long> getOwnerBookingStats(Long ownerId) {
        return Map.of(
            "totalBookings",     (long) bookingRepository.findBySlotOwnerId(ownerId).size(),
            "pendingBookings",   bookingRepository.countBySlotOwnerIdAndStatus(ownerId, Booking.Status.PENDING),
            "approvedBookings",  bookingRepository.countBySlotOwnerIdAndStatus(ownerId, Booking.Status.APPROVED),
            "rejectedBookings",  bookingRepository.countBySlotOwnerIdAndStatus(ownerId, Booking.Status.REJECTED),
            "completedBookings", bookingRepository.countBySlotOwnerIdAndStatus(ownerId, Booking.Status.COMPLETED)
        );
    }
}
