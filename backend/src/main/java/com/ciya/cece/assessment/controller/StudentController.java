package com.ciya.cece.assessment.controller;

import com.ciya.cece.assessment.common.ApiResponse;
import com.ciya.cece.assessment.dto.CreateStudentRequest;
import com.ciya.cece.assessment.dto.UpdateStudentRequest;
import com.ciya.cece.assessment.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ApiResponse<?> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return ApiResponse.success(studentService.createStudent(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getStudent(@PathVariable("id") Long id) {
        return ApiResponse.success(studentService.getStudent(id));
    }

    @GetMapping
    public ApiResponse<?> listStudents() {
        return ApiResponse.success(studentService.listStudents());
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateStudent(@PathVariable("id") Long id,
                                        @Valid @RequestBody UpdateStudentRequest request) {
        return ApiResponse.success(studentService.updateStudent(id, request));
    }
}
