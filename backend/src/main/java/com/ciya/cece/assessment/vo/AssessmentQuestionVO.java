package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AssessmentQuestionVO {

    private Long itemId;

    private String itemCode;

    private String stageCode;

    private String questionType;

    private String abilityType;

    private String wordText;

    private String stemText;

    private String stemAudioUrl;

    private String stemImageUrl;

    private String phaseType;

    private Integer questionNo;

    private List<AssessmentOptionVO> options;
}
