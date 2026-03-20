package com.ciya.cece.assessment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ciya.cece.assessment.common.ErrorCode;
import com.ciya.cece.assessment.entity.AssessmentStageRule;
import com.ciya.cece.assessment.enums.StageCodeEnum;
import com.ciya.cece.assessment.exception.BusinessException;
import com.ciya.cece.assessment.mapper.AssessmentStageRuleMapper;
import com.ciya.cece.assessment.service.StageRuleService;
import com.ciya.cece.assessment.vo.StageRuleSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageRuleServiceImpl implements StageRuleService {

    private final AssessmentStageRuleMapper assessmentStageRuleMapper;

    @Override
    public List<StageRuleSummaryVO> listAvailableStages() {
        return assessmentStageRuleMapper.selectActiveStageRules()
                .stream()
                .sorted(Comparator.comparingInt(rule -> StageCodeEnum.fromCode(rule.getStageCode()).getOrder()))
                .map(rule -> StageRuleSummaryVO.builder()
                        .stageCode(rule.getStageCode())
                        .stageName(rule.getStageName())
                        .startDifficulty(rule.getStartDifficulty())
                        .routeQuestionCount(rule.getRouteQuestionCount())
                        .coreQuestionCount(rule.getCoreQuestionCount())
                        .anchorQuestionCount(rule.getAnchorQuestionCount())
                        .ruleVersion(rule.getRuleVersion())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public AssessmentStageRule getActiveRule(String stageCode) {
        StageCodeEnum.fromCode(stageCode);
        LambdaQueryWrapper<AssessmentStageRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AssessmentStageRule::getStageCode, stageCode)
                .eq(AssessmentStageRule::getStatus, 1)
                .last("limit 1");
        AssessmentStageRule rule = assessmentStageRuleMapper.selectOne(queryWrapper);
        if (rule == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "未找到启用中的学段规则");
        }
        return rule;
    }
}
