package com.modbus.pm130plus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "pm130.poll-interval=5000")
class Pm130plusApplicationTests {

	@Test
	void contextLoads() {
	}

}
