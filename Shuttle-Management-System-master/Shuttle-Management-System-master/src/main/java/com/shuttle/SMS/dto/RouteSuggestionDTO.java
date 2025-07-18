package com.shuttle.SMS.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouteSuggestionDTO {
    private Long routeId;
    private String routeName;
    private List<String> stops;
    private double cost;
    private double estimatedTravelTime;
}
