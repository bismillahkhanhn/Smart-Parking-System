package com.smartparking.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class BookingRequest {
    @NotNull private Long userId;
    @NotNull private Long slotId;
    @NotNull private LocalDateTime startTime;
    @NotNull private LocalDateTime endTime;
}
