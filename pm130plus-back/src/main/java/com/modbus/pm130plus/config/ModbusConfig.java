package com.modbus.pm130plus.config;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import com.serotonin.modbus4j.serial.rtu.RtuSerialPortWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ModbusConfig {

    private final Pm130Properties properties;

    @Bean(destroyMethod = "disconnect")
    public ModbusTcpMaster tcpMaster() throws Exception {
        if (!"tcp".equalsIgnoreCase(properties.protocol())) {
            return null; // no se usa en este modo
        }

        log.info("⚡ Inicializando Modbus TCP Master hacia {}:{}",
                properties.host(), properties.port());

        ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(properties.host())
                .setPort(properties.port())
                .build();

        ModbusTcpMaster master = new ModbusTcpMaster(config);
        master.connect().get();
        return master;
    }

    @Bean(destroyMethod = "destroy")
    public ModbusMaster rtuMaster() {
        if (!"rtu".equalsIgnoreCase(properties.protocol())) {
            return null; // no se usa en este modo
        }

        log.info("⚡ Inicializando Modbus RTU Master en puerto {}",
                properties.serialPort());

        SerialPortWrapper wrapper = new RtuSerialPortWrapper(
                properties.serialPort(),
                properties.baudrate(),
                properties.databits(),
                properties.stopbits(),
                properties.parity()
        );

        ModbusFactory factory = new ModbusFactory();
        return factory.createRtuMaster(wrapper);
    }
}
