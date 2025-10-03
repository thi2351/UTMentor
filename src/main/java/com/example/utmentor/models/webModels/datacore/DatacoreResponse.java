package com.example.utmentor.models.webModels.datacore;

import com.example.utmentor.models.docEntities.Department;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.StudentProfile;
import com.example.utmentor.models.docEntities.users.TutorProfile;
import java.util.List;
public record DatacoreResponse(
        String id,
        String firstName,
        String lastName,
        Department department,
        List<Role> role,
        String email,
        StudentProfile studentProfile,
        TutorProfile tutorProfile,
        boolean isDeleted
) {
}
