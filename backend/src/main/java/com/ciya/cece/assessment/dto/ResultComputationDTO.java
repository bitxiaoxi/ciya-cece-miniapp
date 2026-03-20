package com.ciya.cece.assessment.dto;

import com.ciya.cece.assessment.vo.AbilityScoreVO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ResultComputationDTO {

    private String estimatedStageCode;

    private Integer vocabEstimateMin;

    private Integer vocabEstimateMax;

    private Integer vocabEstimateMid;

    private BigDecimal readingScore;

    private BigDecimal listeningScore;

    private BigDecimal contextScore;

    private BigDecimal confidenceScore;

    private BigDecimal overallScore;

    private String summaryText;

    private String recommendationText;

    private Map<String, Object> basis;

    private List<AbilityScoreVO> abilityScores;
}
