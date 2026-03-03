package com.smartparking.controller;

import com.smartparking.dto.BookingRequest;
import com.smartparking.dto.UserProfileUpdateRequest;
import com.smartparking.service.BookingService;
import com.smartparking.service.ParkingService;
import com.smartparking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final ParkingService parkingService;
    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/available-slots")
    public ResponseEntity<?> availableSlots() {
        return ResponseEntity.ok(parkingService.getAvailableSlots());
    }

    @PostMapping("/book-slot")
    public ResponseEntity<?> bookSlot(@Valid @RequestBody BookingRequest req) {
        try { return ResponseEntity.ok(bookingService.createBooking(req)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> userBookings(@RequestParam Long userId) {
        try { return ResponseEntity.ok(bookingService.getUserBookings(userId)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestParam Long userId) {
        try { return ResponseEntity.ok(userService.getById(userId)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserProfileUpdateRequest req) {
        try {
            return ResponseEntity.ok(userService.updateProfile(
                req.getUserId(), req.getName(), req.getContactNumber(), req.getPassword()));
        } catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
