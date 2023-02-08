package com.gonggu.deal.batch;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize;
    @Value("${chunkSize:100}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Bean
    public Job pushJob() {
        return jobBuilderFactory.get(LocalDateTime.now().toString())
                .start(jpaItemReaderStep())
                .build();
    }

    @Bean
    public Step jpaItemReaderStep(){
        return stepBuilderFactory.get("temp")
                .<Deal, Deal>chunk(chunkSize)
                .reader(jpaCursorItemReader(LocalDate.now()))
                .writer(jpaCursorItemWriter())
                .build();
    }

    @Bean
    public JpaCursorItemReader<Deal> jpaCursorItemReader(LocalDate targetDate) {
        String target = "'" + targetDate + "'";
        return new JpaCursorItemReaderBuilder<Deal>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT d FROM Deal d " +
                        "where date_format(d.expireTime,'%Y-%m-%d') = "+ target +
                        "and d.deletion = false")
                .build();
    }

    private ItemWriter<Deal> jpaCursorItemWriter() {
        return list -> {
            for (Deal deal: list) {
                DealForExpires expires = new DealForExpires(deal.getId(), deal.getTitle());
                System.out.println(expires);
            }
        };
    }
}
