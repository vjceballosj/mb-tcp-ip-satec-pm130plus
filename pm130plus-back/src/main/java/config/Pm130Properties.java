package config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pm130")
public class Pm130Properties {
    private String host;
    private int port;
    private int unitId;
    private long pollInterval;
}
