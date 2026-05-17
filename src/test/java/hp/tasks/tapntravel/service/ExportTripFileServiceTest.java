package hp.tasks.tapntravel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.jdbc.Sql;

import java.io.FileNotFoundException;

@Profile("test")
@Sql("classpath:data-init.sql")
@SpringBootTest
class ExportTripFileServiceTest {

    @Autowired
    private IngestService ingestService;

    @Autowired
    private FareCalculationService fareCalculationService;

    @Autowired
    private ExportTripFileService exportTripFileService;

    @BeforeEach
    void setUp() {
        ingestService.init();
        fareCalculationService.init();
    }

    @Test
    void exportTripFile() throws FileNotFoundException {
        ingestService.ingestInputFile();
        exportTripFileService.exportTripFile();
    }
}