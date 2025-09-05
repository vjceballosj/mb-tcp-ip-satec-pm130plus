package com.modbus.pm130plus.config;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.fazecast.jSerialComm.SerialPort;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.OutputStream;

import static com.fazecast.jSerialComm.SerialPort.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ModbusConfig {

    private final Pm130Properties pm;

    // ---------- TCP (DigitalPetri) ----------
    @Bean(destroyMethod = "disconnect")
    @ConditionalOnProperty(name = "pm130.protocol", havingValue = "tcp")
    public ModbusTcpMaster tcpMaster() throws Exception {
        log.info("⚡ Inicializando Modbus TCP -> {}:{}", pm.host(), pm.port());

        ModbusTcpMasterConfig cfg = new ModbusTcpMasterConfig
                .Builder(pm.host())
                .setPort(pm.port())
                .build();

        ModbusTcpMaster master = new ModbusTcpMaster(cfg);
        // Conectar en arranque para fallar rápido si no hay equipo
        master.connect().toCompletableFuture().get();
        return master;
    }

    // ---------- RTU (Modbus4j + jSerialComm) ----------
    @Bean(destroyMethod = "destroy")
    @ConditionalOnProperty(name = "pm130.protocol", havingValue = "rtu")
    public ModbusMaster rtuMaster() throws ModbusInitException {
        int parity = switch (pm.parity().toLowerCase()) {
            case "none" -> NO_PARITY;
            case "odd" -> ODD_PARITY;
            case "even" -> EVEN_PARITY;
            case "mark" -> MARK_PARITY;
            case "space" -> SPACE_PARITY;
            default -> throw new IllegalArgumentException("Paridad inválida: " + pm.parity());
        };

        SerialPortWrapperImpl wrapper = new SerialPortWrapperImpl(
                pm.serialPort(),
                pm.baudrate(),
                pm.databits(),
                pm.stopbits(),
                parity
        );

        ModbusFactory factory = new ModbusFactory();
        ModbusMaster master = factory.createRtuMaster(wrapper);
        master.init(); // abre el puerto
        log.info("🔌 Modbus RTU listo en {}", pm.serialPort());
        return master;
    }

    // ---------- Implementación del wrapper para jSerialComm ----------
    public static class SerialPortWrapperImpl implements SerialPortWrapper {
        private final String commPortId;
        private final int baudRate;
        private final int dataBits;
        private final int stopBits;
        private final int parity;

        private SerialPort port;

        public SerialPortWrapperImpl(String commPortId, int baudRate, int dataBits, int stopBits, int parity) {
            this.commPortId = commPortId;
            this.baudRate = baudRate;
            this.dataBits = dataBits;
            this.stopBits = stopBits;
            this.parity = parity;
        }

        @Override
        public void open() throws Exception {
            port = SerialPort.getCommPort(commPortId);
            port.setComPortParameters(baudRate, dataBits, stopBits, parity);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 1000);

            if (!port.openPort()) {
                throw new Exception("No se pudo abrir el puerto serie: " + commPortId);
            }
            log.info("Puerto serial {} abierto a {} bps", commPortId, baudRate);
        }

        @Override
        public void close() {
            if (port != null && port.isOpen()) {
                port.closePort();
                log.info("Puerto serial {} cerrado", commPortId);
            }
        }

        @Override
        public InputStream getInputStream() {
            if (port == null) {
                throw new IllegalStateException("Puerto serial no inicializado. Llama a open() primero.");
            }
            return port.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() {
            if (port == null) {
                throw new IllegalStateException("Puerto serial no inicializado. Llama a open() primero.");
            }
            return port.getOutputStream();
        }

        @Override public int getBaudRate() { return baudRate; }
        @Override public int getDataBits() { return dataBits; }
        @Override public int getStopBits() { return stopBits; }
        @Override public int getParity() { return parity; }

        @Override public int getFlowControlIn() { return SerialPort.FLOW_CONTROL_DISABLED; }
        @Override public int getFlowControlOut() { return SerialPort.FLOW_CONTROL_DISABLED; }
    }
}
