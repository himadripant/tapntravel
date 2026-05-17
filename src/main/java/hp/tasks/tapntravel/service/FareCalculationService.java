package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.ZoneFare;
import hp.tasks.tapntravel.models.BusZoneFare;
import hp.tasks.tapntravel.repositories.TapRepository;
import hp.tasks.tapntravel.repositories.ZoneFareRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FareCalculationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ZoneFareRepository zoneFareRepository;

    private Map<BusZoneFare, BigDecimal> busZoneFaresPrices;

    public FareCalculationService(TapRepository tapRepository, ZoneFareRepository zoneFareRepository) {
        this.zoneFareRepository = zoneFareRepository;
    }

    @PostConstruct
    void init() {
        this.busZoneFaresPrices = zoneFareRepository.findAll()
                .stream()
                .collect(Collectors.toMap(this::mapToBusZoneFare, ZoneFare::getPrice));
    }

    public BigDecimal calculateTripFares(Integer busCompanyId, Integer zoneFrom, Integer zoneTo) {
        return busZoneFaresPrices.computeIfAbsent(new BusZoneFare(busCompanyId, zoneFrom, zoneTo),
                _ -> BigDecimal.ZERO);
    }

    private BusZoneFare mapToBusZoneFare(ZoneFare zoneFare) {
        return new BusZoneFare(zoneFare.getBusCompanyId(), zoneFare.getZoneFrom(),  zoneFare.getZoneTo());
    }

}
