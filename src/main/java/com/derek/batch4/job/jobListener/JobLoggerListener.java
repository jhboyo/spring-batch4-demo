package com.derek.batch4.job.jobListener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

@Slf4j
public class JobLoggerListener implements org.springframework.batch.core.JobExecutionListener {

    private final static String BEFORE_MESSAGE = "## {} Job is Running...";
    private final static String AFTER_MESSAGE = "## {} Job is done... (Status: {})";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(BEFORE_MESSAGE, jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(AFTER_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            // email
            log.info("## Job is Failed");
        }
    }
}
