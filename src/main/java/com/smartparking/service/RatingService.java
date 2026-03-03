package com.smartparking.service;
import com.smartparking.model.*;
import com.smartparking.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final BookingService bookingService;

    public Rating addRating(Long bookingId, int score, String comment) {
        if (score < 1 || score > 5) throw new IllegalArgumentException("Score must be 1-5");
        Booking b = bookingService.getBooking(bookingId);
        if (b.getStatus() != Booking.Status.COMPLETED)
            throw new IllegalArgumentException("Rating only allowed after booking is completed");
        Rating r = ratingRepository.findByBookingId(bookingId).orElseGet(Rating::new);
        r.setBooking(b); r.setScore(score); r.setComment(comment);
        return ratingRepository.save(r);
    }

    public Rating getByBooking(Long bookingId) {
        return ratingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found for booking: " + bookingId));
    }
}
