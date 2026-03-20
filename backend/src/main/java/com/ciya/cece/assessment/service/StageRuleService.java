package com.ciya.cece.assessment.service;

import com.ciya.cece.assessment.entity.AssessmentStageRule;
import com.ciya.cece.assessment.vo.StageRuleSummaryVO;

import java.util.List;

public interface StageRuleService {

    List<StageRuleSummaryVO> listAvailableStages();

    AssessmentStageRule getActiveRule(String stageCode);
}
