package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.Stop;
import hp.tasks.tapntravel.entities.ZoneFare;
import hp.tasks.tapntravel.models.BusCompanyZone;
import hp.tasks.tapntravel.models.BusCompanyZones;
import hp.tasks.tapntravel.models.TripStatus;
import hp.tasks.tapntravel.repositories.ZoneFareRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FareCalculationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ZoneFareRepository zoneFareRepository;

    private Map<BusCompanyZones, BigDecimal> busCompanyZonesPrices;

    private final Map<BusCompanyZone, BigDecimal> busCompanyZonesMaxPrices = new HashMap<>();

    public FareCalculationService(ZoneFareRepository zoneFareRepository) {
        this.zoneFareRepository = zoneFareRepository;
    }

    @PostConstruct
    public void init() {
        this.busCompanyZonesPrices = zoneFareRepository.findAll()
                .stream()
                .collect(Collectors.toMap(this::mapToBusZoneFare, ZoneFare::getPrice));
        logger.info("Zone Fare Prices: {}", this.busCompanyZonesPrices);

        this.busCompanyZonesPrices
                .forEach((key, price) -> {
                    addBusCompanyZonePrice(new BusCompanyZone(key.busCompanyId(), key.zoneFrom()), price);
                    addBusCompanyZonePrice(new BusCompanyZone(key.busCompanyId(), key.zoneTo()), price);
                });
        logger.info("Zone Fare Max Prices: {}", this.busCompanyZonesMaxPrices);
    }

    public Pair<TripStatus, BigDecimal> calculateTripFares(Integer busCompanyId, Stop stopFrom, Stop stopTo) {
        if (stopTo == stopFrom) {
            return Pair.of(TripStatus.CANCELLED, BigDecimal.ZERO);
        }
        if (stopTo != null) {
            var cost = busCompanyZonesPrices.computeIfAbsent(new BusCompanyZones(busCompanyId, stopFrom.getZone(), stopTo.getZone()),
                    _ -> BigDecimal.ZERO);
            return Pair.of(TripStatus.COMPLETED, cost);
        } else {
            var cost = busCompanyZonesMaxPrices.computeIfAbsent(new BusCompanyZone(busCompanyId, stopFrom.getZone()),
                    _ -> BigDecimal.ZERO);
            return Pair.of(TripStatus.INCOMPLETE, cost);
        }
    }

    private BusCompanyZones mapToBusZoneFare(ZoneFare zoneFare) {
        return new BusCompanyZones(zoneFare.getBusCompanyId(), zoneFare.getZoneFrom(), zoneFare.getZoneTo());
    }

    private void addBusCompanyZonePrice(BusCompanyZone busCompanyZone, BigDecimal price) {
        if (!busCompanyZonesMaxPrices.containsKey(busCompanyZone)
                || (busCompanyZonesMaxPrices.get(busCompanyZone).compareTo(price) < 0)) {
            busCompanyZonesMaxPrices.put(busCompanyZone, price);
        }
    }

}
