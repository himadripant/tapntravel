package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.repositories.TapRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExportTripFile {
    private final TapRepository tapRepository;

    public ExportTripFile(@Value("${app.output-file-path}") String inputFilePath,
                          TapRepository tapRepository) {
        this.tapRepository = tapRepository;
    }

    public void exportTripFile() {
        tapRepository.findAll().forEach(tap -> {

        });
    }
}
