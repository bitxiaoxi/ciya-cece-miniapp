package com.ciya.cece.assessment.enums;

import com.ciya.cece.assessment.common.ErrorCode;
import com.ciya.cece.assessment.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum StageCodeEnum {

    K3("K3", "幼儿园大班", 1),
    P1_2("P1_2", "小学一至二年级", 2),
    P3_4("P3_4", "小学三至四年级", 3),
    P5_6("P5_6", "小学五至六年级", 4),
    J7_9("J7_9", "初中七至九年级", 5);

    private final String code;

    private final String label;

    private final Integer order;

    StageCodeEnum(String code, String label, Integer order) {
        this.code = code;
        this.label = label;
        this.order = order;
    }

    public static StageCodeEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> item.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST, "不支持的学段:" + code));
    }
}
