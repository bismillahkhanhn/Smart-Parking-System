package com.smartparking.controller;
import com.smartparking.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/ratings") @RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<?> addRating(@RequestParam Long bookingId,
            @RequestParam int score,
            @RequestParam(required=false, defaultValue="") String comment) {
        try { return ResponseEntity.ok(ratingService.addRating(bookingId, score, comment)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> get(@PathVariable Long bookingId) {
        try { return ResponseEntity.ok(ratingService.getByBooking(bookingId)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
