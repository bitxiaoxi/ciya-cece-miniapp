package com.ciya.cece.assessment.service;

import com.ciya.cece.assessment.dto.AssessmentAnswerRecordDTO;
import com.ciya.cece.assessment.dto.ResultComputationDTO;
import com.ciya.cece.assessment.entity.AssessmentSession;
import com.ciya.cece.assessment.entity.AssessmentStageRule;

import java.util.List;

public interface AssessmentResultService {

    ResultComputationDTO computeResult(AssessmentSession session,
                                       AssessmentStageRule rule,
                                       List<AssessmentAnswerRecordDTO> answerRecords);
}
