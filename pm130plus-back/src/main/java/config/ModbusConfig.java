package config;
import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.master.ModbusTcpMasterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModbusConfig {

    private final Pm130Properties properties;

    public ModbusConfig(Pm130Properties properties) {
        this.properties = properties;
    }

    @Bean
    public ModbusTcpMaster modbusMaster() {
        ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder(properties.getHost())
                .setPort(properties.getPort())
                .build();

        ModbusTcpMaster master = new ModbusTcpMaster(config);
        master.connect();
        return master;
    }
}
