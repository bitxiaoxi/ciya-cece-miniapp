package com.ciya.cece.assessment.dto;

import com.ciya.cece.assessment.vo.AbilityScoreVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class AiExplainRequest {

    private Long sessionId;

    private String sessionNo;

    private String selectedStageCode;

    private String estimatedStageCode;

    private BigDecimal overallScore;

    private BigDecimal confidenceScore;

    private BigDecimal readingScore;

    private BigDecimal listeningScore;

    private BigDecimal contextScore;

    private Integer vocabEstimateMin;

    private Integer vocabEstimateMax;

    private Integer vocabEstimateMid;

    private List<AbilityScoreVO> abilityScores;

    private Map<String, Object> basis;
}
