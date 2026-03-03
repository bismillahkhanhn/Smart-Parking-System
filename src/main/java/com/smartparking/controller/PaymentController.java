package com.smartparking.controller;
import com.smartparking.dto.PaymentRequest;
import com.smartparking.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/payments") @RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> pay(@Valid @RequestBody PaymentRequest req) {
        try { return ResponseEntity.ok(paymentService.processPayment(req.getBookingId(), req.getAmount())); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
}
