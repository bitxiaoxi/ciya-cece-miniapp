package com.ciya.cece.assessment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ciya.cece.assessment.dto.AssessmentAnswerRecordDTO;
import com.ciya.cece.assessment.entity.AssessmentAnswer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssessmentAnswerMapper extends BaseMapper<AssessmentAnswer> {

    List<AssessmentAnswerRecordDTO> selectAnswerRecordsBySessionId(@Param("sessionId") Long sessionId);
}
