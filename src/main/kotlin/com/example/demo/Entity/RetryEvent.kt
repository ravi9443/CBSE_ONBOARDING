package com.example.demo.Entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "retry_events")
data class RetryEvent(

    @Id
    var retryId: String? = null,
    var studentRollNo: String? = null,
    var taskType: String? = null,
    var requestMetadata: String? = null,
    var responseMetadata: String? = null,
    var createdDate: LocalDateTime? = null,
    var lastRunDate: LocalDateTime? = null,
    var nextRetryTime: LocalDateTime? = null,
    var version: Int? = null,
    var status: String? = null
)