package hp.tasks.tapntravel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Profile("test")
@Sql("classpath:data.sql")
@SpringBootTest
class ExportTripFileServiceTest {

    @Autowired
    private IngestService ingestService;

    @Autowired
    private FareCalculationService fareCalculationService;

    @Autowired
    private ExportTripFileService exportTripFileService;

    @Value("${app.output-file-path}")
    private String outputFilePath;

    @BeforeEach
    void setUp() {
        ingestService.init();
        fareCalculationService.init();
    }

    @Test
    void exportTripFile() throws FileNotFoundException {
        ingestService.ingestInputFile();
        exportTripFileService.exportTripFile();
        final var file = ResourceUtils.getFile(outputFilePath);
        try (Stream<String> stream = Files.lines(file.toPath())) {
            var outputRows = stream.skip(1)
                    .map(str -> str.split(","))
                    .toList();
            assertEquals(6, outputRows.size());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}