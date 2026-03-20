package com.ciya.cece.assessment.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SubmitAnswerRequest {

    @NotNull(message = "itemId不能为空")
    private Long itemId;

    private Long selectedOptionId;

    private String answerStatus;

    @Min(value = 0, message = "responseTimeMs不能小于0")
    @Max(value = 600000, message = "responseTimeMs不能超过600000")
    private Integer responseTimeMs;
}
