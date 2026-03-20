package com.ciya.cece.assessment.enums;

import com.ciya.cece.assessment.common.ErrorCode;
import com.ciya.cece.assessment.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
public enum AnswerStatusEnum {

    CORRECT("CORRECT", BigDecimal.ONE),
    WRONG("WRONG", BigDecimal.ZERO),
    UNCERTAIN("UNCERTAIN", new BigDecimal("0.25")),
    SKIP("SKIP", BigDecimal.ZERO);

    private final String code;

    private final BigDecimal weight;

    AnswerStatusEnum(String code, BigDecimal weight) {
        this.code = code;
        this.weight = weight;
    }

    public static AnswerStatusEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "不支持的作答状态:" + code));
    }
}
