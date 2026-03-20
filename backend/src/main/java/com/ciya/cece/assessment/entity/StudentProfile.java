package com.ciya.cece.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_profile")
public class StudentProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentName;

    private String gradeCode;

    private Integer birthYear;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
