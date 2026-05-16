package hp.tasks.tapntravel.configs;

import hp.tasks.tapntravel.models.TapFromFile;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StepConfigurations {

    @Bean
    public Step tapInit(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager,
                        ItemReader<TapFromFile> tapReader) {
        return new StepBuilder(jobRepository)
                .<TapFromFile, TapFromFile>chunk(10).transactionManager(transactionManager)
                .reader(tapReader)
                .build();
    }
}
