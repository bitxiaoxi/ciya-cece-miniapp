package com.ciya.cece.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("assessment_item")
public class AssessmentItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String itemCode;

    private String stageCode;

    private String questionType;

    private String abilityType;

    private String wordText;

    private String stemText;

    private String stemAudioUrl;

    private String stemImageUrl;

    private BigDecimal difficultyScore;

    private BigDecimal discriminationScore;

    private Integer isAnchor;

    private String reviewStatus;

    private String bankVersion;

    private String contentSource;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
