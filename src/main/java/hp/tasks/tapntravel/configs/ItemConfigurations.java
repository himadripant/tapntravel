package hp.tasks.tapntravel.configs;

import hp.tasks.tapntravel.models.TapFromFile;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.RecordFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ItemConfigurations {

    @Bean
    public FlatFileItemReader<TapFromFile> tapReader() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(
                String.class, ZonedDateTime.class, source ->
                        LocalDateTime.parse(source.trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
                                .atOffset(ZoneOffset.UTC).toZonedDateTime()
        );

        RecordFieldSetMapper<TapFromFile> fieldSetMapper = new RecordFieldSetMapper<>(
                TapFromFile.class, conversionService);

        return new FlatFileItemReaderBuilder<TapFromFile>()
                .name("tapItemReader")
                .resource(new ClassPathResource("taps.csv"))
                .delimited()
                .names("timestamp", "tapType", "stopId", "companyId", "busId", "pan")
                .linesToSkip(1)
                .fieldSetMapper(fieldSetMapper)
                .build();
    }

}
