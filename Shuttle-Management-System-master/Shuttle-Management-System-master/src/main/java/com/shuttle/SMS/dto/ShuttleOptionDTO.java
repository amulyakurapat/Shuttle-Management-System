package com.shuttle.SMS.dto;

import lombok.Data;

@Data
public class ShuttleOptionDTO {
    private Long shuttleId;
    private String shuttleVehicleNumber;
    private double cost;
    private double estimatedTravelTime;
}
