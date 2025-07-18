package com.shuttle.SMS.service;

import com.shuttle.SMS.dto.RouteCreationDTO;
import com.shuttle.SMS.model.Route;
import com.shuttle.SMS.model.RouteStop;
import com.shuttle.SMS.model.Stop;
import com.shuttle.SMS.model.User;
import com.shuttle.SMS.repository.RouteRepository;
import com.shuttle.SMS.repository.RouteStopRepository;
import com.shuttle.SMS.repository.StopRepository;
import com.shuttle.SMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteStopRepository routeStopRepository;
    private final StopRepository stopRepository;
    private final UserRepository userRepository;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository,
                            RouteStopRepository routeStopRepository,
                            StopRepository stopRepository,
                            UserRepository userRepository) {
        this.routeRepository = routeRepository;
        this.routeStopRepository = routeStopRepository;
        this.stopRepository = stopRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Route createRoute(RouteCreationDTO dto) {
        User admin = userRepository.findById(dto.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        Route route = Route.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .admin(admin)
                .createdAt(LocalDateTime.now())
                .build();
        route = routeRepository.save(route);
        List<RouteStop> routeStops = new ArrayList<>();
        int seq = 1;
        for (Long stopId : dto.getStopIds()) {
            Stop stop = stopRepository.findById(stopId)
                    .orElseThrow(() -> new RuntimeException("Stop not found: " + stopId));
            RouteStop rs = RouteStop.builder()
                    .route(route)
                    .stop(stop)
                    .sequence(seq++)
                    .build();
            routeStops.add(rs);
        }
        routeStopRepository.saveAll(routeStops);
        return route;
    }
}
