package com.shuttle.SMS.dto;

import lombok.Data;

@Data
public class ConfirmBookingRequestDTO {
    private Long userId;
    private Double currentLatitude;
    private Double currentLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private Long selectedShuttleId;
}
