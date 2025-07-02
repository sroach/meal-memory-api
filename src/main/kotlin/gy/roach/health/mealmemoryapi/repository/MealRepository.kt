package gy.roach.health.mealmemoryapi.repository

import gy.roach.health.mealmemoryapi.model.Meal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MealRepository : JpaRepository<Meal, Long> {

    fun findByUserId(userId: String): List<Meal>

    fun findByTimestampBetween(start: LocalDateTime, end: LocalDateTime): List<Meal>

    fun findByUserIdAndTimestampBetween(
        userId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<Meal>
}