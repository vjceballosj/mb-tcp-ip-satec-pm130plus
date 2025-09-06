package com.modbus.pm130plus.service;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.modbus.pm130plus.config.Pm130Properties;
import com.modbus.pm130plus.model.MeterReading;
import com.modbus.pm130plus.repository.MeterReadingRepository;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.locator.BaseLocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "pm130.protocol=tcp",   // por defecto TCP
        "pm130.unit-id=1",
        "pm130.poll-interval=1000"
})
class Pm130PollerServiceTest {

    @Autowired
    private Pm130PollerService pollerService;

    @Autowired
    private MeterReadingRepository repository;

    @Autowired
    private ModbusTcpMaster tcpMaster;

    @Autowired
    private ModbusMaster rtuMaster;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        Mockito.reset(tcpMaster, rtuMaster); // limpiar mocks entre tests
    }

    @Test
    void testPollTcpSavesReading() throws Exception {
        // 🔹 Simulamos respuesta Modbus TCP
        ByteBuf buf = Unpooled.buffer(6);
        buf.writeShort(220);   // voltaje
        buf.writeShort(10);    // corriente
        buf.writeShort(2200);  // potencia

        ReadHoldingRegistersResponse fakeResponse = Mockito.mock(ReadHoldingRegistersResponse.class);
        Mockito.when(fakeResponse.getRegisters()).thenReturn(buf);

        Mockito.when(tcpMaster.sendRequest(Mockito.any(ReadHoldingRegistersRequest.class), Mockito.eq(1)))
                .thenReturn(CompletableFuture.completedFuture(fakeResponse));

        // Ejecutamos el polling
        pollerService.poll();

        List<MeterReading> readings = repository.findAll();
        assertThat(readings).hasSize(1);

        MeterReading r = readings.get(0);
        assertThat(r.getVoltage()).isEqualTo(220);
        assertThat(r.getCurrent()).isEqualTo(10);
        assertThat(r.getPower()).isEqualTo(2200);
    }

    @Test
    void testPollRtuSavesReading() throws Exception {
        // 🔹 Simulamos respuesta Modbus RTU
        Mockito.when(rtuMaster.getValue(Mockito.any(BaseLocator.class)))
                .thenAnswer(inv -> {
                    int address = ((BaseLocator<?>) inv.getArgument(0)).getOffset();
                    return switch (address) {
                        case 0 -> 220;   // voltaje
                        case 1 -> 10;    // corriente
                        case 2 -> 2200;  // potencia
                        default -> 0;
                    };
                });

        // 🔹 Forzamos RTU en runtime cambiando el protocolo
        Field protocolField = pollerService.getClass().getDeclaredField("properties");
        protocolField.setAccessible(true);
        Pm130Properties props = (Pm130Properties) protocolField.get(pollerService);

        Pm130Properties newProps = new Pm130Properties(
                "rtu",                 // protocolo
                props.unitId(),
                props.pollInterval(),
                props.host(),
                props.port(),
                props.serialPort(),
                props.baudrate(),
                props.databits(),
                props.stopbits(),
                props.parity()
        );

        protocolField.set(pollerService, newProps);

        // Ejecutamos el polling
        pollerService.poll();

        List<MeterReading> readings = repository.findAll();
        assertThat(readings).hasSize(1);

        MeterReading r = readings.get(0);
        assertThat(r.getVoltage()).isEqualTo(220);
        assertThat(r.getCurrent()).isEqualTo(10);
        assertThat(r.getPower()).isEqualTo(2200);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        ModbusTcpMaster tcpMaster() {
            return Mockito.mock(ModbusTcpMaster.class);
        }

        @Bean
        ModbusMaster rtuMaster() {
            return Mockito.mock(ModbusMaster.class);
        }
    }
}
