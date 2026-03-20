package com.ciya.cece.assessment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StartAssessmentRequest {

    @NotNull(message = "studentId不能为空")
    private Long studentId;

    @NotBlank(message = "selectedStageCode不能为空")
    private String selectedStageCode;

    @NotNull(message = "aiEnabled不能为空")
    private Boolean aiEnabled;
}
