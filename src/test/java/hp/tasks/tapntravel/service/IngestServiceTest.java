package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.repositories.StopRepository;
import hp.tasks.tapntravel.repositories.TapRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
class IngestServiceTest {

    @Autowired
    private IngestService ingestService;

    @Autowired
    private TapRepository tapRepository;

    @Autowired
    private StopRepository stopRepository;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void ingestInputFile() throws FileNotFoundException {
        var stops = stopRepository.findAll();
        Assertions.assertNotNull(stops);
        ingestService.ingestInputFile();
        var taps = tapRepository.findAll();
        Assertions.assertNotNull(taps);
    }

    @Test
    void persistToDb() {
    }
}