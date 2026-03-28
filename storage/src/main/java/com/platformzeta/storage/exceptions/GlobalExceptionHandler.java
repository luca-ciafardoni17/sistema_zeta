package com.platformzeta.storage.exceptions;

import com.platformzeta.storage.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exception handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Generic handling for non-provided exception
     * @param exception
     * @param webRequest
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception exception, WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * AccessDeniedException specific implementation
     * @param exception
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(Exception exception, WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * MethodArgumentNotValidException specific implementation
     * @param exception
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidatorsException(MethodArgumentNotValidException exception) {
        Map<String,String> errors = new HashMap<>();
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        fieldErrors.forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * HandlerMethodValidationException specific implementation
     * @param exception
     * @return
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String,String>> handleException(HandlerMethodValidationException exception) {
        Map<String, String> errors = new HashMap<>();
        List<ParameterValidationResult> results = exception.getParameterValidationResults();
        results.forEach(result -> {
            String paramName = result.getMethodParameter().getParameterName();
            String combinedMessages = result.getResolvableErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            errors.put(paramName, combinedMessages);
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
