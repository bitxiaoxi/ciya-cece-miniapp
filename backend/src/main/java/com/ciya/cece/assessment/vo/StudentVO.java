package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StudentVO {

    private Long id;

    private String studentName;

    private String gradeCode;

    private Integer birthYear;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
