package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.Stop;
import hp.tasks.tapntravel.entities.Tap;
import hp.tasks.tapntravel.models.TapFromFile;
import hp.tasks.tapntravel.models.TapIdentifierKey;
import hp.tasks.tapntravel.models.TapType;
import hp.tasks.tapntravel.repositories.StopRepository;
import hp.tasks.tapntravel.repositories.TapRepository;
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

    public void init() {
        this.stopCache = stopRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Stop::getId, stop -> stop));
        logger.info("Ingest service started and cached stops: {}", stopCache);
    }

    public void ingestInputFile() throws FileNotFoundException {
        init();
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
        // build a cache of tap-ons as they arrive from file's each row
        Map<TapIdentifierKey, Tap> tapOnCache = new HashMap<>();
        // build a collection of tap-on-offs
        List<Tap> tapTripEntities = new ArrayList<>();
        tapFromFileList
                .forEach(tapFromFile -> {
                    var tapIdentifier = mapToTapIdentifierKey(tapFromFile);
                    if (tapOnCache.containsKey(tapIdentifier)) {
                        /* if tap-on cache contains the tap row from file then check if the row is tap-off
                        then add to the collection of tap-on-offs and remove from cache */
                        var tapEntityFromCache = tapOnCache.get(tapIdentifier);
                        if (tapFromFile.tapType() == TapType.OFF) {
                            tapEntityFromCache.setEndStop(stopCache.get(tapFromFile.stopId()))
                                    .setEndDateTime(tapFromFile.timestamp());
                            tapTripEntities.add(tapEntityFromCache);
                            // removed from cache
                            tapOnCache.remove(tapIdentifier);
                        }
                    } else if (tapFromFile.tapType() == TapType.ON) {
                        /* if the tap row from file is tap on and not present in cache
                            then add to the cache */
                        tapOnCache.put(tapIdentifier, mapToTapEntityForTapOn(tapFromFile));
                    } else if (tapFromFile.tapType() == TapType.OFF) {
                        /* finding the tap-on in the db table */
                        var tapOnOffEntity = mapToTapEntityForTapOffFromDb(tapFromFile);
                        if (tapOnOffEntity == null) {
                            logger.error("Tap record couldn't find match [{}]", tapFromFile);
                        } else  {
                            tapTripEntities.add(tapOnOffEntity);
                        }
                    }
                });
        if (!tapOnCache.isEmpty()) {
            tapTripEntities.addAll(tapOnCache.values());
        }
        var tapTripsFinalizedInChunk = tapRepository.saveAllAndFlush(tapTripEntities.stream()
                .map(this::fillStatusAndCost)
                .toList());
        logger.info("Persisted tapping-on data to DB [{}]", tapTripsFinalizedInChunk);
    }

    private Tap mapToTapEntityForTapOffFromDb(TapFromFile tap) {
        var tapResponse = tapRepository.findByPanAndBusIdAndBusCompanyId(
                tap.pan(), tap.busId(), tap.companyId()
        ).stream().findFirst();
        tapResponse.ifPresent(t -> t.setEndStop(stopCache.get(tap.stopId()))
                .setEndDateTime(tap.timestamp()));
        return tapResponse.orElse(null);
    }

    private TapIdentifierKey mapToTapIdentifierKey(TapFromFile tap) {
        return new TapIdentifierKey(tap.pan(), tap.companyId(), tap.busId());
    }

    private Tap mapToTapEntityForTapOn(TapFromFile tap) {
        return new Tap(
                tap.pan(), tap.busId(), tap.companyId(), stopCache.get(tap.stopId()), tap.timestamp()
        );
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
