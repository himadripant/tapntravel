package hp.tasks.tapntravel;

import hp.tasks.tapntravel.service.FareCalculationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = FareCalculationService.class)
class TapReaderTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testReadValidCsvFile() throws Exception {
    }
}