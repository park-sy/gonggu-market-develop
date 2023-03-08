package com.gonggu.deal.batch;

import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class DealScheduler extends QuartzJobBean { //QuartzJob을 상속
    private String jobName;
    private final Job job;
    private final JobLauncher jobLauncher; //Batch Job의 시작과 실행을 관리
    //private JobLocator jobLocator; // Job에 대한 구현 계획 가져옴
    @Override
    protected void executeInternal(JobExecutionContext context){
        try {
            System.out.println("-------------QuartzJop----------------");
            //
            JobParameters jobParameters = new JobParametersBuilder()
                .addDate("time",new Date())
                .toJobParameters();
            jobLauncher.run(job,jobParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
