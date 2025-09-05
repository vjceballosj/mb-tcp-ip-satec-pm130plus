package com.modbus.pm130plus.service;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.modbus.pm130plus.config.Pm130Properties;
import com.modbus.pm130plus.model.MeterReading;
import com.modbus.pm130plus.repository.MeterReadingRepository;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.locator.BaseLocator;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired(required = false)
    private ModbusTcpMaster tcpMaster;

    @Autowired(required = false)
    private ModbusMaster rtuMaster;

    @PreDestroy
    public void shutdown() {
        try {
            if (tcpMaster != null) {
                tcpMaster.disconnect().get();
                log.info("TCP Master desconectado correctamente.");
            }
        } catch (Exception e) {
            log.warn("Error al cerrar TCP Master: {}", e.getMessage());
        }

        if (rtuMaster != null) {
            rtuMaster.destroy();
            log.info("RTU Master cerrado correctamente.");
        }
    }

    @Scheduled(fixedDelayString = "${pm130.poll-interval}")
    public void poll() {
        String mode = properties.protocol() == null ? "tcp" : properties.protocol().toLowerCase();

        switch (mode) {
            case "tcp" -> pollTcp();
            case "rtu" -> pollRtu();
            default -> log.error("Protocolo no soportado: {}", mode);
        }
    }

    private void pollTcp() {
        if (tcpMaster == null) {
            log.warn("TCP Master no disponible.");
            return;
        }
        try {
            int start = 0;
            int qty = 6;

            ReadHoldingRegistersRequest req = new ReadHoldingRegistersRequest(start, qty);
            CompletableFuture<ReadHoldingRegistersResponse> fut =
                    tcpMaster.sendRequest(req, properties.unitId());

            fut.whenComplete((resp, ex) -> {
                if (ex != null) {
                    log.error("Error en petición Modbus TCP", ex);
                    return;
                }
                if (resp != null && resp.getRegisters() != null) {
                    int voltage = resp.getRegisters().getUnsignedShort(0);
                    int current = resp.getRegisters().getUnsignedShort(1);
                    int power   = resp.getRegisters().getUnsignedShort(2);
                    save(voltage, current, power);
                } else {
                    log.warn("Respuesta TCP vacía.");
                }
            });
        } catch (Exception e) {
            log.error("Excepción en pollTcp()", e);
        }
    }

    private void pollRtu() {
        if (rtuMaster == null) {
            log.warn("RTU Master no disponible.");
            return;
        }
        try {
            int sid = properties.unitId();
            int start = 0;

            Number voltage = rtuMaster.getValue(BaseLocator.holdingRegister(sid, start,     DataType.TWO_BYTE_INT_UNSIGNED));
            Number current = rtuMaster.getValue(BaseLocator.holdingRegister(sid, start + 1, DataType.TWO_BYTE_INT_UNSIGNED));
            Number power   = rtuMaster.getValue(BaseLocator.holdingRegister(sid, start + 2, DataType.TWO_BYTE_INT_UNSIGNED));

            save(voltage.intValue(), current.intValue(), power.intValue());
        } catch (Exception e) {
            log.error("Error en pollRtu()", e);
        }
    }

    private void save(int voltage, int current, int power) {
        repository.save(MeterReading.builder()
                .timestamp(LocalDateTime.now())
                .voltage(voltage)
                .current(current)
                .power(power)
                .build());

        log.info("Lectura guardada: {} V, {} A, {} kW", voltage, current, power);
    }
}
