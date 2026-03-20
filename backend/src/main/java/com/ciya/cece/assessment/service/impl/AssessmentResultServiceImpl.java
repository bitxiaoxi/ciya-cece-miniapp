package com.ciya.cece.assessment.service.impl;

import com.ciya.cece.assessment.dto.AssessmentAnswerRecordDTO;
import com.ciya.cece.assessment.dto.ResultComputationDTO;
import com.ciya.cece.assessment.dto.ResultMappingRule;
import com.ciya.cece.assessment.entity.AssessmentSession;
import com.ciya.cece.assessment.entity.AssessmentStageRule;
import com.ciya.cece.assessment.enums.AbilityTypeEnum;
import com.ciya.cece.assessment.enums.AnswerStatusEnum;
import com.ciya.cece.assessment.enums.PhaseTypeEnum;
import com.ciya.cece.assessment.enums.StageCodeEnum;
import com.ciya.cece.assessment.service.AssessmentResultService;
import com.ciya.cece.assessment.vo.AbilityScoreVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentResultServiceImpl implements AssessmentResultService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final ObjectMapper objectMapper;

    @Override
    public ResultComputationDTO computeResult(AssessmentSession session,
                                              AssessmentStageRule rule,
                                              List<AssessmentAnswerRecordDTO> answerRecords) {
        List<AssessmentAnswerRecordDTO> safeRecords = answerRecords == null ? new ArrayList<>() : answerRecords;
        int totalPlanned = rule.getRouteQuestionCount() + rule.getCoreQuestionCount() + rule.getAnchorQuestionCount();
        int totalAnswered = safeRecords.size();
        int correctCount = (int) safeRecords.stream().filter(record -> Integer.valueOf(1).equals(record.getIsCorrect())).count();
        int uncertainCount = (int) safeRecords.stream().filter(record -> AnswerStatusEnum.UNCERTAIN.getCode().equals(record.getAnswerStatus())).count();
        int skipCount = (int) safeRecords.stream().filter(record -> AnswerStatusEnum.SKIP.getCode().equals(record.getAnswerStatus())).count();

        List<AbilityScoreVO> abilityScores = buildAbilityScores(safeRecords);
        BigDecimal readingScore = getAbilityScore(abilityScores, AbilityTypeEnum.READING.getCode());
        BigDecimal listeningScore = getAbilityScore(abilityScores, AbilityTypeEnum.LISTENING.getCode());
        BigDecimal contextScore = getAbilityScore(abilityScores, AbilityTypeEnum.CONTEXT.getCode());

        BigDecimal abilityRate = averageWeightedRate(safeRecords);
        BigDecimal finalDifficultyRate = normalizeDifficulty(session.getCurrentDifficulty(), rule.getMinDifficulty(), rule.getMaxDifficulty());
        List<AssessmentAnswerRecordDTO> anchorRecords = safeRecords.stream()
                .filter(record -> PhaseTypeEnum.ANCHOR.getCode().equals(record.getPhaseType()))
                .collect(Collectors.toList());
        BigDecimal anchorRate = anchorRecords.isEmpty() ? abilityRate : averageWeightedRate(anchorRecords);
        BigDecimal overallScore = abilityRate.multiply(new BigDecimal("0.60"))
                .add(finalDifficultyRate.multiply(new BigDecimal("0.25")))
                .add(anchorRate.multiply(new BigDecimal("0.15")))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal coverageFactor = totalPlanned == 0
                ? BigDecimal.ZERO
                : divide(BigDecimal.valueOf(totalAnswered), BigDecimal.valueOf(totalPlanned));
        BigDecimal abilityBalanceFactor = computeAbilityBalanceFactor(safeRecords);
        BigDecimal anchorStabilityFactor = anchorRecords.isEmpty()
                ? new BigDecimal("0.50")
                : averageWeightedFraction(anchorRecords);
        BigDecimal certaintyPenalty = totalAnswered == 0
                ? BigDecimal.ONE
                : divide(BigDecimal.valueOf(uncertainCount).multiply(new BigDecimal("0.50")).add(BigDecimal.valueOf(skipCount)),
                BigDecimal.valueOf(totalAnswered));
        BigDecimal certaintyFactor = BigDecimal.ONE.subtract(certaintyPenalty).max(BigDecimal.ZERO);
        BigDecimal confidenceScore = coverageFactor.multiply(new BigDecimal("40"))
                .add(abilityBalanceFactor.multiply(new BigDecimal("20")))
                .add(anchorStabilityFactor.multiply(new BigDecimal("25")))
                .add(certaintyFactor.multiply(new BigDecimal("15")))
                .setScale(2, RoundingMode.HALF_UP);

        ResultMappingRule mappingRule = resolveMappingRule(rule.getResultMappingJson(), overallScore);
        String summaryText = buildSummary(mappingRule, abilityScores, confidenceScore);
        String recommendationText = buildRecommendation(abilityScores);

        Map<String, Object> abilityBreakdown = new LinkedHashMap<>();
        for (AbilityScoreVO abilityScoreVO : abilityScores) {
            Map<String, Object> ability = new LinkedHashMap<>();
            ability.put("label", abilityScoreVO.getAbilityLabel());
            ability.put("score", abilityScoreVO.getScore());
            ability.put("correctCount", abilityScoreVO.getCorrectCount());
            ability.put("totalCount", abilityScoreVO.getTotalCount());
            abilityBreakdown.put(abilityScoreVO.getAbilityType(), ability);
        }

        Map<String, Object> anchorPerformance = new LinkedHashMap<>();
        anchorPerformance.put("totalCount", anchorRecords.size());
        anchorPerformance.put("weightedRate", anchorRate);
        anchorPerformance.put("correctCount", anchorRecords.stream().filter(record -> Integer.valueOf(1).equals(record.getIsCorrect())).count());

        Map<String, Object> finalDifficultyBand = new LinkedHashMap<>();
        finalDifficultyBand.put("currentDifficulty", session.getCurrentDifficulty());
        finalDifficultyBand.put("minDifficulty", rule.getMinDifficulty());
        finalDifficultyBand.put("maxDifficulty", rule.getMaxDifficulty());
        finalDifficultyBand.put("normalizedRate", finalDifficultyRate);

        Map<String, Object> confidenceFactors = new LinkedHashMap<>();
        confidenceFactors.put("coverage", coverageFactor.setScale(4, RoundingMode.HALF_UP));
        confidenceFactors.put("abilityBalance", abilityBalanceFactor.setScale(4, RoundingMode.HALF_UP));
        confidenceFactors.put("anchorStability", anchorStabilityFactor.setScale(4, RoundingMode.HALF_UP));
        confidenceFactors.put("certaintyFactor", certaintyFactor.setScale(4, RoundingMode.HALF_UP));

        Map<String, Object> basis = new LinkedHashMap<>();
        basis.put("ruleVersion", session.getRuleVersion());
        basis.put("bankVersion", session.getBankVersion());
        basis.put("totalAnswered", totalAnswered);
        basis.put("correctRate", totalAnswered == 0 ? BigDecimal.ZERO : percentage(BigDecimal.valueOf(correctCount), BigDecimal.valueOf(totalAnswered)));
        basis.put("uncertainCount", uncertainCount);
        basis.put("skipCount", skipCount);
        basis.put("anchorPerformance", anchorPerformance);
        basis.put("abilityBreakdown", abilityBreakdown);
        basis.put("finalDifficultyBand", finalDifficultyBand);
        basis.put("overallScore", overallScore);
        basis.put("confidenceFactors", confidenceFactors);

        return ResultComputationDTO.builder()
                .estimatedStageCode(mappingRule.getEstimatedStage())
                .vocabEstimateMin(mappingRule.getVocabMin())
                .vocabEstimateMax(mappingRule.getVocabMax())
                .vocabEstimateMid((mappingRule.getVocabMin() + mappingRule.getVocabMax()) / 2)
                .readingScore(readingScore)
                .listeningScore(listeningScore)
                .contextScore(contextScore)
                .confidenceScore(confidenceScore)
                .overallScore(overallScore)
                .summaryText(summaryText)
                .recommendationText(recommendationText)
                .basis(basis)
                .abilityScores(abilityScores)
                .build();
    }

    private List<AbilityScoreVO> buildAbilityScores(List<AssessmentAnswerRecordDTO> records) {
        List<AbilityScoreVO> result = new ArrayList<>();
        for (AbilityTypeEnum abilityTypeEnum : AbilityTypeEnum.values()) {
            List<AssessmentAnswerRecordDTO> abilityRecords = records.stream()
                    .filter(record -> abilityTypeEnum.getCode().equals(record.getAbilityType()))
                    .collect(Collectors.toList());
            int correctCount = (int) abilityRecords.stream().filter(record -> Integer.valueOf(1).equals(record.getIsCorrect())).count();
            int totalCount = abilityRecords.size();
            BigDecimal score = totalCount == 0
                    ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                    : averageWeightedRate(abilityRecords);
            result.add(AbilityScoreVO.builder()
                    .abilityType(abilityTypeEnum.getCode())
                    .abilityLabel(abilityTypeEnum.getLabel())
                    .score(score)
                    .correctCount(correctCount)
                    .totalCount(totalCount)
                    .build());
        }
        return result;
    }

    private BigDecimal getAbilityScore(List<AbilityScoreVO> abilityScores, String abilityType) {
        return abilityScores.stream()
                .filter(item -> item.getAbilityType().equals(abilityType))
                .findFirst()
                .map(AbilityScoreVO::getScore)
                .orElse(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal averageWeightedRate(List<AssessmentAnswerRecordDTO> records) {
        return averageWeightedFraction(records).multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal averageWeightedFraction(List<AssessmentAnswerRecordDTO> records) {
        if (records == null || records.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = records.stream()
                .map(record -> AnswerStatusEnum.fromCode(record.getAnswerStatus()).getWeight())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return divide(total, BigDecimal.valueOf(records.size()));
    }

    private BigDecimal normalizeDifficulty(BigDecimal currentDifficulty, BigDecimal minDifficulty, BigDecimal maxDifficulty) {
        BigDecimal span = maxDifficulty.subtract(minDifficulty);
        if (span.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal ratio = divide(currentDifficulty.subtract(minDifficulty), span);
        ratio = ratio.max(BigDecimal.ZERO).min(BigDecimal.ONE);
        return ratio.multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal computeAbilityBalanceFactor(List<AssessmentAnswerRecordDTO> records) {
        Map<String, Integer> counter = new LinkedHashMap<>();
        counter.put(AbilityTypeEnum.READING.getCode(), 0);
        counter.put(AbilityTypeEnum.LISTENING.getCode(), 0);
        counter.put(AbilityTypeEnum.CONTEXT.getCode(), 0);
        for (AssessmentAnswerRecordDTO record : records) {
            counter.computeIfPresent(record.getAbilityType(), (key, value) -> value + 1);
        }
        long nonZeroCount = counter.values().stream().filter(count -> count > 0).count();
        int maxCount = counter.values().stream().max(Comparator.naturalOrder()).orElse(0);
        int minCount = counter.values().stream().min(Comparator.naturalOrder()).orElse(0);
        BigDecimal coveragePart = divide(BigDecimal.valueOf(nonZeroCount), new BigDecimal("3"));
        BigDecimal distributionPart = maxCount == 0
                ? BigDecimal.ZERO
                : divide(BigDecimal.valueOf(minCount), BigDecimal.valueOf(maxCount));
        return coveragePart.add(distributionPart).divide(new BigDecimal("2"), 4, RoundingMode.HALF_UP);
    }

    private ResultMappingRule resolveMappingRule(String mappingJson, BigDecimal overallScore) {
        try {
            List<ResultMappingRule> rules = objectMapper.readValue(mappingJson, new TypeReference<List<ResultMappingRule>>() {
            });
            ResultMappingRule matched = rules.stream()
                    .filter(rule -> overallScore.compareTo(BigDecimal.valueOf(rule.getScoreMin())) >= 0
                            && overallScore.compareTo(BigDecimal.valueOf(rule.getScoreMax())) <= 0)
                    .findFirst()
                    .orElse(null);
            if (matched != null) {
                return matched;
            }
            return rules.stream()
                    .sorted(Comparator.comparing(ResultMappingRule::getScoreMin))
                    .filter(rule -> overallScore.compareTo(BigDecimal.valueOf(rule.getScoreMin())) < 0)
                    .findFirst()
                    .orElse(rules.get(rules.size() - 1));
        } catch (Exception exception) {
            throw new IllegalStateException("解析result_mapping_json失败", exception);
        }
    }

    private String buildSummary(ResultMappingRule mappingRule, List<AbilityScoreVO> abilityScores, BigDecimal confidenceScore) {
        List<AbilityScoreVO> sorted = abilityScores.stream()
                .sorted(Comparator.comparing(AbilityScoreVO::getScore))
                .collect(Collectors.toList());
        AbilityScoreVO weakest = sorted.get(0);
        AbilityScoreVO strongest = sorted.get(sorted.size() - 1);
        return String.format(
                "本次测评估计接受性词汇量区间为%d-%d，当前更接近%s。%s表现最稳，%s相对薄弱，结果可信度为%.2f。",
                mappingRule.getVocabMin(),
                mappingRule.getVocabMax(),
                StageCodeEnum.fromCode(mappingRule.getEstimatedStage()).getLabel(),
                strongest.getAbilityLabel(),
                weakest.getAbilityLabel(),
                confidenceScore
        );
    }

    private String buildRecommendation(List<AbilityScoreVO> abilityScores) {
        AbilityScoreVO weakest = abilityScores.stream()
                .min(Comparator.comparing(AbilityScoreVO::getScore))
                .orElseThrow(IllegalStateException::new);
        if (AbilityTypeEnum.READING.getCode().equals(weakest.getAbilityType())) {
            return "建议优先巩固高频认读词和词义匹配练习，先把看到单词就能稳定反应出来的基础盘做厚。";
        }
        if (AbilityTypeEnum.LISTENING.getCode().equals(weakest.getAbilityType())) {
            return "建议增加听音辨义和短音频跟读练习，优先覆盖高频词、相近发音词和多音节词。";
        }
        return "建议补充常见语境句、固定搭配和近义词辨析练习，提升在上下文中判断词义的稳定性。";
    }

    private BigDecimal percentage(BigDecimal numerator, BigDecimal denominator) {
        return divide(numerator, denominator).multiply(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerator.divide(denominator, 4, RoundingMode.HALF_UP);
    }
}
