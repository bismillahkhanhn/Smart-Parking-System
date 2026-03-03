package com.smartparking.service;
import com.smartparking.model.*;
import com.smartparking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;

    public Payment processPayment(Long bookingId, double amount) {
        Booking booking = bookingService.getBooking(bookingId);
        if (booking.getStatus() != Booking.Status.APPROVED && booking.getStatus() != Booking.Status.PENDING)
            throw new IllegalArgumentException("Payment only allowed for APPROVED or PENDING bookings");
        Payment p = paymentRepository.findByBookingId(bookingId).orElseGet(Payment::new);
        p.setBooking(booking); p.setAmount(amount); p.setStatus(Payment.Status.COMPLETED);
        booking.setStatus(Booking.Status.COMPLETED);
        bookingService.save(booking);
        return paymentRepository.save(p);
    }

    public double getOwnerRevenue(Long ownerId) {
        return paymentRepository.findByBookingSlotOwnerId(ownerId).stream()
                .filter(p -> p.getStatus() == Payment.Status.COMPLETED)
                .mapToDouble(Payment::getAmount).sum();
    }
}
