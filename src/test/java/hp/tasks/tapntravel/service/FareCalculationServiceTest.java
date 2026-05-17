package hp.tasks.tapntravel.service;

import hp.tasks.tapntravel.entities.Stop;
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
@Sql("classpath:data-init.sql")
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
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(1), new Stop().setZone(1)).compareTo(BigDecimal.valueOf(2.50)) == 0);
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(1), new Stop().setZone(2)).compareTo(BigDecimal.valueOf(3.50)) == 0);
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(2), new Stop().setZone(1)).compareTo(BigDecimal.valueOf(3.50)) == 0);
        assertTrue(fareCalculationService.calculateTripFares(2, new Stop().setZone(1), new Stop().setZone(3)).compareTo(BigDecimal.valueOf(5)) == 0);
    }

    @Test
    @DisplayName("calculating the maximum price from a zone")
    void calculateTripFares_maximumPrice() {
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(1), null).compareTo(BigDecimal.valueOf(5)) == 0);
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(2), null).compareTo(BigDecimal.valueOf(3.75)) == 0);
        assertTrue(fareCalculationService.calculateTripFares(1, new Stop().setZone(3), null).compareTo(BigDecimal.valueOf(5.5)) == 0);
        assertTrue(fareCalculationService.calculateTripFares(2, new Stop().setZone(1), null).compareTo(BigDecimal.valueOf(5)) == 0);
    }
}