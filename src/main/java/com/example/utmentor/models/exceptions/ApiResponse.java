package com.example.utmentor.models.exceptions;

import java.util.Map;

public record ApiResponse<T>(
        T data,
        Map<String, String> errors
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data, null);
    }
    public static <T> ApiResponse<T> fail(Map<String, String> errors) {
        return new ApiResponse<>( null, errors);
    }
}