package com.modbus.pm130plus.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meter_readings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer voltage;    // V
    private Integer current;    // A
    private Integer power;      // kW (o W si luego escalas)

    @Column(nullable = false)
    private LocalDateTime timestamp;
}


