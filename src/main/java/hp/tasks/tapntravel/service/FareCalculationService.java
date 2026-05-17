package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.ZoneFare;
import hp.tasks.tapntravel.models.BusCompanyZone;
import hp.tasks.tapntravel.models.BusCompanyZones;
import hp.tasks.tapntravel.repositories.ZoneFareRepository;
import jakarta.annotation.PostConstruct;
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

    private Map<BusCompanyZone, BigDecimal> busCompanyZonesMaxPrices = new HashMap<>();

    public FareCalculationService(ZoneFareRepository zoneFareRepository) {
        this.zoneFareRepository = zoneFareRepository;
    }

    @PostConstruct
    void init() {
        this.busCompanyZonesPrices = zoneFareRepository.findAll()
                .stream()
                .collect(Collectors.toMap(this::mapToBusZoneFare, ZoneFare::getPrice));
        logger.debug("Zone Fare Prices: {}", this.busCompanyZonesPrices);

        this.busCompanyZonesPrices
                .forEach((key, price) -> {
                    addBusCompanyZonePrice(new BusCompanyZone(key.busCompanyId(), key.zoneFrom()), price);
                    addBusCompanyZonePrice(new BusCompanyZone(key.busCompanyId(), key.zoneTo()), price);
                });
        logger.debug("Zone Fare Max Prices: {}", this.busCompanyZonesMaxPrices);
    }

    public BigDecimal calculateTripFares(Integer busCompanyId, Integer zoneFrom, Integer zoneTo) {
        if (zoneTo != null) {
            return busCompanyZonesPrices.computeIfAbsent(new BusCompanyZones(busCompanyId, zoneFrom, zoneTo),
                    _ -> BigDecimal.ZERO);
        }
        else  {
            return busCompanyZonesMaxPrices.computeIfAbsent(new BusCompanyZone(busCompanyId, zoneFrom),
                    _ -> BigDecimal.ZERO);
        }
    }

    private BusCompanyZones mapToBusZoneFare(ZoneFare zoneFare) {
        return new BusCompanyZones(zoneFare.getBusCompanyId(), zoneFare.getZoneFrom(),  zoneFare.getZoneTo());
    }

    private void addBusCompanyZonePrice(BusCompanyZone busCompanyZone, BigDecimal price) {
        if (!busCompanyZonesMaxPrices.containsKey(busCompanyZone)
                || (busCompanyZonesMaxPrices.get(busCompanyZone).compareTo(price) < 0)) {
            busCompanyZonesMaxPrices.put(busCompanyZone, price);
        }
    }

}
