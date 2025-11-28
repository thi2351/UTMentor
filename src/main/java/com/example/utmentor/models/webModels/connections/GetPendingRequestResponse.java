package com.example.utmentor.models.webModels.connections;

import com.example.utmentor.models.webModels.PageResponse;

public record GetPendingRequestResponse(
        String connectionId,
        String studentName,
        String studentId,
        String message,
        String studentAvatar,
        String requestDate
) {
}
