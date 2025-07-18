package com.shuttle.SMS.dto;

import lombok.Data;
import java.util.List;

@Data
public class TripHistoryResponseDTO {
    private List<TripHistoryDTO> tripHistory;
    private List<FrequentRouteSuggestionDTO> frequentRoutes;
}
