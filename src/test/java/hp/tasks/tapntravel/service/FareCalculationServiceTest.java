package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.Stop;
import hp.tasks.tapntravel.models.TripStatus;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
@Sql("classpath:data.sql")
@SpringBootTest
class FareCalculationServiceTest {

    @Autowired
    private FareCalculationService fareCalculationService;

    @BeforeEach
    void setUp() {
        fareCalculationService.init();
    }

    @Test
    @DisplayName("calculating the price between two zones")
    void calculateTripFares_betweenTwoZones() {
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(1), new Stop().setZone(1)).compareTo(Pair.of(TripStatus.COMPLETED, BigDecimal.valueOf(2.50))) == 0);
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(1), new Stop().setZone(2)).compareTo(Pair.of(TripStatus.COMPLETED, BigDecimal.valueOf(3.50))) == 0);
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(2), new Stop().setZone(1)).compareTo(Pair.of(TripStatus.COMPLETED, BigDecimal.valueOf(3.50))) == 0);
        assertTrue(fareCalculationService.calculateTripFares(2, new Stop().setZone(1), new Stop().setZone(3)).compareTo(Pair.of(TripStatus.COMPLETED, BigDecimal.valueOf(5))) == 0);
    }

    @Test
    @DisplayName("calculating the maximum price from a zone")
    void calculateTripFares_maximumPrice() {
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(1), null).compareTo(Pair.of(TripStatus.INCOMPLETE, BigDecimal.valueOf(5))) == 0);
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(2), null).compareTo(Pair.of(TripStatus.INCOMPLETE, BigDecimal.valueOf(3.75))) == 0);
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(3), null).compareTo(Pair.of(TripStatus.INCOMPLETE, BigDecimal.valueOf(5.5))) == 0);
        assertTrue(fareCalculationService.calculateTripFares(2, new Stop().setZone(1), null).compareTo(Pair.of(TripStatus.INCOMPLETE, BigDecimal.valueOf(5))) == 0);
    }
}