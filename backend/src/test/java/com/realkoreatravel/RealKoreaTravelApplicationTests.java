package com.realkoreatravel;

import com.realkoreatravel.support.PostgresTestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Import(PostgresTestcontainersConfiguration.class)
class RealKoreaTravelApplicationTests {

	@Test
	void contextLoads() {
	}

}
