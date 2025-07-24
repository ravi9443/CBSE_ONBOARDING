package com.example.demo.Scheduler

import com.example.demo.Entity.RetryEvent
import com.example.demo.Repository.RetryEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime


@Component // Marks this class as a Spring-managed component
class RetryScheduler(
    private val retryRepo: RetryEventRepository, // Injects repository for retry event DB operations
    private val objectMapper: ObjectMapper // Injects ObjectMapper (not used here, but available)
) {
    companion object {
        private const val MAX_COUNT = 3 // Maximum retry attempts allowed
        private const val retryIntervalMinutes = 1 // Interval between retries in minutes
    }

    // Scheduled method runs every 10 seconds to process retry events
    @Scheduled(fixedRate = 10000)
    fun processRetryEvents() {
        val now = LocalDateTime.now()
        // Find all retry events with status "OPEN" and nextRetryTime before now
        retryRepo.findByStatusAndNextRetryTimeBefore("OPEN", now)
            .doOnNext { event ->
                println("Retrying rollNo: ${event.studentRollNo}| Version: ${event.version}| NextRunTime: ${event.nextRetryTime}")
                if ((event.version ?: 0) >= MAX_COUNT) {
                    // If max retry count reached, mark as FAILED and update metadata
                    event.status = "FAILED"
                    event.requestMetadata = """{"error":"Max retry count reached"}"""
                    retryRepo.save(event).subscribe()
                    println("Max retry count reached for rollNo: ${event.studentRollNo}")
                } else {
                    // Otherwise, simulate external API call and update event for next retry
                    val newStatus = simulateExternalCbseApiResponse(event.studentRollNo)
                    event.lastRunDate = now
                    event.nextRetryTime = now.plusMinutes(retryIntervalMinutes.toLong())
                    event.version = (event.version ?: 0) + 1
                    event.status = newStatus
                    event.responseMetadata = """{"retryAttempt":${event.version},"status":"$newStatus"}"""
                    retryRepo.save(event).subscribe()
                    println("New status after retry: $newStatus")
                }
            }
            .doOnComplete { println("Running scheduled retry check at: $now") } // Log when processing is complete
            .subscribe() // Start the reactive stream
    }

    // Simulates an external API response based on roll number
    private fun simulateExternalCbseApiResponse(rollNo: String?): String {
        return when (rollNo) {
            "1" -> "CLOSED"
            "2" -> "FAILED"
            else -> "OPEN"
        }
    }
}