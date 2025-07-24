package com.example.demo.Repository
import com.example.demo.Entity.RetryEvent
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface RetryEventRepository : ReactiveMongoRepository<RetryEvent, String>{
    fun findByStatusAndNextRetryTimeBefore(status: String, now: LocalDateTime): Flux<RetryEvent>
}

