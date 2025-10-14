package com.example.utmentor.models.webModels.users;

import java.util.List;

import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.User;

public record LoginResponse(
    String firstName,
    String lastName,
    String username,
    String avatarUrl,
    List<Role> roles,
    String accessToken,
    String tokenType
) {
    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }
    
    public static class LoginResponseBuilder {
        private User user;
        private String accessToken;
        private String tokenType = "Bearer";
        
        public LoginResponseBuilder user(User user) {
            this.user = user;
            return this;
        }
        
        public LoginResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        
        public LoginResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }
        
        public LoginResponse build() {
            if (user == null) {
                throw new IllegalStateException("User is required");
            }
            if (accessToken == null) {
                throw new IllegalStateException("Access token is required");
            }
            
            return new LoginResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getAvatarUrl(),
                user.getRoles(),
                accessToken,
                tokenType
            );
        }
    }
}