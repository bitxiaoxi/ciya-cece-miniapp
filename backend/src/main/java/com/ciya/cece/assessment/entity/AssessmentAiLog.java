package com.ciya.cece.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("assessment_ai_log")
public class AssessmentAiLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sessionId;

    private Long itemId;

    private String stepType;

    private String modelName;

    private String promptVersion;

    private String inputSnapshot;

    private String outputSnapshot;

    private String decisionSummary;

    private LocalDateTime createdAt;
}
