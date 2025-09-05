package com.modbus.pm130plus.controller;

import com.modbus.pm130plus.model.MeterReading;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.modbus.pm130plus.repository.MeterReadingRepository;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Angular dev
public class MeterReadingController {

    private final MeterReadingRepository repo;

    // Todas las lecturas
    @GetMapping
    public List<MeterReading> all() {
        return repo.findAll();
    }

    // Lectura por ID
    @GetMapping("/{id}")
    public MeterReading byId(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Lectura no encontrada con id=" + id));
    }

    // Última lectura registrada
    @GetMapping("/latest")
    public MeterReading latest() {
        return repo.findTopByOrderByTimestampDesc()
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "No hay lecturas registradas aún"));
    }

    // Lecturas por rango de fechas
    @GetMapping("/range")
    public List<MeterReading> byRange(@RequestParam("from") LocalDateTime from,
                                      @RequestParam("to") LocalDateTime to) {
        return repo.findByTimestampBetween(from, to);
    }
}
