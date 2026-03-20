package com.ciya.cece.assessment.service;

import com.ciya.cece.assessment.dto.CreateStudentRequest;
import com.ciya.cece.assessment.dto.UpdateStudentRequest;
import com.ciya.cece.assessment.vo.StudentVO;

import java.util.List;

public interface StudentService {

    StudentVO createStudent(CreateStudentRequest request);

    StudentVO getStudent(Long id);

    List<StudentVO> listStudents();

    StudentVO updateStudent(Long id, UpdateStudentRequest request);
}
