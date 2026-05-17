package hp.tasks.tapntravel.commons;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UtilitiesTest {

    @Test
    void parseDateStringToZonedDateTime() {
        var zonedDate = Utilities.parseDateStringToZonedDateTime("17-05-2026");
        assertEquals(zonedDate, ZonedDateTime.parse("17-05-2026T00:00:00Z"));
    }
}