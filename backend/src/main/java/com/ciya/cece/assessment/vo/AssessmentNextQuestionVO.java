package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssessmentNextQuestionVO {

    private Boolean shouldFinish;

    private AssessmentQuestionVO question;

    private ProgressVO progress;
}
