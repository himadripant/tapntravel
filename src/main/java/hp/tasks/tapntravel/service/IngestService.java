package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.Stop;
import hp.tasks.tapntravel.entities.Tap;
import hp.tasks.tapntravel.models.TapFromFile;
import hp.tasks.tapntravel.models.TapType;
import hp.tasks.tapntravel.repositories.StopRepository;
import hp.tasks.tapntravel.repositories.TapRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
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
    private final FareCalculationService fareCalculationService;
    private Map<Integer, Stop> stopCache = new HashMap<>();

    public IngestService(
            @Value("${app.input-file-path}") String inputFilePath,
            TapRepository tapRepository,
            StopRepository stopRepository, FareCalculationService fareCalculationService
    ) {
        this.inputFilePath = inputFilePath;
        this.tapRepository = tapRepository;
        this.stopRepository = stopRepository;
        this.fareCalculationService = fareCalculationService;
    }

    @PostConstruct
    public void init() {
        this.stopCache = stopRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Stop::getId, stop -> stop));
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

    @Transactional
    void persistToDb(List<TapFromFile> tapFromFileList) {
        List<Tap> tapOnEntities1 = createTapOnEntities(tapFromFileList);
        List<TapFromFile> tapOffsFromFile = tapOffsFromTapFromFileList(tapFromFileList);
        List<Tap> tapOnEntities2 = findTapOffForTapOn(tapOnEntities1, tapOffsFromFile).stream()
                .map(this::mapToTapEntityForTapOff)
                .toList();
        List<Tap> completedTaps = Stream.concat(tapOnEntities1.stream(), tapOnEntities2.stream())
                .map(this::fillStatusAndCost)
                .toList();
        var tapOns = tapRepository.saveAllAndFlush(completedTaps);
        logger.info("Persisted tapping-on data to DB [{}]", tapOns);
    }

    List<Tap> createTapOnEntities(List<TapFromFile> taps) {
        return taps.stream()
                .filter(tapFromFile -> tapFromFile.tapType() == TapType.ON)
                .map(this::mapToTapEntityForTapOn)
                .toList();
    }

    List<TapFromFile> tapOffsFromTapFromFileList(List<TapFromFile> taps) {
        return taps.stream()
                .filter(tapFromFile -> tapFromFile.tapType() == TapType.OFF)
                .toList();
    }

    List<TapFromFile> findTapOffForTapOn(List<Tap> tapEntities, List<TapFromFile> tapOffs) {
        for (Tap tapEntity : tapEntities) {
            var tapOffOptional = tapOffs.stream()
                    .filter(tapFromFile -> (Objects.equals(tapFromFile.pan(), tapEntity.getPan())) &&
                            (Objects.equals(tapFromFile.busId(), tapEntity.getBusId())) &&
                            (Objects.equals(tapFromFile.companyId(), tapEntity.getBusCompanyId())))
                    .findFirst();
            if (tapOffOptional.isPresent()) {
                var tapOff = tapOffOptional.get();
                var stop = stopCache.get(tapOff.stopId());
                tapEntity.setEndStop(stop)
                        .setEndDateTime(tapOff.timestamp());
                return findTapOffForTapOn(tapEntities.stream().filter(e -> !e.equals(tapEntity)).toList(),
                        tapOffs.stream().filter(e -> !e.equals(tapOff)).toList());
            }
        }
        return tapOffs;
    }

    private Tap mapToTapEntityForTapOn(TapFromFile tap) {
        return new Tap(
                tap.pan(), tap.busId(), tap.companyId(), stopCache.get(tap.stopId()), tap.timestamp()
        );
    }

    private Tap mapToTapEntityForTapOff(TapFromFile tap) {
        final var tapEntity = tapRepository.findByPanAndBusIdAndBusCompanyId(
                        tap.pan(), tap.busId(), tap.companyId()
                )
                .getFirst();
        return tapEntity.setEndStop(stopCache.get(tap.stopId()))
                .setEndDateTime(tap.timestamp());
    }

    private Tap fillStatusAndCost(Tap tap) {
        var statusAndCost = fareCalculationService.calculateTripFares(
                tap.getBusCompanyId(),
                tap.getBeginStop(),
                tap.getEndStop()
        );
        return tap.setStatus(statusAndCost.getKey())
                .setCost(statusAndCost.getValue());
    }
}
