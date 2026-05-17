package hp.tasks.tapntravel.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
//@Sql("classpath:data-init.sql")
@SpringBootTest
class FareCalculationServiceTest {

    @Autowired
    private FareCalculationService fareCalculationService;

    @Test
    @DisplayName("calculating the price between two zones")
    void calculateTripFares_betweenTwoZones() {
//        var zoneFare = fareCalculationService.calculateTripFares(1, 1, 1);
//        assertTrue(fareCalculationService.calculateTripFares(1, 1, 1).compareTo(BigDecimal.valueOf(2.50)) == 0);
    }
}