package com.gonggu.deal.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzService {

    private final Scheduler scheduler;

    @PostConstruct // DI 이후에 초기화 수행
    public void init(){
        try {
            System.out.println("-------------init----------------");
            Map paramsMap = new HashMap<>();
            paramsMap.put("date", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            //CronJob 등록
            addCronJob(DealScheduler.class,"test","testing",paramsMap,"0/5 * * * * ?");
        }catch (Exception e){
            log.error("log : {}",e);
        }
    }
    // 시간 간격을 주기로 실행
    public void addSimpleJob(Class job, String name, String desc, Map params, Integer seconds) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(job, name, desc, params);

        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }

        scheduler.scheduleJob(
                jobDetail,
                buildSimpleJobTrigger(seconds)
        );
    }
    // cron expression을 바탕으로 주기적으로 JOB 실행
    public void addCronJob(Class job, String name, String desc, Map params, String expression)
            throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(job, name, desc, params);

        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }

        scheduler.scheduleJob(
                jobDetail,
                buildCronJobTrigger(expression)
        );
    }
    // JobDetail 작성
    private JobDetail buildJobDetail(Class job, String name, String desc, Map params) {
        JobDataMap jobDataMap = new JobDataMap();
        if(params != null) jobDataMap.putAll(params);
        return JobBuilder
                .newJob(job)
                .withIdentity(name)
                .withDescription(desc)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildCronJobTrigger(String scheduleExp) {
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp))
                .build();
    }

    private Trigger buildSimpleJobTrigger(Integer seconds) {
        return TriggerBuilder.newTrigger()
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .repeatForever()
                        .withIntervalInSeconds(seconds))
                .build();
    }

}
