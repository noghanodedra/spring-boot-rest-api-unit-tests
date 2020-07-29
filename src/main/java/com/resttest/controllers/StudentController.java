package com.resttest.controllers;

import com.resttest.StudentNotFoundException;
import com.resttest.dto.Student;
import com.resttest.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public List<Student> all() {
        List<Student> students = studentService.getStudents();
        return students;
    }

    @GetMapping("{id}")
    Student get(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student == null) {
            throw new StudentNotFoundException(id);
        }
        return student;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Student student) {
        Student studentSaved =  studentService.saveStudent(student);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(studentSaved.getId())
                .toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        responseHeaders.set("MyResponseHeader", "MyValue");
        return new ResponseEntity<Student>(studentSaved, responseHeaders, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> replace(@RequestBody Student student, @PathVariable Long id) {
        Student studentUpdated = studentService.updateStudent(student);
        return new ResponseEntity<Student>(studentUpdated, null, HttpStatus.OK);
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> partialUpdate(@RequestBody Student student, @PathVariable Long id) {
        Student studentUpdated = studentService.updateStudent(student);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (id == 1) {
            throw new StudentNotFoundException(id);
        }
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}



