package com.ciya.cece.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("assessment_answer")
public class AssessmentAnswer {

    @TableId(type = IdType.AUTO)
    private Long id;

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
}
