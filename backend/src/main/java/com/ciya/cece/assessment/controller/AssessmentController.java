package com.ciya.cece.assessment.controller;

import com.ciya.cece.assessment.common.ApiResponse;
import com.ciya.cece.assessment.dto.StartAssessmentRequest;
import com.ciya.cece.assessment.dto.SubmitAnswerRequest;
import com.ciya.cece.assessment.service.AssessmentService;
import com.ciya.cece.assessment.service.StageRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
public class AssessmentController {

    private final StageRuleService stageRuleService;
    private final AssessmentService assessmentService;

    @GetMapping("/stages")
    public ApiResponse<?> listStages() {
        return ApiResponse.success(stageRuleService.listAvailableStages());
    }

    @PostMapping("/start")
    public ApiResponse<?> startAssessment(@Valid @RequestBody StartAssessmentRequest request) {
        return ApiResponse.success(assessmentService.startAssessment(request));
    }

    @GetMapping("/{sessionNo}/next-question")
    public ApiResponse<?> getNextQuestion(@PathVariable("sessionNo") String sessionNo) {
        return ApiResponse.success(assessmentService.getNextQuestion(sessionNo));
    }

    @PostMapping("/{sessionNo}/answer")
    public ApiResponse<?> submitAnswer(@PathVariable("sessionNo") String sessionNo,
                                       @Valid @RequestBody SubmitAnswerRequest request) {
        return ApiResponse.success(assessmentService.submitAnswer(sessionNo, request));
    }

    @PostMapping("/{sessionNo}/finish")
    public ApiResponse<?> finishAssessment(@PathVariable("sessionNo") String sessionNo) {
        return ApiResponse.success(assessmentService.finishAssessment(sessionNo));
    }

    @GetMapping("/{sessionNo}/result")
    public ApiResponse<?> getAssessmentResult(@PathVariable("sessionNo") String sessionNo) {
        return ApiResponse.success(assessmentService.getAssessmentResult(sessionNo));
    }

    @GetMapping("/history")
    public ApiResponse<?> getHistory(@RequestParam("studentId") @NotNull Long studentId) {
        return ApiResponse.success(assessmentService.getAssessmentHistory(studentId));
    }
}
