package com.ciya.cece.assessment.enums;

import lombok.Getter;

@Getter
public enum PhaseTypeEnum {

    ROUTE("ROUTE"),
    CORE("CORE"),
    ANCHOR("ANCHOR");

    private final String code;

    PhaseTypeEnum(String code) {
        this.code = code;
    }
}
