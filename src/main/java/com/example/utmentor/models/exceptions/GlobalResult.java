package com.example.utmentor.models.exceptions;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;


public record GlobalResult<T>(
        T data,
        Map<String, String> Errors,
        HttpStatus status
) {
    public static <T> GlobalResult<T> ok(T data) {
        return new GlobalResult<>(data, null, HttpStatus.OK);
    }
    public static <T> GlobalResult<T> badRequest(Map<String, String> errors) {
        return new GlobalResult<>(null, errors, HttpStatus.BAD_REQUEST);
    }
    public static <T> GlobalResult<T> of(T data, HttpStatus status) {
        return new GlobalResult<>(data, null, status);
    }

    @Override
    public T data() {
        return data;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public Map<String, String> Errors() {
        return Errors;
    }
}
