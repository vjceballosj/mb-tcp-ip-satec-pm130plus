package com.modbus.pm130plus.repository;

import com.modbus.pm130plus.model.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long> {
    Optional<MeterReading> findTopByOrderByTimestampDesc();
    List<MeterReading> findByTimestampBetween(LocalDateTime from, LocalDateTime to);
}
