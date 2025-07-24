package com.example.demo.Entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "students")
data class Student(
    @Id
    val id: String? = null,
    val name: String? = null,
    val rollNo: String? = null,
    val schoolName: String? = null
)