package com.ciya.cece.assessment.service.impl;

import com.ciya.cece.assessment.dto.AiExplainRequest;
import com.ciya.cece.assessment.dto.AiExplainResponse;
import com.ciya.cece.assessment.service.AiExplainService;
import com.ciya.cece.assessment.vo.AbilityScoreVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FakeAiExplainService implements AiExplainService {

    private static final String MODEL_NAME = "fake-local-template";
    private static final String PROMPT_VERSION = "fake-result-explain-v1";

    private final ObjectMapper objectMapper;

    @Override
    public AiExplainResponse explain(AiExplainRequest request) {
        List<AbilityScoreVO> sorted = request.getAbilityScores()
                .stream()
                .sorted(Comparator.comparing(AbilityScoreVO::getScore))
                .collect(java.util.stream.Collectors.toList());
        AbilityScoreVO weakest = sorted.get(0);
        AbilityScoreVO strongest = sorted.get(sorted.size() - 1);

        String summary = String.format(
                "规则计算显示本次词汇量估计落在%d-%d之间，更接近%s。%s表现最稳，整体结果可信度为%.2f。",
                request.getVocabEstimateMin(),
                request.getVocabEstimateMax(),
                request.getEstimatedStageCode(),
                strongest.getAbilityLabel(),
                request.getConfidenceScore()
        );
        String recommendation = String.format(
                "建议优先补强%s，先围绕高频词义、同场景辨析和短回合重复练习做巩固，再复测观察区间是否继续上移。",
                weakest.getAbilityLabel()
        );

        Map<String, Object> output = new HashMap<>();
        output.put("summaryText", summary);
        output.put("recommendationText", recommendation);
        output.put("decisionSummary", "AI仅重写解释文本，未参与词汇量数值判定");

        return AiExplainResponse.builder()
                .modelName(MODEL_NAME)
                .promptVersion(PROMPT_VERSION)
                .summaryText(summary)
                .recommendationText(recommendation)
                .decisionSummary("AI仅重写解释文本，未参与词汇量数值判定")
                .inputSnapshot(toJson(request))
                .outputSnapshot(toJson(output))
                .build();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("序列化AI快照失败", exception);
        }
    }
}
