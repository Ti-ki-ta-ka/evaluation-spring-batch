package com.example.evaluationspringbatch.domain.team

import org.springframework.data.jpa.repository.JpaRepository

interface TeamRepository : JpaRepository<Team, Long> {
    fun findAllByOrderByTierScoreDesc(): List<Team>
}