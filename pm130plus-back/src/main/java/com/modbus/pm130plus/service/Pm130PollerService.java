package com.modbus.pm130plus.service;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.modbus.pm130plus.config.Pm130Properties;
import com.modbus.pm130plus.model.MeterReading;
import com.modbus.pm130plus.repository.MeterReadingRepository;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.locator.BaseLocator;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class Pm130PollerService {

    private final Pm130Properties properties;
    private final MeterReadingRepository repository;

    // Inyectados condicionalmente
    private final ModbusTcpMaster tcpMaster;   // DigitalPetri
    private final ModbusMaster rtuMaster;      // Modbus4j

    @PreDestroy
    public void shutdown() {
        log.info("🛑 Cerrando conexiones Modbus...");
        try {
            if (tcpMaster != null) {
                tcpMaster.disconnect().get();
            }
        } catch (Exception e) {
            log.warn("⚠️ Error cerrando TCP master: {}", e.getMessage());
        }
        if (rtuMaster != null) {
            rtuMaster.destroy();
        }
    }

    /**
     * Tarea periódica de polling (cada pollInterval ms).
     */
    @Scheduled(fixedDelayString = "${pm130.poll-interval}")
    public void poll() {
        String protocol = properties.protocol().toLowerCase();

        switch (protocol) {
            case "tcp" -> pollTcp();
            case "rtu" -> pollRtu();
            default -> log.error("❌ Protocolo no soportado: {}", properties.protocol());
        }
    }

    /**
     * 🔹 Polling por TCP/IP usando DigitalPetri.
     */
    private void pollTcp() {
        if (tcpMaster == null) {
            log.warn("⚠️ TCP Master no inicializado. Revisa configuración.");
            return;
        }

        try {
            int startAddress = 0;
            int quantity = 6; // ejemplo: 6 registros

            ReadHoldingRegistersRequest request =
                    new ReadHoldingRegistersRequest(startAddress, quantity);

            CompletableFuture<ReadHoldingRegistersResponse> future =
                    tcpMaster.sendRequest(request, properties.unitId());

            future.whenComplete((response, ex) -> {
                if (response != null) {
                    int voltage = response.getRegisters().readUnsignedShort(0);
                    int current = response.getRegisters().readUnsignedShort(2);
                    int power   = response.getRegisters().readUnsignedShort(4);

                    saveReading(voltage, current, power);
                } else {
                    log.error("❌ Error en polling TCP: {}", ex.getMessage());
                }
            });

        } catch (Exception e) {
            log.error("❌ Excepción en pollTcp()", e);
        }
    }

    /**
     * 🔹 Polling por RTU/RS485 usando Modbus4j.
     */
    private void pollRtu() {
        if (rtuMaster == null) {
            log.warn("⚠️ RTU Master no inicializado. Revisa configuración.");
            return;
        }

        try {
            int slaveId = properties.unitId();
            int startAddress = 0;

            Number voltage = rtuMaster.getValue(
                    BaseLocator.holdingRegister(slaveId, startAddress, BaseLocator.INTEGER16));

            Number current = rtuMaster.getValue(
                    BaseLocator.holdingRegister(slaveId, startAddress + 1, BaseLocator.INTEGER16));

            Number power = rtuMaster.getValue(
                    BaseLocator.holdingRegister(slaveId, startAddress + 2, BaseLocator.INTEGER16));

            saveReading(voltage.intValue(), current.intValue(), power.intValue());

        } catch (Exception e) {
            log.error("❌ Error en polling RTU", e);
        }
    }

    /**
     * 🔹 Guardar en la base de datos.
     */
    private void saveReading(int voltage, int current, int power) {
        MeterReading reading = MeterReading.builder()
                .timestamp(LocalDateTime.now())
                .voltage(voltage)
                .current(current)
                .power(power)
                .build();

        repository.save(reading);
        log.info("✅ Lectura guardada: {} V, {} A, {} kW",
                voltage, current, power);
    }
}
