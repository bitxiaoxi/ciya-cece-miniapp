package com.ciya.cece.assessment.exception;

import com.ciya.cece.assessment.common.ApiResponse;
import com.ciya.cece.assessment.common.ErrorCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.failure(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResponse<Void> handleValidationException(Exception exception) {
        String message;
        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) exception;
            message = ex.getBindingResult().getFieldErrors()
                    .stream()
                    .map(this::formatFieldError)
                    .collect(Collectors.joining("; "));
        } else {
            BindException ex = (BindException) exception;
            message = ex.getBindingResult().getFieldErrors()
                    .stream()
                    .map(this::formatFieldError)
                    .collect(Collectors.joining("; "));
        }
        return ApiResponse.failure(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraintViolationException(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations()
                .stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.joining("; "));
        return ApiResponse.failure(ErrorCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ApiResponse.failure(ErrorCode.BAD_REQUEST.getCode(), "请求体格式不正确");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        return ApiResponse.failure(ErrorCode.INTERNAL_ERROR.getCode(), exception.getMessage());
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ":" + fieldError.getDefaultMessage();
    }

    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ":" + violation.getMessage();
    }
}
