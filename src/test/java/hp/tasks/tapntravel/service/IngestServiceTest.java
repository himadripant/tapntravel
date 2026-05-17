package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.Tap;
import hp.tasks.tapntravel.repositories.TapRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.jdbc.Sql;

import java.io.FileNotFoundException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@Profile("test")
@Sql("classpath:data-init.sql")
@SpringBootTest
class IngestServiceTest {

    @Autowired
    private IngestService ingestService;

    @Autowired
    private FareCalculationService fareCalculationService;

    @Autowired
    private TapRepository tapRepository;

    @BeforeEach
    public void setUp() {
        ingestService.init();
        fareCalculationService.init();
    }

    @Test
    void ingestInputFile() throws FileNotFoundException {
        ingestService.ingestInputFile();
        var taps = tapRepository.findAll();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(taps),
                () -> Assertions.assertEquals(6, taps.size()),
                () -> assertThat(getPansStartingWith(taps, "5500005"), hasSize(3)),
                () -> assertThat(getPansStartingWith(taps, "4444333"), hasSize(2)),
                () -> assertThat(getPansStartingWith(taps, "4111111"), hasSize(1))
        );

    }

    private List<String> getPansStartingWith(List<Tap> taps, String startingWith) {
        return taps.stream()
                .map(Tap::getPan)
                .filter(str -> str.startsWith(startingWith))
                .toList();
    }
}