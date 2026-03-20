package com.ciya.cece.assessment.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AssessmentHistoryVO {

    private String sessionNo;

    private String selectedStageCode;

    private String estimatedStageCode;

    private Integer vocabEstimateMid;

    private BigDecimal confidenceScore;

    private LocalDateTime finishedAt;
}
