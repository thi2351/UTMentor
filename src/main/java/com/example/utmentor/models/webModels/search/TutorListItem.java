package com.example.utmentor.models.webModels.search;

import java.util.List;

import com.example.utmentor.models.docEntities.Connection.StatusRequest;
import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Expertise;

public record TutorListItem(
    String id,
    String firstName,
    String lastName,
    String avatarUrl,
    Department department,
    List<Expertise> expertise,
    int rating_count,
    double rating_avg,
    int currMentee,
    int maxMentee,
    String description,
    StatusRequest statusConnection
) {}


