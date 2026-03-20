package com.ciya.cece.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("assessment_stage_rule")
public class AssessmentStageRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String stageCode;

    private String stageName;

    private BigDecimal startDifficulty;

    private BigDecimal minDifficulty;

    private BigDecimal maxDifficulty;

    private Integer routeQuestionCount;

    private Integer coreQuestionCount;

    private Integer anchorQuestionCount;

    private BigDecimal stopConfidence;

    private String resultMappingJson;

    private String ruleVersion;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
