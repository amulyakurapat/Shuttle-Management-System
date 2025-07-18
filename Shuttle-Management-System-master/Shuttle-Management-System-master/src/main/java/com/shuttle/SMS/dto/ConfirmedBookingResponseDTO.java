package com.shuttle.SMS.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConfirmedBookingResponseDTO {
    private Long bookingId;
    private Long userId;
    private Long shuttleId;
    private String shuttleVehicleNumber;
    private double cost;
    private double estimatedTravelTime;
    private LocalDateTime bookingTime;
    private String status;
}
