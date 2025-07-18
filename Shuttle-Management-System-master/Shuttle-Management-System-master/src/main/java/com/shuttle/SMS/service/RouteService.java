package com.shuttle.SMS.service;

import com.shuttle.SMS.dto.RouteCreationDTO;
import com.shuttle.SMS.model.Route;

public interface RouteService {
    Route createRoute(RouteCreationDTO routeCreationDTO);
}
