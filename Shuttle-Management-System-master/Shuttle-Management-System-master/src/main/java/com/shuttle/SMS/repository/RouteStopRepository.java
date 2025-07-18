package com.shuttle.SMS.repository;

import com.shuttle.SMS.model.Route;
import com.shuttle.SMS.model.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByRouteOrderBySequence(Route route);
}
