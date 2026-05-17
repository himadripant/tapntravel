package hp.tasks.tapntravel.service;

import com.opencsv.CSVWriter;
import hp.tasks.tapntravel.entities.Stop;
import hp.tasks.tapntravel.repositories.TapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;

@Service
public class ExportTripFileService {
    private final Logger logger = LoggerFactory.getLogger(ExportTripFileService.class);
    private final TapRepository tapRepository;
    private final String outputFilePath;

    public ExportTripFileService(@Value("${app.output-file-path}") String outputFilePath,
                                 TapRepository tapRepository) {
        this.outputFilePath = outputFilePath;
        this.tapRepository = tapRepository;
    }

    public void exportTripFile() {
        try {
            Path path = Paths.get(outputFilePath);
            Files.deleteIfExists(path);
            Files.createFile(path);
            try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile(), false))) {
                writer.writeNext(new String[]{"Started", "Finished", "DurationSecs", "FromStopId", "ToStopId", "ChargeAmount", "CompanyId", "PAN", "Status"});
                tapRepository.findAll().forEach(tap ->
                        writer.writeNext(new String[] {
                                tap.getBeginDateTime().toString(),
                                findEndDatetime(tap.getEndDateTime()),
                                calculateDurationInSeconds(tap.getBeginDateTime(), tap.getEndDateTime()),
                                String.valueOf(tap.getBeginStop().getId()),
                                findEndStop(tap.getEndStop()),
                                String.format("$%.2f", tap.getCost()),
                                String.valueOf(tap.getBusCompanyId()),
                                tap.getPan(),
                                tap.getStatus()
                        })
                );
            } catch (IOException e) {
                logger.error("Trip file could not be created.. ", e);
            }
        } catch (IOException e) {
            logger.error("Trip file could not be created.. ", e);
        } finally {
            tapRepository.deleteAll();
        }

    }

    private String calculateDurationInSeconds(ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
        if (endDateTime == null) {
            return "";
        }
        return String.valueOf(Duration.between(startDateTime, endDateTime).toSeconds());
    }

    private String findEndStop(Stop stop) {
        if (stop == null) {
            return "";
        }
        return String.valueOf(stop.getId());
    }

    private String findEndDatetime(ZonedDateTime endDateTime) {
        if (endDateTime == null) {
            return "";
        }
        return endDateTime.toString();
    }
}
