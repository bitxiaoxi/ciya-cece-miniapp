package com.ciya.cece.assessment.service;

import com.ciya.cece.assessment.dto.AiExplainRequest;
import com.ciya.cece.assessment.dto.AiExplainResponse;

public interface AiExplainService {

    AiExplainResponse explain(AiExplainRequest request);
}
