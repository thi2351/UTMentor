package com.example.utmentor.services;

import com.example.utmentor.infrastructures.repository.UserRepository;
import com.example.utmentor.models.docEntities.User;
import com.example.utmentor.models.webModels.users.CreateUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserCrudService {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserCrudService(UserRepository users, PasswordEncoder encoder) {
        this.users = users; this.encoder = encoder;
    }

    public User create(CreateUserRequest r) {
        if (users.existsByEmail(r.email()))    throw new IllegalArgumentException("Email already in use");
        if (users.existsByUsername(r.username())) throw new IllegalArgumentException("Username already in use");
        String hash = encoder.encode(r.password());
        System.out.println("Create a user at " + Instant.now());
        return users.save(new User(UUID.randomUUID().toString(),
                        r.lastName(),
                        r.firstName(),
                        r.department(),
                        r.email(),
                        r.username(),
                        hash,
                        r.role(),
                        Instant.now())
        );
    }
}
