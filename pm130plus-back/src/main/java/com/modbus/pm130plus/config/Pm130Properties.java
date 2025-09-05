package com.modbus.pm130plus.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pm130")
public record Pm130Properties(

        // --- General ---
        @NotBlank String protocol,       // "tcp" o "rtu"
        @Min(1) int unitId,              // ID del dispositivo esclavo
        @Min(1000) long pollInterval,    // intervalo mínimo = 1s

        // --- TCP ---
        String host,
        @Min(1) int port,

        // --- RTU ---
        String serialPort,               // Ejemplo: COM3, /dev/ttyUSB0
        @Min(1200) int baudrate,         // mínimo 1200 bps
        @Min(5) int databits,            // usual: 7 u 8
        @Min(1) int stopbits,            // usual: 1 o 2
        @NotBlank String parity          // "none", "even", "odd"
) {}
