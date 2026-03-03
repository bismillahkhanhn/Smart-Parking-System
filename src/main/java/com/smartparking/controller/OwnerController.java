package com.smartparking.controller;
import com.smartparking.model.*;
import com.smartparking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/owner") @RequiredArgsConstructor
public class OwnerController {
    private final ParkingService parkingService;
    private final BookingService bookingService;
    private final PaymentService paymentService;

    @PostMapping("/add-slot")
    public ResponseEntity<?> addSlot(@RequestBody ParkingSlot slot) {
        try { return ResponseEntity.ok(parkingService.addSlot(slot)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/my-slots")
    public ResponseEntity<?> mySlots(@RequestParam Long ownerId) {
        return ResponseEntity.ok(parkingService.getSlotsByOwner(ownerId));
    }

    @PutMapping("/slot/{slotId}")
    public ResponseEntity<?> updateSlot(@PathVariable Long slotId, @RequestParam Long ownerId,
            @RequestParam(required=false) String location,
            @RequestParam(required=false) Double price,
            @RequestParam(required=false) Boolean available) {
        try { return ResponseEntity.ok(parkingService.updateSlot(slotId, ownerId, location, price, available)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @DeleteMapping("/slot/{slotId}")
    public ResponseEntity<?> deleteSlot(@PathVariable Long slotId, @RequestParam Long ownerId) {
        try { parkingService.deleteSlot(slotId, ownerId); return ResponseEntity.ok(Map.of("message","Slot deleted")); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> ownerBookings(@RequestParam Long ownerId) {
        return ResponseEntity.ok(bookingService.getOwnerBookings(ownerId));
    }

    @PutMapping("/bookings/{bookingId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long bookingId, @RequestParam Long ownerId, @RequestParam String status) {
        try {
            Booking.Status s = Booking.Status.valueOf(status.toUpperCase());
            return ResponseEntity.ok(bookingService.updateBookingStatusByOwner(bookingId, ownerId, s));
        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> analytics(@RequestParam Long ownerId) {
        return ResponseEntity.ok(Map.of(
            "ownerId", ownerId,
            "stats",   bookingService.getOwnerBookingStats(ownerId),
            "revenue", paymentService.getOwnerRevenue(ownerId)
        ));
    }
}
