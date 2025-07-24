package com.example.demo.KafkaProducer

import com.example.demo.Entity.Student
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service // Marks this class as a Spring service component
class StudentKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>, // Injects KafkaTemplate for sending messages
    private val objectMapper: ObjectMapper // Injects ObjectMapper for JSON serialization
) {
    companion object {
        private const val TOPIC = "ONBOARDING" // Kafka topic name
    }

    // Sends a Student event to the Kafka topic
    fun sendStudentEvent(student: Student) {
        try {
            val studentJson = objectMapper.writeValueAsString(student) // Converts Student object to JSON string
            kafkaTemplate.send(TOPIC, student.rollNo, studentJson) // Sends the JSON to Kafka with rollNo as key
        } catch (e: Exception) {
            e.printStackTrace() // Logs any serialization or send errors
        }
    }
}