package com.ciya.cece.assessment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ciya.cece.assessment.entity.AssessmentSession;
import com.ciya.cece.assessment.vo.AssessmentHistoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssessmentSessionMapper extends BaseMapper<AssessmentSession> {

    List<AssessmentHistoryVO> selectHistoryByStudentId(@Param("studentId") Long studentId);
}
