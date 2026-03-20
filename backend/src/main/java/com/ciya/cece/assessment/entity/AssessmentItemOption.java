package com.ciya.cece.assessment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("assessment_item_option")
public class AssessmentItemOption {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long itemId;

    private String optionKey;

    private String optionText;

    private String optionAudioUrl;

    private String optionImageUrl;

    private Integer isCorrect;

    private Integer sortNo;

    private LocalDateTime createdAt;
}
