package com.modbus.pm130plus.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una lectura del PM130 Plus,
 * con valores físicos ya transformados.
 */
@Entity
@Table(name = "meter_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double voltage;   // Voltaje en V
    private double current;   // Corriente en A
    private double power;     // Potencia en kW

    private LocalDateTime timestamp;
}


