package com.example.utmentor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice(basePackages = "com.example.utmentor")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ValidatorException.class)
    public ResponseEntity<ProblemDetail> onValidate(ValidatorException ex) {
        HttpStatusCode status;

        // Handle specific error codes
        if (Errors.USER_NOT_FOUND.equals(ex.getMessage())) {
            status = HttpStatus.NOT_FOUND;
        } else if (Errors.INVALID_TOKEN.equals(ex.getMessage())) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = ex.getHttpCode() != null ? ex.getHttpCode() : HttpStatus.BAD_REQUEST;
        }

        ProblemDetail pd = ProblemDetail.forStatus(status);
        if (ex.getTitle() != null) pd.setTitle(ex.getTitle());
        pd.setProperty("errors", ex.getErrors());
        return ResponseEntity.status(status).body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> onBeanValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        List<Map<String, String>> items = new ArrayList<>();

        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            items.add(Map.of(
                    "field", fe.getField(),
                    "code",  fe.getCode(),
                    "message", fe.getDefaultMessage()
            ));
        }

        ex.getBindingResult().getGlobalErrors().forEach(oe -> {
            items.add(Map.of(
                    "field", "global",
                    "code",  oe.getCode(),
                    "message", oe.getDefaultMessage()
            ));
        });
        pd.setProperty("errors", items);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> onConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        var items = ex.getConstraintViolations().stream()
                .map(cv -> Map.of(
                        "field", cv.getPropertyPath().toString(),
                        "code",  cv.getMessageTemplate(),
                        "message", cv.getMessage()
                )).toList();
        pd.setProperty("errors", items);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> onBindException(BindException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        var items = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "code",  fe.getCode(),
                        "message", fe.getDefaultMessage()
                )).toList();
        pd.setProperty("errors", items);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> onNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setProperty("errors", List.of(Map.of(
                "field","body", "code","malformed_json", "message","Request body is not readable JSON."
        )));
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> onDataIntegrity(DataIntegrityViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setProperty("errors", List.of(Map.of(
                "field","global", "code","unique_violation", "message","Duplicate value violates a unique constraint."
        )));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> onAny(Exception ex) {
        log.error("Unhandled exception", ex);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }
}