package com.smartparking.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class UserProfileUpdateRequest {
    @NotNull private Long userId;
    private String name;
    private String contactNumber;
    private String password;
}
