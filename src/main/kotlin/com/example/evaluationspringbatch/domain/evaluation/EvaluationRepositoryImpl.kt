package com.example.evaluationspringbatch.domain.evaluation

import com.example.evaluationspringbatch.domain.evaluation.QEvaluation.evaluation
import com.example.evaluationspringbatch.querydsl.QueryDslSupport
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class EvaluationRepositoryImpl : CustomEvaluationRepository, QueryDslSupport() {
    override fun findEvaluationsWithPagination(queryFactory: JPAQueryFactory): JPAQuery<Evaluation> {
        return queryFactory.selectFrom(evaluation)
            .where(evaluation.evaluationStatus.isTrue)
            .orderBy(evaluation.id.asc())
    }

}