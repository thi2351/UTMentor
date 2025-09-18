package com.example.utmentor.util;

import org.springframework.http.HttpStatusCode;

import java.util.ArrayList;
import java.util.List;

public class ValidatorException extends RuntimeException {
    private HttpStatusCode httpCode;
    private String title;
    private List<ErrorItem> errors = new ArrayList<>();

    public ValidatorException(String title) {
        this.title = title;
    }
    public void add(String field, String code, String message) {
        errors.add(new ErrorItem(field, code, message));
    }

    public void add(ErrorItem e) {
        errors.add(e);
    }
    public boolean hasAny() {
        return !errors.isEmpty();
    }

    public HttpStatusCode getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(HttpStatusCode httpCode) {
        this.httpCode = httpCode;
    }

    public List<ErrorItem> getErrors() {
        return errors;
    }

    public String getTitle() {
        return title;
    }
}
