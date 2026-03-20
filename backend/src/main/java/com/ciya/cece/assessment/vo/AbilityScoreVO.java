package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AbilityScoreVO {

    private String abilityType;

    private String abilityLabel;

    private BigDecimal score;

    private Integer correctCount;

    private Integer totalCount;
}
