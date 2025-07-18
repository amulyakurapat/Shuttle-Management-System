package com.shuttle.SMS.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TripHistoryDTO {
    private Long bookingId;
    private LocalDateTime bookingTime;
    private LocalDate travelDate;
    private String startStopName;
    private String endStopName;
    private BigDecimal fare;
    private BigDecimal pointsDeducted;
}
