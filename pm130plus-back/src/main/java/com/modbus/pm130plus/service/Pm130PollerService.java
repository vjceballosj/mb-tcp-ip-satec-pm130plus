package com.modbus.pm130plus.service;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.modbus.pm130plus.config.Pm130Properties;
import com.modbus.pm130plus.model.MeterReading;
import com.modbus.pm130plus.repository.MeterReadingRepository;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class Pm130PollerService {

    private final ModbusTcpMaster master;
    private final Pm130Properties properties;
    private final MeterReadingRepository repository;

    /**
     * Polls the PM130 Plus meter at fixed intervals.
     * Interval is configurable in application.properties -> pm130.poll-interval (ms).
     */
    @Scheduled(fixedDelayString = "${pm130.poll-interval:5000}")
    public void pollMeter() {
        log.debug("🔄 Polling PM130 Plus at {}:{}", properties.host(), properties.port());

        try {
            // Ejemplo: leer 6 registros desde dirección 0
            ReadHoldingRegistersRequest request =
                    new ReadHoldingRegistersRequest(0, 6);

            CompletableFuture<ReadHoldingRegistersResponse> future =
                    master.sendRequest(request, properties.unitId());

            future.whenComplete((response, ex) -> {
                if (ex != null) {
                    log.error("❌ Error polling PM130 Plus: {}", ex.getMessage());
                    return;
                }

                if (response != null) {
                    ByteBuf buf = response.getRegisters();

                    // ⚡ Ejemplo simple: leer 3 valores
                    double voltage = buf.readUnsignedShort();
                    double current = buf.readUnsignedShort();
                    double power   = buf.readUnsignedShort();

                    MeterReading reading = MeterReading.builder()
                            .timestamp(Instant.now())
                            .voltage(voltage)
                            .current(current)
                            .power(power)
                            .build();

                    repository.save(reading);

                    log.info("✅ Saved reading -> V: {}, I: {}, P: {}",
                            voltage, current, power);
                }
            });

        } catch (Exception e) {
            log.warn("⚠️ Polling failed: {}", e.getMessage());
        }
    }
}