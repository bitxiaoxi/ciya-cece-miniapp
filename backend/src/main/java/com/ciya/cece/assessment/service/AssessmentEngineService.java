package com.ciya.cece.assessment.service;

import com.ciya.cece.assessment.dto.QuestionSelectionResult;
import com.ciya.cece.assessment.entity.AssessmentSession;
import com.ciya.cece.assessment.entity.AssessmentStageRule;

import java.math.BigDecimal;
import java.util.List;

public interface AssessmentEngineService {

    Integer totalQuestionCount(AssessmentStageRule rule);

    String resolvePhaseType(AssessmentStageRule rule, Integer answeredCount);

    QuestionSelectionResult selectNextQuestion(AssessmentSession session,
                                               AssessmentStageRule rule,
                                               List<Long> answeredItemIds,
                                               String lastQuestionType,
                                               String lastAbilityType);

    BigDecimal adjustDifficulty(BigDecimal difficultyBefore, String normalizedAnswerStatus, AssessmentStageRule rule);
}
