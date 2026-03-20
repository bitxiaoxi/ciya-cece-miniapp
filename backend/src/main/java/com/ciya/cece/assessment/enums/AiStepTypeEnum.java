package com.ciya.cece.assessment.enums;

import lombok.Getter;

@Getter
public enum AiStepTypeEnum {

    ITEM_REVIEW("ITEM_REVIEW"),
    ITEM_SELECT("ITEM_SELECT"),
    RESULT_EXPLAIN("RESULT_EXPLAIN");

    private final String code;

    AiStepTypeEnum(String code) {
        this.code = code;
    }
}
