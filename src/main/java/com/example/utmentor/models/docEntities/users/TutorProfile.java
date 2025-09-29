package com.example.utmentor.models.docEntities.users;

import com.example.utmentor.models.docEntities.Expertise;

import java.util.List;

public class TutorProfile {
    private String id;
    private String tutorID;
    private List<Expertise> expertise;
    private boolean isActive;
    private Integer maximumCapacity;
    private Integer currentMenteeCount;
}