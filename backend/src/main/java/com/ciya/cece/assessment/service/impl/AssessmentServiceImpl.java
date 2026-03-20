package com.ciya.cece.assessment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ciya.cece.assessment.common.ErrorCode;
import com.ciya.cece.assessment.dto.AiExplainRequest;
import com.ciya.cece.assessment.dto.AiExplainResponse;
import com.ciya.cece.assessment.dto.AssessmentAnswerRecordDTO;
import com.ciya.cece.assessment.dto.QuestionSelectionResult;
import com.ciya.cece.assessment.dto.ResultComputationDTO;
import com.ciya.cece.assessment.dto.StartAssessmentRequest;
import com.ciya.cece.assessment.dto.SubmitAnswerRequest;
import com.ciya.cece.assessment.entity.AssessmentAiLog;
import com.ciya.cece.assessment.entity.AssessmentAnswer;
import com.ciya.cece.assessment.entity.AssessmentItem;
import com.ciya.cece.assessment.entity.AssessmentItemOption;
import com.ciya.cece.assessment.entity.AssessmentResult;
import com.ciya.cece.assessment.entity.AssessmentSession;
import com.ciya.cece.assessment.entity.AssessmentStageRule;
import com.ciya.cece.assessment.entity.StudentProfile;
import com.ciya.cece.assessment.enums.AbilityTypeEnum;
import com.ciya.cece.assessment.enums.AiStepTypeEnum;
import com.ciya.cece.assessment.enums.AnswerStatusEnum;
import com.ciya.cece.assessment.enums.PhaseTypeEnum;
import com.ciya.cece.assessment.enums.SessionStatusEnum;
import com.ciya.cece.assessment.exception.BusinessException;
import com.ciya.cece.assessment.mapper.AssessmentAiLogMapper;
import com.ciya.cece.assessment.mapper.AssessmentAnswerMapper;
import com.ciya.cece.assessment.mapper.AssessmentItemMapper;
import com.ciya.cece.assessment.mapper.AssessmentItemOptionMapper;
import com.ciya.cece.assessment.mapper.AssessmentResultMapper;
import com.ciya.cece.assessment.mapper.AssessmentSessionMapper;
import com.ciya.cece.assessment.mapper.StudentProfileMapper;
import com.ciya.cece.assessment.service.AiExplainService;
import com.ciya.cece.assessment.service.AssessmentEngineService;
import com.ciya.cece.assessment.service.AssessmentResultService;
import com.ciya.cece.assessment.service.AssessmentService;
import com.ciya.cece.assessment.service.StageRuleService;
import com.ciya.cece.assessment.vo.AbilityScoreVO;
import com.ciya.cece.assessment.vo.AssessmentHistoryVO;
import com.ciya.cece.assessment.vo.AssessmentNextQuestionVO;
import com.ciya.cece.assessment.vo.AssessmentOptionVO;
import com.ciya.cece.assessment.vo.AssessmentQuestionVO;
import com.ciya.cece.assessment.vo.AssessmentResultVO;
import com.ciya.cece.assessment.vo.ProgressVO;
import com.ciya.cece.assessment.vo.SessionSummaryVO;
import com.ciya.cece.assessment.vo.StartAssessmentVO;
import com.ciya.cece.assessment.vo.SubmitAnswerVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private static final DateTimeFormatter SESSION_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final StudentProfileMapper studentProfileMapper;
    private final AssessmentItemMapper assessmentItemMapper;
    private final AssessmentItemOptionMapper assessmentItemOptionMapper;
    private final AssessmentSessionMapper assessmentSessionMapper;
    private final AssessmentAnswerMapper assessmentAnswerMapper;
    private final AssessmentResultMapper assessmentResultMapper;
    private final AssessmentAiLogMapper assessmentAiLogMapper;
    private final StageRuleService stageRuleService;
    private final AssessmentEngineService assessmentEngineService;
    private final AssessmentResultService assessmentResultService;
    private final AiExplainService aiExplainService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StartAssessmentVO startAssessment(StartAssessmentRequest request) {
        StudentProfile studentProfile = studentProfileMapper.selectById(request.getStudentId());
        if (studentProfile == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生档案不存在");
        }
        AssessmentStageRule rule = stageRuleService.getActiveRule(request.getSelectedStageCode());
        ensureQuestionBankCoverage(rule);

        LocalDateTime now = LocalDateTime.now();
        AssessmentSession session = new AssessmentSession();
        session.setSessionNo(generateSessionNo());
        session.setStudentId(request.getStudentId());
        session.setSelectedStageCode(request.getSelectedStageCode());
        session.setStartDifficulty(rule.getStartDifficulty());
        session.setCurrentDifficulty(rule.getStartDifficulty());
        session.setRuleVersion(rule.getRuleVersion());
        session.setBankVersion(resolveBankVersion(request.getSelectedStageCode()));
        session.setAiEnabled(Boolean.TRUE.equals(request.getAiEnabled()) ? 1 : 0);
        session.setStatus(SessionStatusEnum.IN_PROGRESS.getCode());
        session.setAnsweredCount(0);
        session.setCorrectCount(0);
        session.setUncertainCount(0);
        session.setStartedAt(now);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        assessmentSessionMapper.insert(session);

        QuestionSelectionResult firstQuestion = assessmentEngineService.selectNextQuestion(session, rule, null, null, null);
        return StartAssessmentVO.builder()
                .sessionNo(session.getSessionNo())
                .selectedStageCode(session.getSelectedStageCode())
                .startDifficulty(session.getStartDifficulty())
                .ruleVersion(session.getRuleVersion())
                .bankVersion(session.getBankVersion())
                .totalQuestionCount(assessmentEngineService.totalQuestionCount(rule))
                .firstQuestion(firstQuestion == null ? null : buildQuestionVO(firstQuestion))
                .build();
    }

    @Override
    public AssessmentNextQuestionVO getNextQuestion(String sessionNo) {
        AssessmentSession session = getSessionBySessionNo(sessionNo);
        ensureSessionInProgress(session);
        AssessmentStageRule rule = stageRuleService.getActiveRule(session.getSelectedStageCode());
        List<AssessmentAnswerRecordDTO> answerRecords = loadAnswerRecords(session.getId());
        List<Long> answeredItemIds = answerRecords.stream().map(AssessmentAnswerRecordDTO::getItemId).collect(Collectors.toList());
        String lastQuestionType = answerRecords.isEmpty() ? null : answerRecords.get(answerRecords.size() - 1).getQuestionType();
        String lastAbilityType = answerRecords.isEmpty() ? null : answerRecords.get(answerRecords.size() - 1).getAbilityType();

        QuestionSelectionResult nextQuestion = assessmentEngineService.selectNextQuestion(session, rule, answeredItemIds, lastQuestionType, lastAbilityType);
        if (nextQuestion == null) {
            return AssessmentNextQuestionVO.builder()
                    .shouldFinish(Boolean.TRUE)
                    .question(null)
                    .progress(buildProgress(session, rule, null))
                    .build();
        }
        return AssessmentNextQuestionVO.builder()
                .shouldFinish(Boolean.FALSE)
                .question(buildQuestionVO(nextQuestion))
                .progress(buildProgress(session, rule, nextQuestion.getPhaseType()))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitAnswerVO submitAnswer(String sessionNo, SubmitAnswerRequest request) {
        AssessmentSession session = getSessionBySessionNo(sessionNo);
        ensureSessionInProgress(session);
        AssessmentStageRule rule = stageRuleService.getActiveRule(session.getSelectedStageCode());

        LambdaQueryWrapper<AssessmentAnswer> duplicateWrapper = new LambdaQueryWrapper<>();
        duplicateWrapper.eq(AssessmentAnswer::getSessionId, session.getId())
                .eq(AssessmentAnswer::getItemId, request.getItemId())
                .last("limit 1");
        if (assessmentAnswerMapper.selectOne(duplicateWrapper) != null) {
            throw new BusinessException(ErrorCode.CONFLICT, "当前题目已作答，不能重复提交");
        }

        List<AssessmentAnswerRecordDTO> answerRecords = loadAnswerRecords(session.getId());
        List<Long> answeredItemIds = answerRecords.stream().map(AssessmentAnswerRecordDTO::getItemId).collect(Collectors.toList());
        String lastQuestionType = answerRecords.isEmpty() ? null : answerRecords.get(answerRecords.size() - 1).getQuestionType();
        String lastAbilityType = answerRecords.isEmpty() ? null : answerRecords.get(answerRecords.size() - 1).getAbilityType();
        QuestionSelectionResult expectedQuestion = assessmentEngineService.selectNextQuestion(session, rule, answeredItemIds, lastQuestionType, lastAbilityType);
        if (expectedQuestion == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "当前测评已没有可作答题目，请直接完成测评");
        }
        if (!expectedQuestion.getItem().getId().equals(request.getItemId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "当前作答题目与系统预期题目不一致");
        }

        AssessmentItem item = expectedQuestion.getItem();
        List<AssessmentItemOption> options = listItemOptions(item.getId());
        AnswerNormalization normalization = normalizeAnswer(request, options);
        BigDecimal difficultyBefore = session.getCurrentDifficulty();
        BigDecimal difficultyAfter = assessmentEngineService.adjustDifficulty(difficultyBefore, normalization.getAnswerStatus(), rule);
        LocalDateTime now = LocalDateTime.now();

        AssessmentAnswer answer = new AssessmentAnswer();
        answer.setSessionId(session.getId());
        answer.setItemId(item.getId());
        answer.setQuestionNo(expectedQuestion.getQuestionNo());
        answer.setPhaseType(expectedQuestion.getPhaseType());
        answer.setSelectedOptionId(normalization.getSelectedOptionId());
        answer.setAnswerStatus(normalization.getAnswerStatus());
        answer.setIsCorrect(normalization.getIsCorrect());
        answer.setResponseTimeMs(request.getResponseTimeMs());
        answer.setDifficultyBefore(difficultyBefore);
        answer.setDifficultyAfter(difficultyAfter);
        answer.setCreatedAt(now);
        assessmentAnswerMapper.insert(answer);

        session.setAnsweredCount(session.getAnsweredCount() + 1);
        session.setCorrectCount(session.getCorrectCount() + (normalization.getIsCorrect() == 1 ? 1 : 0));
        if (AnswerStatusEnum.UNCERTAIN.getCode().equals(normalization.getAnswerStatus())) {
            session.setUncertainCount(session.getUncertainCount() + 1);
        }
        session.setCurrentDifficulty(difficultyAfter);
        session.setUpdatedAt(now);
        assessmentSessionMapper.updateById(session);

        List<Long> nextAnsweredItemIds = new ArrayList<>(answeredItemIds);
        nextAnsweredItemIds.add(item.getId());
        QuestionSelectionResult nextQuestion = assessmentEngineService.selectNextQuestion(
                session,
                rule,
                nextAnsweredItemIds,
                item.getQuestionType(),
                item.getAbilityType()
        );
        boolean shouldFinish = nextQuestion == null || session.getAnsweredCount() >= assessmentEngineService.totalQuestionCount(rule);
        return SubmitAnswerVO.builder()
                .isCorrect(normalization.getIsCorrect() == 1)
                .nextDifficulty(difficultyAfter)
                .shouldFinish(shouldFinish)
                .progress(buildProgress(session, rule, shouldFinish ? null : nextQuestion.getPhaseType()))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssessmentResultVO finishAssessment(String sessionNo) {
        AssessmentSession session = getSessionBySessionNo(sessionNo);
        if (SessionStatusEnum.FINISHED.getCode().equals(session.getStatus())) {
            return getAssessmentResult(sessionNo);
        }
        if (SessionStatusEnum.ABORTED.getCode().equals(session.getStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "已终止的测评不能生成结果");
        }
        AssessmentStageRule rule = stageRuleService.getActiveRule(session.getSelectedStageCode());
        List<AssessmentAnswerRecordDTO> answerRecords = loadAnswerRecords(session.getId());
        ResultComputationDTO computed = assessmentResultService.computeResult(session, rule, answerRecords);

        String summaryText = computed.getSummaryText();
        String recommendationText = computed.getRecommendationText();
        if (Integer.valueOf(1).equals(session.getAiEnabled())) {
            AiExplainResponse aiExplainResponse = aiExplainService.explain(buildAiExplainRequest(session, computed));
            summaryText = aiExplainResponse.getSummaryText();
            recommendationText = aiExplainResponse.getRecommendationText();
            persistAiLog(session.getId(), aiExplainResponse);
        }

        AssessmentResult existingResult = getResultEntity(session.getId());
        LocalDateTime now = LocalDateTime.now();
        if (existingResult == null) {
            AssessmentResult assessmentResult = new AssessmentResult();
            assessmentResult.setSessionId(session.getId());
            assessmentResult.setEstimatedStageCode(computed.getEstimatedStageCode());
            assessmentResult.setVocabEstimateMin(computed.getVocabEstimateMin());
            assessmentResult.setVocabEstimateMax(computed.getVocabEstimateMax());
            assessmentResult.setVocabEstimateMid(computed.getVocabEstimateMid());
            assessmentResult.setReadingScore(computed.getReadingScore());
            assessmentResult.setListeningScore(computed.getListeningScore());
            assessmentResult.setContextScore(computed.getContextScore());
            assessmentResult.setConfidenceScore(computed.getConfidenceScore());
            assessmentResult.setSummaryText(summaryText);
            assessmentResult.setRecommendationText(recommendationText);
            assessmentResult.setBasisJson(toJson(computed.getBasis()));
            assessmentResult.setCreatedAt(now);
            assessmentResultMapper.insert(assessmentResult);
        }

        session.setStatus(SessionStatusEnum.FINISHED.getCode());
        session.setFinishedAt(now);
        session.setUpdatedAt(now);
        assessmentSessionMapper.updateById(session);
        return getAssessmentResult(sessionNo);
    }

    @Override
    public AssessmentResultVO getAssessmentResult(String sessionNo) {
        AssessmentSession session = getSessionBySessionNo(sessionNo);
        AssessmentResult result = getResultEntity(session.getId());
        if (result == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "测评结果不存在");
        }
        Map<String, Object> basis = parseJsonMap(result.getBasisJson());
        List<AbilityScoreVO> abilityScores = extractAbilityScores(basis);
        return AssessmentResultVO.builder()
                .session(buildSessionSummary(session))
                .estimatedStageCode(result.getEstimatedStageCode())
                .vocabEstimateMin(result.getVocabEstimateMin())
                .vocabEstimateMax(result.getVocabEstimateMax())
                .vocabEstimateMid(result.getVocabEstimateMid())
                .readingScore(result.getReadingScore())
                .listeningScore(result.getListeningScore())
                .contextScore(result.getContextScore())
                .confidenceScore(result.getConfidenceScore())
                .summaryText(result.getSummaryText())
                .recommendationText(result.getRecommendationText())
                .abilityScores(abilityScores)
                .basis(basis)
                .build();
    }

    @Override
    public List<AssessmentHistoryVO> getAssessmentHistory(Long studentId) {
        StudentProfile studentProfile = studentProfileMapper.selectById(studentId);
        if (studentProfile == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生档案不存在");
        }
        return assessmentSessionMapper.selectHistoryByStudentId(studentId);
    }

    private void ensureQuestionBankCoverage(AssessmentStageRule rule) {
        LambdaQueryWrapper<AssessmentItem> nonAnchorWrapper = new LambdaQueryWrapper<>();
        nonAnchorWrapper.eq(AssessmentItem::getStageCode, rule.getStageCode())
                .eq(AssessmentItem::getStatus, 1)
                .eq(AssessmentItem::getIsAnchor, 0);
        long nonAnchorCount = assessmentItemMapper.selectCount(nonAnchorWrapper);

        LambdaQueryWrapper<AssessmentItem> anchorWrapper = new LambdaQueryWrapper<>();
        anchorWrapper.eq(AssessmentItem::getStageCode, rule.getStageCode())
                .eq(AssessmentItem::getStatus, 1)
                .eq(AssessmentItem::getIsAnchor, 1);
        long anchorCount = assessmentItemMapper.selectCount(anchorWrapper);

        if (nonAnchorCount < rule.getRouteQuestionCount() + rule.getCoreQuestionCount() || anchorCount < rule.getAnchorQuestionCount()) {
            throw new BusinessException(ErrorCode.CONFLICT, "题库数量不足，无法启动测评");
        }
    }

    private String resolveBankVersion(String stageCode) {
        String bankVersion = assessmentItemMapper.selectLatestBankVersion(stageCode);
        if (!StringUtils.hasText(bankVersion)) {
            throw new BusinessException(ErrorCode.CONFLICT, "未找到启用中的题库版本");
        }
        return bankVersion;
    }

    private AssessmentSession getSessionBySessionNo(String sessionNo) {
        LambdaQueryWrapper<AssessmentSession> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssessmentSession::getSessionNo, sessionNo).last("limit 1");
        AssessmentSession session = assessmentSessionMapper.selectOne(queryWrapper);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "测评会话不存在");
        }
        return session;
    }

    private void ensureSessionInProgress(AssessmentSession session) {
        if (!SessionStatusEnum.IN_PROGRESS.getCode().equals(session.getStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "当前测评不处于进行中状态");
        }
    }

    private List<AssessmentAnswerRecordDTO> loadAnswerRecords(Long sessionId) {
        List<AssessmentAnswerRecordDTO> records = assessmentAnswerMapper.selectAnswerRecordsBySessionId(sessionId);
        return records == null ? new ArrayList<>() : records;
    }

    private AssessmentQuestionVO buildQuestionVO(QuestionSelectionResult selectionResult) {
        List<AssessmentOptionVO> options = listItemOptions(selectionResult.getItem().getId()).stream()
                .map(option -> AssessmentOptionVO.builder()
                        .id(option.getId())
                        .optionKey(option.getOptionKey())
                        .optionText(option.getOptionText())
                        .optionAudioUrl(option.getOptionAudioUrl())
                        .optionImageUrl(option.getOptionImageUrl())
                        .sortNo(option.getSortNo())
                        .build())
                .collect(Collectors.toList());
        AssessmentItem item = selectionResult.getItem();
        return AssessmentQuestionVO.builder()
                .itemId(item.getId())
                .itemCode(item.getItemCode())
                .stageCode(item.getStageCode())
                .questionType(item.getQuestionType())
                .abilityType(item.getAbilityType())
                .wordText(item.getWordText())
                .stemText(item.getStemText())
                .stemAudioUrl(item.getStemAudioUrl())
                .stemImageUrl(item.getStemImageUrl())
                .phaseType(selectionResult.getPhaseType())
                .questionNo(selectionResult.getQuestionNo())
                .options(options)
                .build();
    }

    private List<AssessmentItemOption> listItemOptions(Long itemId) {
        LambdaQueryWrapper<AssessmentItemOption> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssessmentItemOption::getItemId, itemId)
                .orderByAsc(AssessmentItemOption::getSortNo, AssessmentItemOption::getId);
        return assessmentItemOptionMapper.selectList(queryWrapper);
    }

    private ProgressVO buildProgress(AssessmentSession session, AssessmentStageRule rule, String currentPhase) {
        return ProgressVO.builder()
                .answeredCount(session.getAnsweredCount())
                .totalCount(assessmentEngineService.totalQuestionCount(rule))
                .currentPhase(currentPhase)
                .correctCount(session.getCorrectCount())
                .uncertainCount(session.getUncertainCount())
                .build();
    }

    private String generateSessionNo() {
        return "ASM" + LocalDateTime.now().format(SESSION_NO_FORMATTER) + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }

    private AnswerNormalization normalizeAnswer(SubmitAnswerRequest request, List<AssessmentItemOption> options) {
        if (StringUtils.hasText(request.getAnswerStatus())) {
            AnswerStatusEnum requestedStatus = AnswerStatusEnum.fromCode(request.getAnswerStatus());
            if (requestedStatus == AnswerStatusEnum.UNCERTAIN || requestedStatus == AnswerStatusEnum.SKIP) {
                return new AnswerNormalization(requestedStatus.getCode(), 0, request.getSelectedOptionId());
            }
        }
        if (request.getSelectedOptionId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "正常作答必须传selectedOptionId");
        }
        AssessmentItemOption selectedOption = options.stream()
                .filter(option -> option.getId().equals(request.getSelectedOptionId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "所选选项不存在"));
        String normalizedStatus = Integer.valueOf(1).equals(selectedOption.getIsCorrect())
                ? AnswerStatusEnum.CORRECT.getCode()
                : AnswerStatusEnum.WRONG.getCode();
        Integer isCorrect = Integer.valueOf(1).equals(selectedOption.getIsCorrect()) ? 1 : 0;
        return new AnswerNormalization(normalizedStatus, isCorrect, selectedOption.getId());
    }

    private AssessmentResult getResultEntity(Long sessionId) {
        LambdaQueryWrapper<AssessmentResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssessmentResult::getSessionId, sessionId).last("limit 1");
        return assessmentResultMapper.selectOne(queryWrapper);
    }

    private AiExplainRequest buildAiExplainRequest(AssessmentSession session, ResultComputationDTO computed) {
        AiExplainRequest request = new AiExplainRequest();
        request.setSessionId(session.getId());
        request.setSessionNo(session.getSessionNo());
        request.setSelectedStageCode(session.getSelectedStageCode());
        request.setEstimatedStageCode(computed.getEstimatedStageCode());
        request.setOverallScore(computed.getOverallScore());
        request.setConfidenceScore(computed.getConfidenceScore());
        request.setReadingScore(computed.getReadingScore());
        request.setListeningScore(computed.getListeningScore());
        request.setContextScore(computed.getContextScore());
        request.setVocabEstimateMin(computed.getVocabEstimateMin());
        request.setVocabEstimateMax(computed.getVocabEstimateMax());
        request.setVocabEstimateMid(computed.getVocabEstimateMid());
        request.setAbilityScores(computed.getAbilityScores());
        request.setBasis(computed.getBasis());
        return request;
    }

    private void persistAiLog(Long sessionId, AiExplainResponse aiExplainResponse) {
        AssessmentAiLog aiLog = new AssessmentAiLog();
        aiLog.setSessionId(sessionId);
        aiLog.setStepType(AiStepTypeEnum.RESULT_EXPLAIN.getCode());
        aiLog.setModelName(aiExplainResponse.getModelName());
        aiLog.setPromptVersion(aiExplainResponse.getPromptVersion());
        aiLog.setInputSnapshot(aiExplainResponse.getInputSnapshot());
        aiLog.setOutputSnapshot(aiExplainResponse.getOutputSnapshot());
        aiLog.setDecisionSummary(aiExplainResponse.getDecisionSummary());
        aiLog.setCreatedAt(LocalDateTime.now());
        assessmentAiLogMapper.insert(aiLog);
    }

    private SessionSummaryVO buildSessionSummary(AssessmentSession session) {
        return SessionSummaryVO.builder()
                .sessionNo(session.getSessionNo())
                .studentId(session.getStudentId())
                .selectedStageCode(session.getSelectedStageCode())
                .startDifficulty(session.getStartDifficulty())
                .currentDifficulty(session.getCurrentDifficulty())
                .ruleVersion(session.getRuleVersion())
                .bankVersion(session.getBankVersion())
                .aiEnabled(Integer.valueOf(1).equals(session.getAiEnabled()))
                .status(session.getStatus())
                .answeredCount(session.getAnsweredCount())
                .correctCount(session.getCorrectCount())
                .uncertainCount(session.getUncertainCount())
                .startedAt(session.getStartedAt())
                .finishedAt(session.getFinishedAt())
                .build();
    }

    private Map<String, Object> parseJsonMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception exception) {
            throw new IllegalStateException("解析basis_json失败", exception);
        }
    }

    private List<AbilityScoreVO> extractAbilityScores(Map<String, Object> basis) {
        Object abilityBreakdownObj = basis.get("abilityBreakdown");
        if (!(abilityBreakdownObj instanceof Map)) {
            return new ArrayList<>();
        }
        Map<String, Object> abilityBreakdown = (Map<String, Object>) abilityBreakdownObj;
        List<AbilityScoreVO> abilityScores = new ArrayList<>();
        for (AbilityTypeEnum abilityTypeEnum : AbilityTypeEnum.values()) {
            Map<String, Object> item = abilityBreakdown.get(abilityTypeEnum.getCode()) instanceof Map
                    ? (Map<String, Object>) abilityBreakdown.get(abilityTypeEnum.getCode())
                    : new LinkedHashMap<String, Object>();
            abilityScores.add(AbilityScoreVO.builder()
                    .abilityType(abilityTypeEnum.getCode())
                    .abilityLabel(String.valueOf(item.getOrDefault("label", abilityTypeEnum.getLabel())))
                    .score(toBigDecimal(item.get("score")))
                    .correctCount(toInteger(item.get("correctCount")))
                    .totalCount(toInteger(item.get("totalCount")))
                    .build());
        }
        return abilityScores;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String toJson(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("序列化结果依据失败", exception);
        }
    }

    @lombok.AllArgsConstructor
    @lombok.Getter
    private static class AnswerNormalization {

        private final String answerStatus;

        private final Integer isCorrect;

        private final Long selectedOptionId;
    }
}
