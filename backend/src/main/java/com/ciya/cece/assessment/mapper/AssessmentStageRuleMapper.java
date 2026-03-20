package com.ciya.cece.assessment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ciya.cece.assessment.entity.AssessmentStageRule;

import java.util.List;

public interface AssessmentStageRuleMapper extends BaseMapper<AssessmentStageRule> {

    List<AssessmentStageRule> selectActiveStageRules();
}
