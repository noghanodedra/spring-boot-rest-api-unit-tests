package com.resttest.services;

import com.resttest.dto.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        Student student = new Student(200l,"noghan", 0);
        students.add(student);
        student = new Student(201l,"noghan2",0);
        students.add(student);
        return students;
    }

    public Student getStudent(Long id) {
        if(id > 500) return null;
        Student student = new Student();
        student.setId(id);
        student.setName("Test");
        return student;
    }

    public Student saveStudent(Student student) {
        return student;
    }

    public Student updateStudent(Student student) {
        return student;
    }

    public String deleteStudent(Long id) {
        return "Student is deleted";
    }
}