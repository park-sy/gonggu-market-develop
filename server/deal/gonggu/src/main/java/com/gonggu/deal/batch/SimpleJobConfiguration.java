package com.gonggu.deal.batch;

import com.gonggu.deal.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DealRepository dealRepository;

    @Bean
    public Job pushJob() {
        return jobBuilderFactory.get(LocalDateTime.now().toString())
                .start(simpleStep1())
                .build();
    }


    @Bean
    public Step simpleStep1() {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution, chunkContext) -> {
                    List<DealForExpires> ids = dealRepository.getDealIdByDate(LocalDate.now());
                    System.out.println(ids.toString());
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
