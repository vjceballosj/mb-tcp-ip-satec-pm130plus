package com.modbus.pm130plus.controller;

import com.modbus.pm130plus.model.MeterReading;
import com.modbus.pm130plus.repository.MeterReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingRepository repository;

    /**
     * Get all meter readings.
     */
    @GetMapping
    public ResponseEntity<List<MeterReading>> getAll() {
        List<MeterReading> readings = repository.findAll();
        log.info("📡 Retrieved {} readings", readings.size());
        return ResponseEntity.ok(readings);
    }

    /**
     * Get one reading by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MeterReading> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete one reading by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            log.info("🗑️ Deleted reading with id={}", id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
