package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SubmitAnswerVO {

    private Boolean isCorrect;

    private BigDecimal nextDifficulty;

    private Boolean shouldFinish;

    private ProgressVO progress;
}
