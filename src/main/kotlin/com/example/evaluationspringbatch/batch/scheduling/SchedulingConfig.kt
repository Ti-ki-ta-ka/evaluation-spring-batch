package com.example.evaluationspringbatch.batch.scheduling

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class SchedulingConfig(
    private val jobLauncher: JobLauncher,
    private val teamEvaluationJob: Job
) {

    @Scheduled(cron = "0 0 1 * * ?")
    fun performTeamEvaluationJob() {
        val jobParameters = JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .toJobParameters()

        jobLauncher.run(teamEvaluationJob, jobParameters)
    }
}