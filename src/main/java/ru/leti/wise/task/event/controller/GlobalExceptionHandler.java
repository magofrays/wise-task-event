package ru.leti.wise.task.event.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import ru.leti.wise.task.event.exception.BusinessException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handle(BusinessException e){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
        problemDetail.setTitle("Business Error");
        problemDetail.setProperty("errorCode", e.getErrorCode());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<String>> handleValidationException(ConstraintViolationException ex) {
        return Mono.just(ResponseEntity.badRequest().body("Validation failed: " + ex.getMessage()));
    }
}