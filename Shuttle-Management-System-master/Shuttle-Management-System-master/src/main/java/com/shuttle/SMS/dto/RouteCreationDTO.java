package com.shuttle.SMS.dto;

import lombok.Data;
import java.util.List;

@Data
public class RouteCreationDTO {
    private String name;
    private String description;
    private Long adminId;
    private List<Long> stopIds;
}
