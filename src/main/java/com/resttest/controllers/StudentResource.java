package com.resttest.controllers;

import com.resttest.StudentNotFoundException;
import com.resttest.dto.Student;
import com.resttest.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/v2/students")
public class StudentResource {

    @Autowired
    private StudentService studentService;

    @GetMapping(produces = { "application/hal+json" })
    public CollectionModel<Student> all() {
        List<Student> students = studentService.getStudents();

        for (Student student : students) {
            String id = student.getId() + "";
            Link selfLink = linkTo(StudentResource.class).slash(id).withSelfRel();
            student.add(selfLink);
        }

        Link link = linkTo(StudentResource.class).withSelfRel();
        CollectionModel<Student> result = new CollectionModel<>(students, link);
        return result;
    }

    @GetMapping("{id}")
    EntityModel<Student>  get(@PathVariable Long id) {
        Student student = studentService.getStudent(id);
        if (student == null) {
            throw new StudentNotFoundException(id);
        }
        EntityModel<Student> resource = EntityModel.of(student);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).all());
        resource.add(linkTo.withRel("students"));
        return resource;
    }

    @PostMapping
    public EntityModel<Student> create(@RequestBody Student student) {
        Student savedStudent = studentService.saveStudent(student);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedStudent.getId()).toUri();
        EntityModel<Student> resource = EntityModel.of(savedStudent);
        Link link = linkTo(StudentResource.class).slash(savedStudent.getId()).withSelfRel();
        resource.add(link);
        return resource;
    }

    @PutMapping("{id}")
    public Student replace(@RequestBody Student student, @PathVariable Long id) {
        return studentService.updateStudent(student);
    }

    @PatchMapping("{id}")
    public Student partialUpdate(@RequestBody Student student, @PathVariable Long id) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}



