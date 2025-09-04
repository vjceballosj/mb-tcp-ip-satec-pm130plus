package com.modbus.pm130plus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pm130")
public record Pm130Properties(
        String host,
        int port,
        int unitId,
        long pollInterval
) {}

