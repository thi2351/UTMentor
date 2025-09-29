package com.example.utmentor.util;

import com.example.utmentor.util.ErrorItem;
import com.example.utmentor.util.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.example.utmentor")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ValidatorException.class)
    public ResponseEntity<ProblemDetail> onValidate(ValidatorException ex) {
        HttpStatusCode status = ex.getHttpCode() != null ? ex.getHttpCode() : HttpStatus.BAD_REQUEST;
        ProblemDetail pd = ProblemDetail.forStatus(status);
        if (ex.getTitle() != null) pd.setTitle(ex.getTitle());
        // Không set detail theo yêu cầu
        pd.setProperty("errors", ex.getErrors());
        return ResponseEntity.status(status).body(pd);
    }

    // @Valid trên body (DTO) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> onBeanValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        List<Map<String, String>> items = new ArrayList<>();
        // Field errors
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            items.add(Map.of(
                    "field", fe.getField(),
                    "code",  fe.getCode(),
                    "message", fe.getDefaultMessage()
            ));
        }
        // Class-level/Object errors (ví dụ cross-field)
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

    // @Validated trên @RequestParam/@PathVariable/... -> 400
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

    // Binding error cho form-data/query object -> 400
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

    // JSON parse lỗi -> 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> onNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setProperty("errors", List.of(Map.of(
                "field","body", "code","malformed_json", "message","Request body is not readable JSON."
        )));
        return ResponseEntity.badRequest().body(pd);
    }

    // Unique/constraint từ DB -> 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> onDataIntegrity(DataIntegrityViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflict");
        pd.setProperty("errors", List.of(Map.of(
                "field","global", "code","unique_violation", "message","Duplicate value violates a unique constraint."
        )));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    // Catch-all cuối cùng -> 500 (ẩn detail)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> onAny(Exception ex) {
        log.error("Unhandled exception", ex);
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }
}
