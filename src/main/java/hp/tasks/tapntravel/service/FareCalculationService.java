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
    private final Map<BusCompanyZone, BigDecimal> busCompanyZonesMaxPrices = new HashMap<>();
    private Map<BusCompanyZones, BigDecimal> busCompanyZonesPrices;

    public FareCalculationService(ZoneFareRepository zoneFareRepository) {
        this.zoneFareRepository = zoneFareRepository;
    }

    @PostConstruct
    public void init() {
        this.busCompanyZonesPrices = zoneFareRepository.findAll()
                .stream()
                .collect(Collectors.toMap(this::mapToBusZoneFare, ZoneFare::getPrice));

        Map<BusCompanyZones, BigDecimal> busCompanyZonesPricesBackwards = this.busCompanyZonesPrices.entrySet()
                .stream()
                .filter(entry ->
                        this.checkIfBusZoneFareForBackTripDoesNotExists(busCompanyZonesPrices, entry.getKey()))
                .map(entry -> Pair.of(
                        new BusCompanyZones(entry.getKey().busCompanyId(),
                                entry.getKey().zoneTo(), entry.getKey().zoneFrom()), entry.getValue()))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        this.busCompanyZonesPrices.putAll(busCompanyZonesPricesBackwards);

        this.busCompanyZonesPrices
                .forEach((key, price) ->
                    addBusCompanyZoneMaxPrice(new BusCompanyZone(key.busCompanyId(), key.zoneFrom()), price));
        logger.info("Zone Fare Max Prices: {}", this.busCompanyZonesMaxPrices);
    }

    public Pair<TripStatus, BigDecimal> calculateTripFares(Integer busCompanyId, Stop stopFrom, Stop stopTo) {
        if (stopTo == null) {
            var cost = busCompanyZonesMaxPrices.computeIfAbsent(new BusCompanyZone(busCompanyId, stopFrom.getZone()),
                    _ -> BigDecimal.ZERO);
            return Pair.of(TripStatus.INCOMPLETE, cost);
        } else if (stopFrom.getId().equals(stopTo.getId())) {
            return Pair.of(TripStatus.CANCELLED, BigDecimal.ZERO);
        } else {
            var cost = busCompanyZonesPrices.computeIfAbsent(
                    new BusCompanyZones(busCompanyId, stopFrom.getZone(), stopTo.getZone()),
                    _ -> BigDecimal.ZERO
            );
            return Pair.of(TripStatus.COMPLETED, cost);
        }
    }

    private BusCompanyZones mapToBusZoneFare(ZoneFare zoneFare) {
        return new BusCompanyZones(zoneFare.getBusCompanyId(), zoneFare.getZoneFrom(), zoneFare.getZoneTo());
    }

    private boolean checkIfBusZoneFareForBackTripDoesNotExists(
            Map<BusCompanyZones, BigDecimal> busCompanyZonesPrices,
            BusCompanyZones busCompanyZones
    ) {
        BusCompanyZones busCompanyZonesReturnTrip = new BusCompanyZones(busCompanyZones.busCompanyId(),
                busCompanyZones.zoneTo(), busCompanyZones.zoneFrom());
        return !busCompanyZonesPrices.containsKey(busCompanyZonesReturnTrip);
    }

    private void addBusCompanyZoneMaxPrice(BusCompanyZone busCompanyZone, BigDecimal price) {
        if (!busCompanyZonesMaxPrices.containsKey(busCompanyZone)
                || (busCompanyZonesMaxPrices.get(busCompanyZone).compareTo(price) < 0)) {
            busCompanyZonesMaxPrices.put(busCompanyZone, price);
        }
    }

}
