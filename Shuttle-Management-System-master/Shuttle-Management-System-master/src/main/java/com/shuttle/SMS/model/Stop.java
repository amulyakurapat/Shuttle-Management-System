package com.shuttle.SMS.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
}
