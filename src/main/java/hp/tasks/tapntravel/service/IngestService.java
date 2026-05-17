package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.Tap;
import hp.tasks.tapntravel.models.TapFromFile;
import hp.tasks.tapntravel.models.TapType;
import hp.tasks.tapntravel.repositories.StopRepository;
import hp.tasks.tapntravel.repositories.TapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

import static hp.tasks.tapntravel.commons.Utilities.parseStringToZonedDateTime;
import static hp.tasks.tapntravel.commons.Utilities.validatePan;
import static hp.tasks.tapntravel.models.TapType.tapType;
import static java.util.stream.Collectors.joining;

@Component
public class IngestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String inputFilePath;
    private final TapRepository tapRepository;
    private final StopRepository stopRepository;

    public IngestService(
            @Value("${app.input-file-path}") String inputFilePath,
            TapRepository tapRepository,
            StopRepository stopRepository
    ) {
        this.inputFilePath = inputFilePath;
        this.tapRepository = tapRepository;
        this.stopRepository = stopRepository;
    }

    public void ingestInputFile() throws FileNotFoundException {
        final var file = ResourceUtils.getFile(inputFilePath);
        logger.info("Reading input file [{}]", file.getAbsolutePath());
        try (Stream<String> stream = Files.lines(file.toPath())) {
            stream.skip(1)
                    .map(str -> str.split(","))
                    .gather(Gatherers.windowFixed(100))
                    .map(this::mapStringsListToTapFromFileList)
                    .forEach(this::persistToDb);
        } catch (IOException e) {
            logger.error("Error reading input file [{}]", file.getAbsolutePath(), e);
            throw new RuntimeException(e);
        }
    }

    private List<TapFromFile> mapStringsListToTapFromFileList(List<String[]> rows) {
        return rows.stream()
                .map(this::mapRowToTapFromFile)
                .filter(Objects::nonNull)
                .toList();
    }

    private TapFromFile mapRowToTapFromFile(String[] row) {
        try {
            row = Arrays.stream(row).map(String::trim).toArray(String[]::new);
            return new TapFromFile(
                    Long.valueOf(row[0]),
                    parseStringToZonedDateTime(row[1]),
                    tapType(row[2]),
                    Integer.valueOf(row[3]),
                    Integer.valueOf(row[4]),
                    row[5],
                    validatePan(row[6])
            );
        } catch (IllegalArgumentException exception) {
            logger.error("Error parsing row [{}]", Arrays.stream(row).limit(6).collect(joining(",")), exception);
            return null;
        }
    }

    void persistToDb(List<TapFromFile> tapFromFileList) {
        final var tapOns = tapFromFileList.stream()
                .filter(tapFromFile -> tapFromFile.tapType() == TapType.ON)
                .map(this::mapToTapEntityForTapOn)
                .toList();
        final var tapOnsSaved = tapRepository.saveAllAndFlush(tapOns);
        logger.info("Persisting tapping-on data to DB [{}]", tapOnsSaved);

        final var tapOffs = tapFromFileList.stream()
                .filter(tapFromFile -> tapFromFile.tapType() == TapType.OFF)
                .map(this::mapToTapEntityForTapOff)
                .toList();
        tapRepository.saveAllAndFlush(tapOffs);
        logger.info("Persisting tapping-off data to DB [{}]", tapOffs);
    }

    private Tap mapToTapEntityForTapOn(TapFromFile tap) {
        return new Tap(
                tap.pan(), tap.busId(), tap.companyId(), stopRepository.getReferenceById(tap.stopId()), tap.timestamp()
        );
    }

    private Tap mapToTapEntityForTapOff(TapFromFile tap) {
        final var tapEntity = tapRepository.findByPanAndBusIdAndBusCompanyId(
                        tap.pan(), tap.busId(), tap.companyId()
                )
                .getFirst();
        tapEntity.setEndStop(stopRepository.getReferenceById(tap.stopId()))
                .setEndDateTime(tap.timestamp());
        return tapEntity;
    }
}
