package hp.tasks.tapntravel.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilitiesTest {

    @Test
    void parseDateStringToZonedDateTime() {
        var zonedDate = Utilities.parseDateStringToZonedDateTime("17-05-2026");
        assertEquals(zonedDate.getHour(), 0);
        assertEquals(zonedDate.getMinute(), 0);
        assertEquals(zonedDate.getSecond(), 0);
    }
}