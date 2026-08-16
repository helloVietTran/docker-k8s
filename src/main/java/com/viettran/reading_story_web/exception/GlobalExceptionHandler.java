package com.viettran.reading_story_web.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.viettran.reading_story_web.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> handlingRuntimeException(Exception exception) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        if (exception.getFieldError() == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.builder()
                            .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                            .result("Uncategorized exception")
                            .build());
        }
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<String>> handleNumberFormatException(NumberFormatException ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(ErrorCode.NUMBER_FORMAT_EXCEPTION.getCode())
                .message("Invalid story ID format: ID should be a number.")
                .result(null)
                .build();
        return ResponseEntity.badRequest().body(response);
    }
}
