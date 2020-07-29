package com.resttest.controllers;
//https://medium.com/@thankgodukachukwu/unit-and-integrated-testing-spring-boot-and-junit-5-99b9745b782a

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resttest.dto.Student;
import com.resttest.services.StudentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class StudentControllerIntegrationTest {

    private final static String baseURL = "/api/v1/students";

    @MockBean
    private StudentService studentService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testGetAll() throws Exception {

        //given

        Student student = new Student(1l, "noghan", 2);
        List<Student> students = new ArrayList<>();
        students.add(student);
        given(studentService.getStudents()).willReturn(students);

        // when + then

        RequestBuilder request = get(baseURL);
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(students.size())))
                .andExpect(content().json("[{'id':1,'name':'noghan'}]"));
    }

    @Test
    public void getOne() throws Exception {

        // given
        Student student = new Student(1l, "noghan1", 3);
        given(studentService.getStudent(student.getId())).willReturn(student);

        // get student

        this.mockMvc
                .perform(get(baseURL + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void get_Student_Not_Found() throws Exception {

        // given
        Student student = new Student(1000l, "noghan1", 3);
        given(studentService.getStudent(student.getId())).willReturn(student);

        // get student

        this.mockMvc
                .perform(get(baseURL + "/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void create() throws Exception {
        Student student = new Student(1l, "noghan2", 4);
        given(studentService.saveStudent(any(Student.class))).willAnswer((invocation) -> invocation.getArgument(0));
        mockMvc.perform(post(baseURL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(student)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("noghan2"));
    }

    @Test
    public void replace() throws Exception {
        Student student = new Student(5l, "noghan3", 5);

        given(studentService.getStudent(student.getId())).willReturn(student);
        given(studentService.updateStudent(any(Student.class))).willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc.perform(put(baseURL + "/{id}", student.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(student)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name", is(student.getName())));
    }

    @Test
    public void partialUpdate() throws Exception {
        Student student = new Student(6l, "noghan4", 6);

        given(studentService.getStudent(student.getId())).willReturn(student);
        given(studentService.updateStudent(any(Student.class))).willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc.perform(patch(baseURL + "/{id}", student.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapToJson(student)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDelete() throws Exception {
        String uri = baseURL + "/2";
        MvcResult mvcResult = mockMvc.perform(delete(uri)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(204, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "");
    }

    @Test
    public void testDelete404() throws Exception {
        String uri = baseURL + "/1";
        Long userId = 1L;
        given(studentService.getStudent(userId)).willReturn(null);
        this.mockMvc.perform(delete(baseURL + "/{id}", userId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}