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

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class Pm130PollerService {

    private final ModbusTcpMaster master;
    private final Pm130Properties properties;
    private final MeterReadingRepository repository;

    /**
     * Lee periódicamente registros Modbus del PM130 Plus
     * y persiste los valores físicos (V, A, kW).
     */
    @Scheduled(fixedDelayString = "${pm130.poll-interval}")
    public void poll() {
        try {
            // Ejemplo de direcciones de registros Modbus (ajustar según manual PM130 Plus)
            int voltageAddress = 0x0000;
            int currentAddress = 0x0006;
            int powerAddress   = 0x0012;

            double voltage = readRegister(voltageAddress) / 10.0; // Escala de ejemplo
            double current = readRegister(currentAddress) / 100.0;
            double power   = readRegister(powerAddress) / 100.0;

            MeterReading reading = MeterReading.builder()
                    .voltage(voltage)
                    .current(current)
                    .power(power)
                    .timestamp(LocalDateTime.now())
                    .build();

            repository.save(reading);

            log.info("📥 Nueva lectura: {} V | {} A | {} kW", voltage, current, power);

        } catch (Exception e) {
            log.error("❌ Error leyendo el PM130 Plus", e);
        }
    }

    private double readRegister(int address) throws Exception {
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(address, 2, properties.unitId());
        Optional<ReadHoldingRegistersResponse> response =
                master.send(request).get();

        if (response.isPresent()) {
            ByteBuf buf = response.get().getRegisters();
            return buf.readUnsignedShort();
        } else {
            throw new RuntimeException("Sin respuesta de Modbus para dirección " + address);
        }
    }
}