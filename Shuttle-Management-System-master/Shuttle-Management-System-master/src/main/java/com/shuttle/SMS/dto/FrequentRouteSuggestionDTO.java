package com.shuttle.SMS.dto;

import lombok.Data;

@Data
public class FrequentRouteSuggestionDTO {
    private Long routeId;
    private String routeName;
    private long bookingCount;
}
