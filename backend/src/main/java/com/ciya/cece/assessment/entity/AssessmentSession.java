package com.ciya.cece.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("assessment_session")
public class AssessmentSession {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sessionNo;

    private Long studentId;

    private String selectedStageCode;

    private BigDecimal startDifficulty;

    private BigDecimal currentDifficulty;

    private String ruleVersion;

    private String bankVersion;

    private Integer aiEnabled;

    private String status;

    private Integer answeredCount;

    private Integer correctCount;

    private Integer uncertainCount;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
