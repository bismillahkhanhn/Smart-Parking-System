package com.smartparking.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class PaymentRequest {
    @NotNull private Long bookingId;
    @Positive private double amount;
}
