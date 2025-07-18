package com.shuttle.SMS.controller;

import com.shuttle.SMS.dto.RouteCreationDTO;
import com.shuttle.SMS.model.Route;
import com.shuttle.SMS.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/routes")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody RouteCreationDTO routeCreationDTO) {
        Route route = routeService.createRoute(routeCreationDTO);
        return ResponseEntity.ok(route);
    }
}
