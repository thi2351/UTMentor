package com.example.utmentor.models.webModels.profile;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Achievement(
    String id,
    String title,
    String description,
    String year,
    AchievementType type
) {}