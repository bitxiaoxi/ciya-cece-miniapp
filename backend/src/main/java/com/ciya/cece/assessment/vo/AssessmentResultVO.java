package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AssessmentResultVO {

    private SessionSummaryVO session;

    private String estimatedStageCode;

    private Integer vocabEstimateMin;

    private Integer vocabEstimateMax;

    private Integer vocabEstimateMid;

    private BigDecimal readingScore;

    private BigDecimal listeningScore;

    private BigDecimal contextScore;

    private BigDecimal confidenceScore;

    private String summaryText;

    private String recommendationText;

    private List<AbilityScoreVO> abilityScores;

    private Map<String, Object> basis;
}
