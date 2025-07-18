package com.shuttle.SMS.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long bookingId;
    private String startStopName;
    private String endStopName;
    private String shuttleVehicleNumber;
    private Double fare;
    private LocalDateTime bookingTime;
    private String status;
}
