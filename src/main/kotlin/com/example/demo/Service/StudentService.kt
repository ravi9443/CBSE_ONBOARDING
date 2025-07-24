package com.example.demo.Service

import com.example.demo.Entity.Student
import com.example.demo.KafkaProducer.StudentKafkaProducer
import com.example.demo.Repository.StudentRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class StudentService(
    private val kafkaProducer: StudentKafkaProducer, // Injects Kafka producer for sending events
    private val studentRepository: StudentRepository // Injects repository for DB operations
) {

    // Adds a new student and sends a Kafka event after saving
    fun addStudent(student: Student): Mono<Student> {
        return studentRepository.save(student)
            .doOnNext { kafkaProducer.sendStudentEvent(it) } // Side-effect: send event to Kafka
    }

    // Retrieves all students as a reactive stream
    fun getAllStudents(): Flux<Student> {
        return studentRepository.findAll()
    }

    // Retrieves a student by ID (roll number)
    fun getStudentById(rollNo: String): Mono<Student> {
        return studentRepository.findByRollNo(rollNo)
            .onErrorResume {
                it.printStackTrace()
                Mono.empty()
            }
    }

    fun deleteStudent(rollNo: String): Mono<Boolean> {
        return studentRepository.findByRollNo(rollNo)
            .flatMap { student ->
                studentRepository.deleteById(student.id).thenReturn(true)
            }
            .defaultIfEmpty(false)
    }

    fun updateStudent(student: Student, rollNo: String): Mono<Student> {
        return studentRepository.findByRollNo(rollNo)
            .flatMap { existing ->
                val updatedStudent = student.copy(id = existing.id, rollNo = rollNo)
                studentRepository.save(updatedStudent)
            }
            .switchIfEmpty(Mono.error(NoSuchElementException("Student not found")))
    }
}


//map transforms the item emitted by a Mono/Flux by applying a function to it.
// The function returns a plain value, and the result is wrapped back into a Mono/Flux.

//flatMap is used when the function returns another Mono/Flux.
// It "flattens" the nested publishers into a single stream.