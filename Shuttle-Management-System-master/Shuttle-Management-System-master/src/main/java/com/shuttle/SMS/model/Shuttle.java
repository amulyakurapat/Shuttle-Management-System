package com.shuttle.SMS.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shuttles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shuttle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number", nullable = false, unique = true)
    private String vehicleNumber;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "current_status", nullable = false)
    private String currentStatus;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    // Add the avgSpeed field here
    @Column(name = "avg_speed")
    private Double avgSpeed;
}
