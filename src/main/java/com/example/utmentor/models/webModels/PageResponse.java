package com.example.utmentor.models.webModels;

import java.util.List;

public record PageResponse<T>(
    List<T> data,
    int page,
    int pageSize,
    long total,
    int totalPages,
    boolean hasNext
) {}
