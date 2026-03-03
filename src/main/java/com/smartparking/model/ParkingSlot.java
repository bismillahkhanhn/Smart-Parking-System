package com.smartparking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "parking_slots")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class ParkingSlot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private boolean available = true;
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"password","verified","hibernateLazyInitializer","handler"})
    private User owner;
}
