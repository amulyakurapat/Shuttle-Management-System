package com.shuttle.SMS.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "start_stop_id", nullable = false)
    private Stop startStop;
    @ManyToOne
    @JoinColumn(name = "end_stop_id", nullable = false)
    private Stop endStop;

    @ManyToOne
    @JoinColumn(name = "shuttle_id", nullable = false)
    private Shuttle shuttle;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @Column(name = "fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal fare;

    @Column(name = "points_deducted", nullable = false, precision = 10, scale = 2)
    private BigDecimal pointsDeducted;

    @Column(name = "status", nullable = false)
    private String status;
}
