package com.example.evaluationspringbatch.domain.evaluation

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory

interface CustomEvaluationRepository {
    fun findEvaluationsWithPagination(queryFactory: JPAQueryFactory): JPAQuery<Evaluation>
}