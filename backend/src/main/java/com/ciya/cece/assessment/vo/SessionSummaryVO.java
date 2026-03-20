package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SessionSummaryVO {

    private String sessionNo;

    private Long studentId;

    private String selectedStageCode;

    private BigDecimal startDifficulty;

    private BigDecimal currentDifficulty;

    private String ruleVersion;

    private String bankVersion;

    private Boolean aiEnabled;

    private String status;

    private Integer answeredCount;

    private Integer correctCount;

    private Integer uncertainCount;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;
}
