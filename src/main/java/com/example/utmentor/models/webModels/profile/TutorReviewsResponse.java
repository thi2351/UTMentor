package com.example.utmentor.models.webModels.profile;

import com.example.utmentor.models.webModels.PageResponse;

public record TutorReviewsResponse(
    PageResponse<ReviewResponse> reviews
) {}

