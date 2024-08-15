package com.example.evaluationspringbatch.domain.evaluation

import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationRepository : JpaRepository<Evaluation, Long>, CustomEvaluationRepository