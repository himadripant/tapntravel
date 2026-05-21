package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.Tap;
import hp.tasks.tapntravel.models.TripStatus;
import hp.tasks.tapntravel.repositories.StopRepository;
import hp.tasks.tapntravel.repositories.TapRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Profile("test")
@SpringBootTest
class IngestServiceTest {

    @Autowired
    private FareCalculationService fareCalculationService;

    @Autowired
    private TapRepository tapRepository;

    @Autowired
    private StopRepository stopRepository;

    @Autowired
    private DataSource dataSource;

    private void loadFixture() {
        new JdbcTemplate(dataSource).execute("delete from tap");
        var fixtureSqlPath = "data.sql";
        new ResourceDatabasePopulator(new ClassPathResource(fixtureSqlPath)).execute(dataSource);
        fareCalculationService.init();
    }

    static Stream<Arguments> inputCsvFiles() {
        return Stream.of(
                Arguments.of("taps.csv", 6, 3, 2, 1),
                Arguments.of("taps-alt.csv", 3, 1, 1, 1)
        );
    }

    @ParameterizedTest(name = "Ingest with input: {0}")
    @MethodSource("inputCsvFiles")
    void ingestInputFile(
            String inputCsvFile,
            int expectedTotalTrips,
            int expectedCompletedTrips,
            int expectedIncompleteTrips,
            int expectedCancelledTrips
    ) throws FileNotFoundException {
        loadFixture();
        var ingestService = new IngestService(
                "classpath:" + inputCsvFile, tapRepository, stopRepository, fareCalculationService
        );
        ingestService.init();
        ingestService.ingestInputFile();
        var taps = tapRepository.findAll();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(taps),
                () -> assertEquals(expectedTotalTrips, taps.size()),
                () -> assertThat(filterByStatus(taps, TripStatus.COMPLETED), hasSize(expectedCompletedTrips)),
                () -> assertThat(filterByStatus(taps, TripStatus.INCOMPLETE), hasSize(expectedIncompleteTrips)),
                () -> assertThat(filterByStatus(taps, TripStatus.CANCELLED), hasSize(expectedCancelledTrips))
        );
    }

    private List<Tap> filterByStatus(List<Tap> taps, TripStatus status) {
        return taps.stream()
                .filter(tap -> tap.getStatus().equals(status.name()))
                .toList();
    }
}
