package hp.tasks.tapntravel;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.batch.autoconfigure.BatchProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class TapntravelApplication {

	static void main(String[] args) {
		SpringApplication.run(TapntravelApplication.class, args);
	}

	@Bean
	public Job sampleJob(JobRepository jobRepository, Step tapInit) {
		return new JobBuilder("sampleJob", jobRepository)
				.start(tapInit)
				.build();
	}

}
