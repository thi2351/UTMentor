package com.example.utmentor.models.webModels;

import java.util.List;

public record PageResponse<T>(
    List<T> data,
    int page,
    int pageSize,
    long total,
    int totalPages,
    boolean hasNext
) {
    public PageResponse(List<T> data, int page, int pageSize, long total) {
        this(
            data,
            page,
            pageSize,
            total,
            pageSize <= 0 ? 0 : (int) Math.ceil((double) total / pageSize),
            page < (pageSize <= 0 ? 0 : (int) Math.ceil((double) total / pageSize))
        );
    }
}
