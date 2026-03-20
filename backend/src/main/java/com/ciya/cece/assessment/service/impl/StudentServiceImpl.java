package com.ciya.cece.assessment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ciya.cece.assessment.common.ErrorCode;
import com.ciya.cece.assessment.dto.CreateStudentRequest;
import com.ciya.cece.assessment.dto.UpdateStudentRequest;
import com.ciya.cece.assessment.entity.StudentProfile;
import com.ciya.cece.assessment.exception.BusinessException;
import com.ciya.cece.assessment.mapper.StudentProfileMapper;
import com.ciya.cece.assessment.service.StudentService;
import com.ciya.cece.assessment.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentProfileMapper studentProfileMapper;

    @Override
    public StudentVO createStudent(CreateStudentRequest request) {
        LocalDateTime now = LocalDateTime.now();
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setStudentName(request.getStudentName());
        studentProfile.setGradeCode(request.getGradeCode());
        studentProfile.setBirthYear(request.getBirthYear());
        studentProfile.setCreatedAt(now);
        studentProfile.setUpdatedAt(now);
        studentProfileMapper.insert(studentProfile);
        return toVO(studentProfile);
    }

    @Override
    public StudentVO getStudent(Long id) {
        return toVO(getStudentEntity(id));
    }

    @Override
    public List<StudentVO> listStudents() {
        LambdaQueryWrapper<StudentProfile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(StudentProfile::getCreatedAt, StudentProfile::getId);
        return studentProfileMapper.selectList(queryWrapper)
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentVO updateStudent(Long id, UpdateStudentRequest request) {
        StudentProfile studentProfile = getStudentEntity(id);
        studentProfile.setStudentName(request.getStudentName());
        studentProfile.setGradeCode(request.getGradeCode());
        studentProfile.setBirthYear(request.getBirthYear());
        studentProfile.setUpdatedAt(LocalDateTime.now());
        studentProfileMapper.updateById(studentProfile);
        return toVO(studentProfile);
    }

    private StudentProfile getStudentEntity(Long id) {
        StudentProfile studentProfile = studentProfileMapper.selectById(id);
        if (studentProfile == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生档案不存在");
        }
        return studentProfile;
    }

    private StudentVO toVO(StudentProfile studentProfile) {
        return StudentVO.builder()
                .id(studentProfile.getId())
                .studentName(studentProfile.getStudentName())
                .gradeCode(studentProfile.getGradeCode())
                .birthYear(studentProfile.getBirthYear())
                .createdAt(studentProfile.getCreatedAt())
                .updatedAt(studentProfile.getUpdatedAt())
                .build();
    }
}
