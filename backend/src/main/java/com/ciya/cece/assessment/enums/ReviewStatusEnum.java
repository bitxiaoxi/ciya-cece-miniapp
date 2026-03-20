package com.ciya.cece.assessment.enums;

import lombok.Getter;

@Getter
public enum ReviewStatusEnum {

    DRAFT("DRAFT"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String code;

    ReviewStatusEnum(String code) {
        this.code = code;
    }
}
