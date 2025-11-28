package com.example.utmentor.models.webModels.communities;

public record GetCommunitiesResponse(
        String id,
        String name,
        int studentCount,
        boolean isActive
) { }
