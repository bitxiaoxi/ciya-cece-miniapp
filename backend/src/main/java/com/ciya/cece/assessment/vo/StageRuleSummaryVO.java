package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StageRuleSummaryVO {

    private String stageCode;

    private String stageName;

    private BigDecimal startDifficulty;

    private Integer routeQuestionCount;

    private Integer coreQuestionCount;

    private Integer anchorQuestionCount;

    private String ruleVersion;
}
