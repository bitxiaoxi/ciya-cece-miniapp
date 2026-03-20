package com.ciya.cece.assessment.enums;

import com.ciya.cece.assessment.common.ErrorCode;
import com.ciya.cece.assessment.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AbilityTypeEnum {

    READING("READING", "认读理解"),
    LISTENING("LISTENING", "听辨理解"),
    CONTEXT("CONTEXT", "语境理解");

    private final String code;

    private final String label;

    AbilityTypeEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static AbilityTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "不支持的能力类型:" + code));
    }
}
