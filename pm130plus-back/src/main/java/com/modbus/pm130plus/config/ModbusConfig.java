package com.modbus.pm130plus.config;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ModbusConfig {

    private final Pm130Properties properties;
    private ModbusTcpMaster master;

    public ModbusConfig(Pm130Properties properties) {
        this.properties = properties;
    }

    @Bean
    public ModbusTcpMaster modbusMaster() {
        ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(properties.host())
                .setPort(properties.port())
                .build();

        this.master = new ModbusTcpMaster(config);

        try {
            master.connect();
            log.info("✅ Connected to PM130 Plus at {}:{}", properties.host(), properties.port());
        } catch (Exception e) {
            log.warn("⚠️ Could not connect to PM130 Plus at {}:{} - {}",
                    properties.host(), properties.port(), e.getMessage());
        }

        return master;
    }

    @PreDestroy
    public void cleanup() {
        if (master != null) {
            try {
                master.disconnect();
                log.info("🔌 Modbus master disconnected cleanly.");
            } catch (Exception e) {
                log.warn("⚠️ Error while disconnecting Modbus master: {}", e.getMessage());
            }
        }
    }
}
