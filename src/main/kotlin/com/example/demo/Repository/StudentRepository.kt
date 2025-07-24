package com.example.demo.Repository
import com.example.demo.Entity.Student
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.util.*


interface StudentRepository : ReactiveMongoRepository<Student, String> {
    fun findByRollNo(rollNo: String): Mono<Student>
}