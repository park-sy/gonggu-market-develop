package com.gonggu.deal.batch;

import com.gonggu.deal.domain.Deal;
import com.gonggu.deal.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PushJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final KafkaProducer kafkaProducer;

    private int chunkSize;
    @Value("${chunkSize:100}")
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Bean
    public Job pushJob() {
        return jobBuilderFactory.get(LocalDateTime.now().toString())
                .start(jpaItemReaderStep())
                .next(decider()) // decider를 통해 자정이라면 step 2 수행
                    .on("MIDNIGHT")
                    .to(jpaItemReaderStep2())
                    .on("*")
                    .end()
                .end()
                .build();
    }

    @Bean
    public Step jpaItemReaderStep(){
        return stepBuilderFactory.get("imminentDeal")
                .<Deal, Deal>chunk(chunkSize)
                .reader(jpaCursorItemReader(false)) // 첫 스텝은 모두 false를 넘겨줌
                .writer(jpaCursorItemWriter(false))
                .build();
    }

    @Bean
    public Step jpaItemReaderStep2(){
        return stepBuilderFactory.get("closedDeal")
                .<Deal, Deal>chunk(chunkSize)
                .reader(jpaCursorItemReader(true))  // 자정에는 두번째 step에서 true를 념겨줌
                .writer(jpaCursorItemWriter(true))
                .build();
    }

    @Bean
    public JpaCursorItemReader<Deal> jpaCursorItemReader(boolean isMidnight) {
        String target;
        if(isMidnight) target = "'" + LocalDate.now().minusDays(1) + "'"; // true라면 전날 게시글을 가져옴
        else target = "'" + LocalDate.now() + "'";                                    // false라면 오늘 게시글을 가져옴
        return new JpaCursorItemReaderBuilder<Deal>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT d FROM Deal d " +
                        "where date_format(d.expireTime,'%Y-%m-%d') = "+ target +
                        "and d.deletion = false")
                .build();
    }

    private ItemWriter<Deal> jpaCursorItemWriter(boolean isClosed) {
        final int time = isClosed ? -1 : LocalDateTime.now().getHour(); // true라면 -1을 넘겨주고 false라면 시간을 넘겨줌
        return list -> {
            for (Deal deal: list) {
                kafkaProducer.sendDealMemberAndTimeToPush("closeDeal",deal,time);   // deal에 대한 정보를 Kafka 로직에 넘겨줌
                System.out.println(time + " " +deal.getId()+ " "+ deal.getExpireTime());
            }
        };
    }
    @Bean
    public JobExecutionDecider decider() {
        return new midnightDecider();
    }
    public static class midnightDecider implements JobExecutionDecider {
        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            if(LocalDateTime.now().getHour() == 0) {
                return new FlowExecutionStatus("MIDNIGHT"); //현재 시간이 0이라면 midnight 상태
            } else {
                return new FlowExecutionStatus("COMMON");  // 그 외에는 보통 상태
            }
        }
    }
}
