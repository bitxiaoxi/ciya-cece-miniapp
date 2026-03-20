package com.ciya.cece.assessment.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateStudentRequest {

    @NotBlank(message = "studentName不能为空")
    @Size(max = 64, message = "studentName长度不能超过64")
    private String studentName;

    @NotBlank(message = "gradeCode不能为空")
    @Size(max = 32, message = "gradeCode长度不能超过32")
    private String gradeCode;

    @Min(value = 1900, message = "birthYear不合法")
    @Max(value = 2100, message = "birthYear不合法")
    private Integer birthYear;
}
