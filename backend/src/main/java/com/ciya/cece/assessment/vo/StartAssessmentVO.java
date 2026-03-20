package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StartAssessmentVO {

    private String sessionNo;

    private String selectedStageCode;

    private BigDecimal startDifficulty;

    private String ruleVersion;

    private String bankVersion;

    private Integer totalQuestionCount;

    private AssessmentQuestionVO firstQuestion;
}
