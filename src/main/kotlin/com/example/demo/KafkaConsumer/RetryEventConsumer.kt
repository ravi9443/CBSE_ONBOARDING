package com.example.demo.KafkaConsumer

import com.example.demo.Entity.RetryEvent
import com.example.demo.Entity.Student
import com.example.demo.Repository.RetryEventRepository
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@Service // Marks this class as a Spring service component
class RetryEventConsumer @Autowired constructor(
    private val objectMapper: ObjectMapper, // Injects ObjectMapper for JSON deserialization
    private val retryRepo: RetryEventRepository // Injects repository for saving RetryEvent
) {

    // Listens to messages from the "ONBOARDING" Kafka topic
    @KafkaListener(topics = ["ONBOARDING"], groupId = "student-group")
    fun consumeStudentEvent(message: String) {
        mono {
            try {
                // Deserialize the incoming message to a Student object
                val student = objectMapper.readValue(message, Student::class.java)
                // Simulate an external API response based on student roll number
                val status = simulateExternalCbseApiResponse(student)
                // Create a RetryEvent object with relevant metadata
                val retryEvent = RetryEvent(
                    retryId = UUID.randomUUID().toString(), // Generate unique retry ID
                    studentRollNo = student.rollNo,
                    taskType = "ONBOARDING",
                    requestMetadata = message, // Store original message
                    responseMetadata = """{"status":"$status"}""", // Store simulated response
                    createdDate = LocalDateTime.now(),
                    lastRunDate = LocalDateTime.now(),
                    nextRetryTime = LocalDateTime.now().plusMinutes(1), // Schedule next retry
                    version = 0,
                    status = status
                )
                // Save the RetryEvent to the database
                retryRepo.save(retryEvent).subscribe()
            } catch (e: Exception) {
                e.printStackTrace() // Log any errors during processing
            }
        }.subscribe() // It is used to start the execution of the reactive Mono created by mono
    }

    // Simulates an external API response based on student roll number
    private fun simulateExternalCbseApiResponse(student: Student): String {
        val rollNo = student.rollNo?.trim()
        return when (rollNo) {
            "1" -> "CLOSED"
            "2" -> "FAILED"
            else -> "OPEN"
        }
    }
}