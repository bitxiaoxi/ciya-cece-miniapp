package com.ciya.cece.assessment.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(0, "success"),
    BAD_REQUEST(400, "请求参数不合法"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "请求冲突"),
    INTERNAL_ERROR(500, "服务器内部错误");

    private final Integer code;

    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
