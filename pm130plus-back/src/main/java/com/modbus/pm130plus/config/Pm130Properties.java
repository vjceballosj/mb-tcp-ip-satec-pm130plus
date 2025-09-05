package com.modbus.pm130plus.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pm130")
public record Pm130Properties(

        @NotBlank String protocol,       // "tcp" o "rtu"

        // --- TCP ---
        String host,
        int port,

        // --- RTU ---
        String serialPort,
        @Min(1200) int baudrate,
        @Min(5) int databits,
        @Min(1) int stopbits,
        @NotBlank String parity,

        // --- General ---
        @Min(1) int unitId,
        @Min(1000) long pollInterval

) {}
