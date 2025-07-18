package com.shuttle.SMS.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "route_stops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    @Column(nullable = false)
    private Integer sequence;
}
