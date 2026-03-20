package com.ciya.cece.assessment.service.impl;

import com.ciya.cece.assessment.dto.QuestionSelectionResult;
import com.ciya.cece.assessment.entity.AssessmentItem;
import com.ciya.cece.assessment.entity.AssessmentSession;
import com.ciya.cece.assessment.entity.AssessmentStageRule;
import com.ciya.cece.assessment.enums.AnswerStatusEnum;
import com.ciya.cece.assessment.enums.PhaseTypeEnum;
import com.ciya.cece.assessment.mapper.AssessmentItemMapper;
import com.ciya.cece.assessment.service.AssessmentEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentEngineServiceImpl implements AssessmentEngineService {

    private static final BigDecimal UP_STEP = new BigDecimal("0.50");
    private static final BigDecimal DOWN_STEP = new BigDecimal("0.70");
    private static final BigDecimal WEAK_DOWN_STEP = new BigDecimal("0.30");
    private static final BigDecimal SKIP_DOWN_STEP = new BigDecimal("0.50");
    private static final List<BigDecimal> DIFFICULTY_RADII = java.util.Arrays.asList(
            new BigDecimal("0.30"),
            new BigDecimal("0.60"),
            new BigDecimal("1.00")
    );

    private final AssessmentItemMapper assessmentItemMapper;

    @Override
    public Integer totalQuestionCount(AssessmentStageRule rule) {
        return rule.getRouteQuestionCount() + rule.getCoreQuestionCount() + rule.getAnchorQuestionCount();
    }

    @Override
    public String resolvePhaseType(AssessmentStageRule rule, Integer answeredCount) {
        List<String> plan = buildPhasePlan(rule);
        if (answeredCount >= plan.size()) {
            return null;
        }
        return plan.get(answeredCount);
    }

    @Override
    public QuestionSelectionResult selectNextQuestion(AssessmentSession session,
                                                      AssessmentStageRule rule,
                                                      List<Long> answeredItemIds,
                                                      String lastQuestionType,
                                                      String lastAbilityType) {
        Integer answeredCount = session.getAnsweredCount() == null ? 0 : session.getAnsweredCount();
        Integer totalQuestionCount = totalQuestionCount(rule);
        if (answeredCount >= totalQuestionCount) {
            return null;
        }

        String phaseType = resolvePhaseType(rule, answeredCount);
        Integer isAnchor = PhaseTypeEnum.ANCHOR.getCode().equals(phaseType) ? 1 : 0;
        AssessmentItem selectedItem = null;
        for (BigDecimal radius : DIFFICULTY_RADII) {
            selectedItem = chooseCandidate(session, rule, answeredItemIds, lastQuestionType, lastAbilityType, isAnchor, radius);
            if (selectedItem != null) {
                break;
            }
        }
        if (selectedItem == null) {
            selectedItem = chooseCandidate(session, rule, answeredItemIds, lastQuestionType, lastAbilityType, isAnchor, null);
        }
        if (selectedItem == null) {
            return null;
        }
        return QuestionSelectionResult.builder()
                .item(selectedItem)
                .phaseType(phaseType)
                .questionNo(answeredCount + 1)
                .totalQuestionCount(totalQuestionCount)
                .build();
    }

    @Override
    public BigDecimal adjustDifficulty(BigDecimal difficultyBefore, String normalizedAnswerStatus, AssessmentStageRule rule) {
        BigDecimal target = difficultyBefore;
        AnswerStatusEnum answerStatusEnum = AnswerStatusEnum.fromCode(normalizedAnswerStatus);
        switch (answerStatusEnum) {
            case CORRECT:
                target = difficultyBefore.add(UP_STEP);
                break;
            case WRONG:
                target = difficultyBefore.subtract(DOWN_STEP);
                break;
            case UNCERTAIN:
                target = difficultyBefore.subtract(WEAK_DOWN_STEP);
                break;
            case SKIP:
                target = difficultyBefore.subtract(SKIP_DOWN_STEP);
                break;
            default:
                break;
        }
        if (target.compareTo(rule.getMinDifficulty()) < 0) {
            target = rule.getMinDifficulty();
        }
        if (target.compareTo(rule.getMaxDifficulty()) > 0) {
            target = rule.getMaxDifficulty();
        }
        return target.setScale(2, RoundingMode.HALF_UP);
    }

    private AssessmentItem chooseCandidate(AssessmentSession session,
                                           AssessmentStageRule rule,
                                           List<Long> answeredItemIds,
                                           String lastQuestionType,
                                           String lastAbilityType,
                                           Integer isAnchor,
                                           BigDecimal radius) {
        BigDecimal minDifficulty = radius == null
                ? rule.getMinDifficulty()
                : session.getCurrentDifficulty().subtract(radius).max(rule.getMinDifficulty());
        BigDecimal maxDifficulty = radius == null
                ? rule.getMaxDifficulty()
                : session.getCurrentDifficulty().add(radius).min(rule.getMaxDifficulty());
        List<AssessmentItem> candidates = assessmentItemMapper.selectCandidateItems(
                session.getSelectedStageCode(),
                session.getBankVersion(),
                isAnchor,
                minDifficulty,
                maxDifficulty,
                session.getCurrentDifficulty(),
                answeredItemIds == null || answeredItemIds.isEmpty() ? null : answeredItemIds,
                20
        );
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        return candidates.stream()
                .min(Comparator.comparing(item -> scoreCandidate(item, session.getCurrentDifficulty(), lastQuestionType, lastAbilityType)))
                .orElse(null);
    }

    private BigDecimal scoreCandidate(AssessmentItem item,
                                      BigDecimal targetDifficulty,
                                      String lastQuestionType,
                                      String lastAbilityType) {
        BigDecimal score = item.getDifficultyScore().subtract(targetDifficulty).abs().multiply(new BigDecimal("100"));
        if (item.getQuestionType().equals(lastQuestionType)) {
            score = score.add(new BigDecimal("15"));
        }
        if (item.getAbilityType().equals(lastAbilityType)) {
            score = score.add(new BigDecimal("10"));
        }
        return score;
    }

    private List<String> buildPhasePlan(AssessmentStageRule rule) {
        List<String> plan = new ArrayList<>();
        for (int i = 0; i < rule.getRouteQuestionCount(); i++) {
            plan.add(PhaseTypeEnum.ROUTE.getCode());
        }
        int gaps = rule.getAnchorQuestionCount() + 1;
        int baseCorePerGap = gaps == 0 ? 0 : rule.getCoreQuestionCount() / gaps;
        int extraCore = gaps == 0 ? 0 : rule.getCoreQuestionCount() % gaps;
        for (int gapIndex = 0; gapIndex < gaps; gapIndex++) {
            int coreCount = baseCorePerGap + (gapIndex < extraCore ? 1 : 0);
            for (int i = 0; i < coreCount; i++) {
                plan.add(PhaseTypeEnum.CORE.getCode());
            }
            if (gapIndex < rule.getAnchorQuestionCount()) {
                plan.add(PhaseTypeEnum.ANCHOR.getCode());
            }
        }
        return plan;
    }
}
