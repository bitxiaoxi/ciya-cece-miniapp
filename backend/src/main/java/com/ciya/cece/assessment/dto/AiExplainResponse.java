package com.ciya.cece.assessment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiExplainResponse {

    private String modelName;

    private String promptVersion;

    private String summaryText;

    private String recommendationText;

    private String decisionSummary;

    private String inputSnapshot;

    private String outputSnapshot;
}
