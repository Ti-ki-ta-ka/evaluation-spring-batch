package com.example.evaluationspringbatch.batch.configuration

import com.example.evaluationspringbatch.batch.listener.StepPerformanceListener
import com.example.evaluationspringbatch.batch.reader.QuerydslZeroOffsetItemReader
import com.example.evaluationspringbatch.domain.evaluation.Evaluation
import com.example.evaluationspringbatch.domain.evaluation.EvaluationRepository
import com.example.evaluationspringbatch.domain.evaluation.QEvaluation.evaluation
import com.example.evaluationspringbatch.domain.team.Team
import com.example.evaluationspringbatch.domain.team.TeamRepository
import com.example.evaluationspringbatch.exception.ModelNotFoundException
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class TeamEvaluationStepConfig(
    private val evaluationRepository: EvaluationRepository,
    private val teamRepository: TeamRepository,
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory,
) {

    @Bean
    fun teamEvaluationStep(
        performanceListener: StepPerformanceListener
    ): Step {
        return StepBuilder("teamEvaluationStep", jobRepository).chunk<Evaluation, Team>(200, transactionManager)
            .reader(evaluationItemReader(evaluationRepository)).processor(evaluationItemProcessor(teamRepository))
            .writer(teamItemWriter()).listener(performanceListener).build()
    }

    @Bean
    fun evaluationItemReader(evaluationRepository: EvaluationRepository): QuerydslZeroOffsetItemReader<Evaluation> {
        return QuerydslZeroOffsetItemReader(entityManagerFactory = entityManagerFactory,
            queryCreator = { queryFactory, lastId ->
                val query = evaluationRepository.findEvaluationsWithPagination(queryFactory)
                lastId?.let {
                    query.where(evaluation.id.gt(it))
                }
                query
            },
            idExtractor = { it.id as Long }).apply {
            setPageSize(200)
        }
    }


    @StepScope
    @Bean
    fun evaluationItemProcessor(teamRepository: TeamRepository): ItemProcessor<Evaluation, Team> {
        return ItemProcessor { evaluation ->
            val teamId = evaluation.evaluateeTeamId
            val team = teamRepository.findById(teamId).orElseThrow { ModelNotFoundException("Team", teamId) }

            team.mannerScore += evaluation.mannerScore
            team.tierScore += evaluation.skillScore
            team.attendanceScore += evaluation.attendanceScore

            team
        }
    }

    @StepScope
    @Bean
    fun teamItemWriter(): ItemWriter<Team> {
        return ItemWriter { teams ->
            teamRepository.saveAll(teams)
        }
    }
}