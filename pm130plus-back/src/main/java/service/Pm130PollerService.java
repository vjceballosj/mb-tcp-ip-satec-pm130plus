package service;

import com.digitalpetri.modbus.master.ModbusTcpMaster;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import config.Pm130Properties;
import model.MeterReading;
import repository.MeterReadingRepository;
import io.netty.util.ReferenceCountUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class Pm130PollerService {

    private final ModbusTcpMaster master;
    private final Pm130Properties properties;
    private final MeterReadingRepository repository;

    @PostConstruct
    public void init() {
        System.out.println("Iniciando Poller PM130 con intervalo: " + properties.getPollInterval() + " ms");
    }

    @Scheduled(fixedDelayString = "${pm130.poll-interval}")
    public void pollRegister() {
        int registerAddress = 0; // Ajustar según manual del PM130
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(registerAddress, 1);

        master.sendRequest(request, properties.getUnitId())
                .thenAccept(response -> {
                    try {
                        if (response instanceof ReadHoldingRegistersResponse registersResponse) {
                            int value = registersResponse.getRegisters().readUnsignedShort(0);

                            MeterReading reading = MeterReading.builder()
                                    .registerAddress(registerAddress)
                                    .value(value)
                                    .timestamp(LocalDateTime.now())
                                    .build();

                            repository.save(reading);

                            System.out.println("Leído registro " + registerAddress + " = " + value);
                        } else {
                            System.err.println("Respuesta inesperada: " + response);
                        }
                    } finally {
                        ReferenceCountUtil.release(response);
                    }
                });
    }

}

