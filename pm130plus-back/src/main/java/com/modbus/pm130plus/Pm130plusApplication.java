/**
 * Entry point for the PM130 Plus Modbus TCP backend application.
 * Provides REST APIs to read data from the SATEC PM130 Plus meter
 * and persist results into MySQL.
 */
package com.modbus.pm130plus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.modbus.pm130plus.config")
public class Pm130plusApplication {

    public static void main(String[] args) {
        SpringApplication.run(Pm130plusApplication.class, args);
    }
}

