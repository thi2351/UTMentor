package com.example.utmentor.models.webModels.users;

import java.util.List;
import com.example.utmentor.models.docEntities.Role;
public record LoginResponse(
        String username,
        String firstName,
        String lastName,
        Role[] role,
        String avatarUrl
) {} 