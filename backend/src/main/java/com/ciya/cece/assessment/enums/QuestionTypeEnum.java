package com.ciya.cece.assessment.enums;

import com.ciya.cece.assessment.common.ErrorCode;
import com.ciya.cece.assessment.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum QuestionTypeEnum {

    WORD_TO_CN("WORD_TO_CN", "英文选中文"),
    IMAGE_TO_WORD("IMAGE_TO_WORD", "看图识词"),
    AUDIO_CHOICE("AUDIO_CHOICE", "听音辨词"),
    CONTEXT_CHOICE("CONTEXT_CHOICE", "语境辨析");

    private final String code;

    private final String label;

    QuestionTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static QuestionTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "不支持的题型:" + code));
    }
}
