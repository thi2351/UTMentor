package com.example.utmentor.models.webModels.profile;

import com.example.utmentor.models.docEntities.Expertise;
import java.util.List;

public class UpdateProfileTutorRequest {
    private String phoneNumber;
    private String description;
    private String avatar;
    private List<Expertise> expertise; //
    private List<Achievement> achievements;

}
