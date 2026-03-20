package com.ciya.cece.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("assessment_result")
public class AssessmentResult {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sessionId;

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

    private String basisJson;

    private LocalDateTime createdAt;
}
