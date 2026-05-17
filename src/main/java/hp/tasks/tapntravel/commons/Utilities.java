package hp.tasks.tapntravel.commons;

import hp.tasks.tapntravel.entities.Tap;
import hp.tasks.tapntravel.models.TapFromFile;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Utilities {

    public static ZonedDateTime parseStringToZonedDateTime(String string) {
        return LocalDateTime.parse(string.trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
                .atOffset(ZoneOffset.UTC).toZonedDateTime();
    }

    public static String validatePan(String string) throws IllegalArgumentException {
        if (StringUtils.isNotBlank(string)) {
            var stringTrimmed = string.trim();
            if (StringUtils.isNumeric(stringTrimmed) && stringTrimmed.length() == 16) {
                return  stringTrimmed;
            }
        }
        throw new IllegalArgumentException("Invalid pan string");
    }
}
