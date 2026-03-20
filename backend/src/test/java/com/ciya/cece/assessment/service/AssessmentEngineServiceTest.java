package com.ciya.cece.assessment.service;

import com.ciya.cece.assessment.entity.AssessmentStageRule;
import com.ciya.cece.assessment.mapper.AssessmentItemMapper;
import com.ciya.cece.assessment.service.impl.AssessmentEngineServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;

class AssessmentEngineServiceTest {

    private final AssessmentEngineService assessmentEngineService = new AssessmentEngineServiceImpl(mock(AssessmentItemMapper.class));

    @Test
    void shouldResolvePhasePlanWithAnchorsInExpectedOrder() {
        AssessmentStageRule rule = buildRule();
        Assertions.assertEquals("ROUTE", assessmentEngineService.resolvePhaseType(rule, 0));
        Assertions.assertEquals("ROUTE", assessmentEngineService.resolvePhaseType(rule, 1));
        Assertions.assertEquals("CORE", assessmentEngineService.resolvePhaseType(rule, 2));
        Assertions.assertEquals("CORE", assessmentEngineService.resolvePhaseType(rule, 3));
        Assertions.assertEquals("ANCHOR", assessmentEngineService.resolvePhaseType(rule, 4));
        Assertions.assertEquals("CORE", assessmentEngineService.resolvePhaseType(rule, 5));
        Assertions.assertEquals("ANCHOR", assessmentEngineService.resolvePhaseType(rule, 6));
        Assertions.assertEquals("CORE", assessmentEngineService.resolvePhaseType(rule, 7));
        Assertions.assertNull(assessmentEngineService.resolvePhaseType(rule, 8));
    }

    @Test
    void shouldAdjustDifficultyWithinRuleBoundary() {
        AssessmentStageRule rule = buildRule();
        Assertions.assertEquals(new BigDecimal("2.00"), assessmentEngineService.adjustDifficulty(new BigDecimal("1.50"), "CORRECT", rule));
        Assertions.assertEquals(new BigDecimal("1.00"), assessmentEngineService.adjustDifficulty(new BigDecimal("1.20"), "WRONG", rule));
        Assertions.assertEquals(new BigDecimal("1.00"), assessmentEngineService.adjustDifficulty(new BigDecimal("1.10"), "SKIP", rule));
        Assertions.assertEquals(new BigDecimal("3.50"), assessmentEngineService.adjustDifficulty(new BigDecimal("3.40"), "CORRECT", rule));
    }

    private AssessmentStageRule buildRule() {
        AssessmentStageRule rule = new AssessmentStageRule();
        rule.setRouteQuestionCount(2);
        rule.setCoreQuestionCount(4);
        rule.setAnchorQuestionCount(2);
        rule.setMinDifficulty(new BigDecimal("1.00"));
        rule.setMaxDifficulty(new BigDecimal("3.50"));
        return rule;
    }
}
