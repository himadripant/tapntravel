package hp.tasks.tapntravel;

import hp.tasks.tapntravel.models.TapFromFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.RecordFieldSetMapper;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class TapReaderTest {

    private FlatFileItemReader<TapFromFile> itemReader;

    @BeforeEach
    void setUp() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(
                String.class, ZonedDateTime.class, source ->
                        LocalDateTime.parse(source.trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
                                .atOffset(ZoneOffset.UTC).toZonedDateTime()
        );

        RecordFieldSetMapper<TapFromFile> fieldSetMapper = new RecordFieldSetMapper<>(
                TapFromFile.class, conversionService);
        itemReader = new FlatFileItemReaderBuilder<TapFromFile>()
                .name("tapItemReader")
                .resource(new ClassPathResource("taps.csv"))
                .delimited()
                .names("id", "timestamp", "tapType", "stopId", "companyId", "busId", "pan")
                .linesToSkip(1)
                .fieldSetMapper(fieldSetMapper)
                .build();

        ExecutionContext executionContext = MetaDataInstanceFactory.createStepExecution().getExecutionContext();

        itemReader.open(executionContext);
    }

    @AfterEach
    void tearDown() {
        itemReader.close();
    }

    @Test
    void testReadValidCsvFile() throws Exception {
        TapFromFile firstProduct = itemReader.read();
        assertNotNull(firstProduct);

        TapFromFile secondProduct = itemReader.read();
        assertNotNull(secondProduct);

        TapFromFile thirdProduct = itemReader.read();
        assertNotNull(thirdProduct);
    }
}