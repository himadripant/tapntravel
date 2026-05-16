package hp.tasks.tapntravel;

import hp.tasks.tapntravel.configs.ItemConfigurations;
import hp.tasks.tapntravel.models.TapFromFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = ItemConfigurations.class)
class TapReaderTest {

    @Autowired
    private FlatFileItemReader<TapFromFile> itemReader;

    @BeforeEach
    void setUp() {
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
        assertAll(() -> {
            assertEquals(1, firstProduct.id());
            assertEquals("1", firstProduct.busId().trim());
            assertEquals(1, firstProduct.companyId());
        });

        TapFromFile secondProduct = itemReader.read();
        assertNotNull(secondProduct);

        TapFromFile thirdProduct = itemReader.read();
        assertNotNull(thirdProduct);
    }
}