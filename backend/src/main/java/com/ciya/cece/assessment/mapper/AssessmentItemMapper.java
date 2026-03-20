package com.ciya.cece.assessment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ciya.cece.assessment.entity.AssessmentItem;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AssessmentItemMapper extends BaseMapper<AssessmentItem> {

    List<AssessmentItem> selectCandidateItems(@Param("stageCode") String stageCode,
                                              @Param("bankVersion") String bankVersion,
                                              @Param("isAnchor") Integer isAnchor,
                                              @Param("minDifficulty") BigDecimal minDifficulty,
                                              @Param("maxDifficulty") BigDecimal maxDifficulty,
                                              @Param("targetDifficulty") BigDecimal targetDifficulty,
                                              @Param("excludedItemIds") List<Long> excludedItemIds,
                                              @Param("limitSize") Integer limitSize);

    String selectLatestBankVersion(@Param("stageCode") String stageCode);
}
