package com.example.demo.Controller

import com.example.demo.Entity.Student
import com.example.demo.Service.StudentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class StudentController (
    private val studentService: StudentService // Injects the StudentService dependency
) {

    // Endpoint to create a new student
    @PostMapping("/students")
    fun createStudent(@RequestBody student: Student): Mono<ResponseEntity<Student>> =
        studentService.addStudent(student)
            .map { ResponseEntity.ok(it) } // Returns 200 OK with the created student
            .onErrorResume { Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()) } // Handles errors

    // Endpoint to get all students
    @GetMapping("/students")
    fun getAllStudents(): Flux<Student> =
        studentService.getAllStudents() // Returns a Flux (reactive stream) of students

    // Endpoint to get a student by roll number
    @GetMapping("/students/{rollNo}")
    fun getStudentById(@PathVariable rollNo: String): Mono<ResponseEntity<Student>> =
        studentService.getStudentById(rollNo)
            .map { ResponseEntity.ok(it) } // Returns 200 OK if found
            .defaultIfEmpty(ResponseEntity.notFound().build()) // Returns 404 if not found

    // Endpoint to delete a student by roll number
    @DeleteMapping("/students/{rollNo}")
    fun deleteStudent(@PathVariable rollNo: String): Mono<ResponseEntity<Void>> =
        studentService.deleteStudent(rollNo)
            .map { ResponseEntity.noContent().build<Void>() } // Returns 204 No Content on success
            .onErrorResume { Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()) } // Handles errors

    // Endpoint to update a student by roll number
    @PutMapping("/students/{rollNo}")
    fun updateStudent(@RequestBody student: Student, @PathVariable rollNo: String): Mono<ResponseEntity<Student>> =
        studentService.updateStudent(student, rollNo)
            .map { ResponseEntity.ok(it) } // Returns 200 OK with the updated student
            .onErrorResume { Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()) } // Handles errors
}