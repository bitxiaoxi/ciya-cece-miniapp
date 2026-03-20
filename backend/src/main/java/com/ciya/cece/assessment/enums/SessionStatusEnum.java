package com.ciya.cece.assessment.enums;

import lombok.Getter;

@Getter
public enum SessionStatusEnum {

    IN_PROGRESS("IN_PROGRESS"),
    FINISHED("FINISHED"),
    ABORTED("ABORTED");

    private final String code;

    SessionStatusEnum(String code) {
        this.code = code;
    }
}
