package com.ciya.cece.assessment.enums;

import lombok.Getter;

@Getter
public enum ContentSourceEnum {

    MANUAL("MANUAL"),
    AI_DRAFT("AI_DRAFT"),
    AI_REVIEWED("AI_REVIEWED");

    private final String code;

    ContentSourceEnum(String code) {
        this.code = code;
    }
}
