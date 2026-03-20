package com.ciya.cece.assessment.service;

import com.ciya.cece.assessment.dto.AssessmentAnswerRecordDTO;
import com.ciya.cece.assessment.dto.ResultComputationDTO;
import com.ciya.cece.assessment.entity.AssessmentSession;
import com.ciya.cece.assessment.entity.AssessmentStageRule;
import com.ciya.cece.assessment.enums.AbilityTypeEnum;
import com.ciya.cece.assessment.enums.PhaseTypeEnum;
import com.ciya.cece.assessment.service.impl.AssessmentResultServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

class AssessmentResultServiceTest {

    private final AssessmentResultService assessmentResultService = new AssessmentResultServiceImpl(new ObjectMapper());

    @Test
    void shouldComputeResultAndBasisFromAnswerRecords() {
        AssessmentSession session = new AssessmentSession();
        session.setRuleVersion("RULE_V1");
        session.setBankVersion("v1_seed");
        session.setCurrentDifficulty(new BigDecimal("5.20"));

        AssessmentStageRule rule = new AssessmentStageRule();
        rule.setMinDifficulty(new BigDecimal("3.00"));
        rule.setMaxDifficulty(new BigDecimal("6.50"));
        rule.setRouteQuestionCount(2);
        rule.setCoreQuestionCount(4);
        rule.setAnchorQuestionCount(2);
        rule.setResultMappingJson("[{\"scoreMin\":0,\"scoreMax\":39,\"vocabMin\":300,\"vocabMax\":800,\"estimatedStage\":\"K3\"},{\"scoreMin\":40,\"scoreMax\":54,\"vocabMin\":800,\"vocabMax\":1500,\"estimatedStage\":\"P1_2\"},{\"scoreMin\":55,\"scoreMax\":69,\"vocabMin\":1500,\"vocabMax\":2600,\"estimatedStage\":\"P3_4\"},{\"scoreMin\":70,\"scoreMax\":84,\"vocabMin\":2600,\"vocabMax\":4000,\"estimatedStage\":\"P5_6\"},{\"scoreMin\":85,\"scoreMax\":100,\"vocabMin\":4000,\"vocabMax\":6000,\"estimatedStage\":\"J7_9\"}]");

        List<AssessmentAnswerRecordDTO> records = Arrays.asList(
                buildRecord(AbilityTypeEnum.READING.getCode(), "CORRECT", "ROUTE", 1),
                buildRecord(AbilityTypeEnum.READING.getCode(), "CORRECT", "ROUTE", 1),
                buildRecord(AbilityTypeEnum.LISTENING.getCode(), "WRONG", "CORE", 0),
                buildRecord(AbilityTypeEnum.CONTEXT.getCode(), "UNCERTAIN", "CORE", 0),
                buildRecord(AbilityTypeEnum.READING.getCode(), "CORRECT", "ANCHOR", 1),
                buildRecord(AbilityTypeEnum.LISTENING.getCode(), "CORRECT", "CORE", 1),
                buildRecord(AbilityTypeEnum.CONTEXT.getCode(), "CORRECT", "ANCHOR", 1),
                buildRecord(AbilityTypeEnum.CONTEXT.getCode(), "SKIP", "CORE", 0)
        );

        ResultComputationDTO result = assessmentResultService.computeResult(session, rule, records);

        Assertions.assertEquals("P5_6", result.getEstimatedStageCode());
        Assertions.assertEquals(Integer.valueOf(2600), result.getVocabEstimateMin());
        Assertions.assertEquals(Integer.valueOf(4000), result.getVocabEstimateMax());
        Assertions.assertTrue(result.getConfidenceScore().compareTo(BigDecimal.ZERO) > 0);
        Assertions.assertTrue(result.getBasis().containsKey("abilityBreakdown"));
        Assertions.assertEquals(3, result.getAbilityScores().size());
    }

    private AssessmentAnswerRecordDTO buildRecord(String abilityType, String answerStatus, String phaseType, Integer isCorrect) {
        AssessmentAnswerRecordDTO record = new AssessmentAnswerRecordDTO();
        record.setAbilityType(abilityType);
        record.setAnswerStatus(answerStatus);
        record.setPhaseType(PhaseTypeEnum.valueOf(phaseType).getCode());
        record.setIsCorrect(isCorrect);
        return record;
    }
}
