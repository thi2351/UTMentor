package com.example.utmentor.util;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.models.webModels.users.LoginResponse;

import java.util.List;

public class UserUtils {
    public static LoginResponse pickUser(User user) {
        if (user == null) return null;

        return new LoginResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole() ,
                user.getAvatarUrl()
        );
    }
}

