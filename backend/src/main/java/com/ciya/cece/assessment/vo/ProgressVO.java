package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgressVO {

    private Integer answeredCount;

    private Integer totalCount;

    private String currentPhase;

    private Integer correctCount;

    private Integer uncertainCount;
}
