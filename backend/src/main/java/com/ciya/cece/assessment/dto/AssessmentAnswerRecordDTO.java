package com.ciya.cece.assessment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AssessmentAnswerRecordDTO {

    private Long answerId;

    private Long sessionId;

    private Long itemId;

    private Integer questionNo;

    private String phaseType;

    private Long selectedOptionId;

    private String answerStatus;

    private Integer isCorrect;

    private Integer responseTimeMs;

    private BigDecimal difficultyBefore;

    private BigDecimal difficultyAfter;

    private LocalDateTime createdAt;

    private String questionType;

    private String abilityType;

    private String wordText;

    private BigDecimal itemDifficultyScore;

    private Integer itemIsAnchor;
}
