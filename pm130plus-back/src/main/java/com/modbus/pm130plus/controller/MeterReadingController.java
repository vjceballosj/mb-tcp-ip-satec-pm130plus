package com.modbus.pm130plus.controller;

import com.modbus.pm130plus.model.MeterReading;
import com.modbus.pm130plus.repository.MeterReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Ajusta si tu frontend corre en otro host
public class MeterReadingController {

    private final MeterReadingRepository repository;

    @GetMapping
    public List<MeterReading> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public MeterReading getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }
}
