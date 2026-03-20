package com.ciya.cece.assessment.dto;

import com.ciya.cece.assessment.entity.AssessmentItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionSelectionResult {

    private AssessmentItem item;

    private String phaseType;

    private Integer questionNo;

    private Integer totalQuestionCount;
}
