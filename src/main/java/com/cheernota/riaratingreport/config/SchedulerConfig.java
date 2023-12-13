package com.cheernota.riaratingreport.config;

import com.cheernota.riaratingreport.service.IndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

@Slf4j
@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "ria-rating-report.scheduler.enable", matchIfMissing = true)
@RequiredArgsConstructor
public class SchedulerConfig implements TaskSchedulerCustomizer {

    private final IndexService indexService;

    @Scheduled(cron = "${ria-rating-report.scheduler.cron}")
    public void startIndexing() {
        log.info("Start indexing researches by scheduler...");
        indexService.indexResearches();
    }

    @Override
    public void customize(ThreadPoolTaskScheduler taskScheduler) {
        taskScheduler.setErrorHandler(new CustomErrorHandler());
    }

    private static class CustomErrorHandler implements ErrorHandler {
        @Override
        public void handleError(Throwable t) {
            log.error("Scheduled task threw an exception: {}", t.getMessage(), t);
        }
    }
}