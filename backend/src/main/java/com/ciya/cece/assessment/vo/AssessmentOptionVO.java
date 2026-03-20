package com.ciya.cece.assessment.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssessmentOptionVO {

    private Long id;

    private String optionKey;

    private String optionText;

    private String optionAudioUrl;

    private String optionImageUrl;

    private Integer sortNo;
}
