package com.ali.learning.salesInfo.batch;

import com.ali.learning.salesInfo.batch.dto.SalesInfoDTO;
import com.ali.learning.salesInfo.batch.processor.SalesInfoItemProcessor;
import com.ali.learning.salesInfo.domain.SalesInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class SalesInfoJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final SalesInfoItemProcessor salesInfoItemProcessor;

    @Bean
    public Job readCsvJob() {
        return jobBuilderFactory.get("readCsvJob")
                .incrementer(new RunIdIncrementer())
                .start(readCsvStep())
                .build();
    }

    @Bean
    public Step readCsvStep() {
        return stepBuilderFactory.get("readCsvStep")
                .<SalesInfoDTO, Future<SalesInfo>>chunk(100)
                .reader(salesInfoFileReader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public FlatFileItemReader<SalesInfoDTO> salesInfoFileReader() {
        return new FlatFileItemReaderBuilder<SalesInfoDTO>()
                .resource(new ClassPathResource("/data/General-Store.csv"))
                .name("salesInfoFileReader")
                .delimited()
                .delimiter(",")
                .names(new String[]{"product", "seller", "sellerId", "price", "city", "category"})
                .linesToSkip(1)
                .targetType(SalesInfoDTO.class)
                .build();

    }

    @Bean
    public JpaItemWriter<SalesInfo> salesInfoItemWriter() {
        return new JpaItemWriterBuilder<SalesInfo>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        threadPoolTaskExecutor.setQueueCapacity(10);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix("Thread N -> : ");
        return threadPoolTaskExecutor;
    }

    @Bean
    public AsyncItemProcessor<SalesInfoDTO, SalesInfo> asyncItemProcessor() {
        AsyncItemProcessor<SalesInfoDTO, SalesInfo> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(salesInfoItemProcessor);
        asyncItemProcessor.setTaskExecutor(taskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter<SalesInfo> asyncItemWriter() {
        AsyncItemWriter<SalesInfo> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(salesInfoItemWriter());
        return asyncItemWriter;
    }
}
