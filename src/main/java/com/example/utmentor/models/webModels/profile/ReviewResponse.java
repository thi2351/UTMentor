package com.example.utmentor.models.webModels.profile;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ReviewResponse(
    String reviewID,
    String reviewerName,
    String reviewerAvatarUrl,
    Integer rating,
    String comment,
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant timestamp
) {}

