package com.ciya.cece.assessment.service;

import com.ciya.cece.assessment.dto.StartAssessmentRequest;
import com.ciya.cece.assessment.dto.SubmitAnswerRequest;
import com.ciya.cece.assessment.vo.AssessmentHistoryVO;
import com.ciya.cece.assessment.vo.AssessmentNextQuestionVO;
import com.ciya.cece.assessment.vo.AssessmentResultVO;
import com.ciya.cece.assessment.vo.StartAssessmentVO;
import com.ciya.cece.assessment.vo.SubmitAnswerVO;

import java.util.List;

public interface AssessmentService {

    StartAssessmentVO startAssessment(StartAssessmentRequest request);

    AssessmentNextQuestionVO getNextQuestion(String sessionNo);

    SubmitAnswerVO submitAnswer(String sessionNo, SubmitAnswerRequest request);

    AssessmentResultVO finishAssessment(String sessionNo);

    AssessmentResultVO getAssessmentResult(String sessionNo);

    List<AssessmentHistoryVO> getAssessmentHistory(Long studentId);
}
