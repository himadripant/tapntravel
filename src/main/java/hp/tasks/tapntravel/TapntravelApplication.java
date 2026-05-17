package hp.tasks.tapntravel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.ZoneOffset;

import static hp.tasks.tapntravel.commons.Utilities.parseDateStringToZonedDateTime;

@SpringBootApplication
public class TapntravelApplication implements CommandLineRunner {

	static void main(String[] args) {
		SpringApplication.run(TapntravelApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var startOfDayAsString = args.length == 0 ? null : args[0];
		var startOfDay = StringUtils.isBlank(startOfDayAsString) ?
				LocalDate.now().atStartOfDay(ZoneOffset.UTC) : parseDateStringToZonedDateTime(args[0]);
	}
}
