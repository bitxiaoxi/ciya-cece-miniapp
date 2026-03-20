package com.ciya.cece.assessment.dto;

import lombok.Data;

@Data
public class ResultMappingRule {

    private Integer scoreMin;

    private Integer scoreMax;

    private Integer vocabMin;

    private Integer vocabMax;

    private String estimatedStage;
}
