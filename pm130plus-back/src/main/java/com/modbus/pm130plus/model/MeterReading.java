package com.modbus.pm130plus.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "meter_readings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private double voltage;

    @Column(nullable = false)
    private double current;

    @Column(nullable = false)
    private double power;
}

