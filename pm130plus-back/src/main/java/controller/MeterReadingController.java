package controller;

import model.MeterReading;
import repository.MeterReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
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
