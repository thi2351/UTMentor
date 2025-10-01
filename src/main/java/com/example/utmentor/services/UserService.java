package com.example.utmentor.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.utmentor.infrastructures.repository.UserRepository;
import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.util.Errors;
import com.example.utmentor.util.ValidatorException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CRUD
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public User create(User userInput) {
        // Basic validation
        ValidatorException vex = new ValidatorException("Create user failed.");
        if (userInput.getEmail() == null || userInput.getEmail().isBlank()) {
            vex.add(Errors.EMAIL_REQUIRED);
        }
        if (userInput.getUsername() == null || userInput.getUsername().isBlank()) {
            vex.add(Errors.USERNAME_REQUIRED);
        }
        if (userInput.getPasswordHash() == null || userInput.getPasswordHash().isBlank()) {
            vex.add(Errors.PASSWORD_REQUIRED);
        }
        if (userRepository.existsByEmail(userInput.getEmail())) {
            vex.add(Errors.EMAIL_EXISTS);
        }
        if (userRepository.existsByUsername(userInput.getUsername())) {
            vex.add(Errors.USERNAME_EXISTS);
        }
        if (vex.hasAny()) {
            vex.setHttpCode(HttpStatus.BAD_REQUEST);
            throw vex;
        }

        String id = userInput.getId() != null ? userInput.getId() : UUID.randomUUID().toString();
        List<Role> roles = userInput.getRoles() != null ? userInput.getRoles() : new ArrayList<>();
        String passwordHash = passwordEncoder.encode(userInput.getPasswordHash());

        User user = new User(
                id,
                userInput.getFirstName(),
                userInput.getLastName(),
                userInput.getDepartment(),
                roles,
                userInput.getEmail(),
                userInput.getUsername(),
                userInput.getAvatarUrl(),
                passwordHash,
                userInput.getStudentProfile(),
                userInput.getTutorProfile()
        );

        return userRepository.save(user);
    }

    public User update(String userId, User partial) {
        var existingOpt = userRepository.findById(userId);
        if (existingOpt.isEmpty()) {
            ValidatorException vex = new ValidatorException("User not found");
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }
        User existing = existingOpt.get();

        if (partial.getFirstName() != null) existing.setFirstName(partial.getFirstName());
        if (partial.getLastName() != null) existing.setLastName(partial.getLastName());
        if (partial.getDepartment() != null) existing.setDepartment(partial.getDepartment());
        if (partial.getAvatarUrl() != null) existing.setAvatarUrl(partial.getAvatarUrl());
        if (partial.getEmail() != null && !partial.getEmail().equals(existing.getEmail())) {
            if (userRepository.existsByEmail(partial.getEmail())) {
                ValidatorException vex = new ValidatorException("Email exists");
                vex.add(Errors.EMAIL_EXISTS);
                vex.setHttpCode(HttpStatus.BAD_REQUEST);
                throw vex;
            }
            existing.setEmail(partial.getEmail());
        }
        if (partial.getUsername() != null && !partial.getUsername().equals(existing.getUsername())) {
            if (userRepository.existsByUsername(partial.getUsername())) {
                ValidatorException vex = new ValidatorException("Username exists");
                vex.add(Errors.USERNAME_EXISTS);
                vex.setHttpCode(HttpStatus.BAD_REQUEST);
                throw vex;
            }
            existing.setUsername(partial.getUsername());
        }

        // Roles: if present, replace all (use dedicated methods below for granular ops)
        if (partial.getRoles() != null && !partial.getRoles().isEmpty()) {
            existing.setRoles(partial.getRoles());
        }

        return userRepository.save(existing);
    }

    public void delete(String userId) {
        if (!userRepository.existsById(userId)) {
            ValidatorException vex = new ValidatorException("User not found");
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }
        userRepository.deleteById(userId);
    }

    // Password operations
    public void changePassword(String userId, String currentRawPassword, String newRawPassword) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            ValidatorException vex = new ValidatorException("User not found");
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }
        User user = userOpt.get();
        boolean matches = passwordEncoder.matches(currentRawPassword, user.getPasswordHash());
        if (!matches) {
            ValidatorException vex = new ValidatorException("Invalid current password");
            vex.setHttpCode(HttpStatus.UNAUTHORIZED);
            throw vex;
        }
        user.setPasswordHash(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
    }

    public void resetPassword(String userId, String newRawPassword) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            ValidatorException vex = new ValidatorException("User not found");
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }
        User user = userOpt.get();
        user.setPasswordHash(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
    }

    // Role management
    public void addRole(String userId, Role role) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            ValidatorException vex = new ValidatorException("User not found");
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }
        User user = userOpt.get();
        user.addRole(role);
        userRepository.save(user);
    }

    public void removeRole(String userId, Role role) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            ValidatorException vex = new ValidatorException("User not found");
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }
        User user = userOpt.get();
        user.removeRole(role);
        userRepository.save(user);
    }

    public void setRoles(String userId, List<Role> roles) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            ValidatorException vex = new ValidatorException("User not found");
            vex.setHttpCode(HttpStatus.NOT_FOUND);
            throw vex;
        }
        User user = userOpt.get();
        user.setRoles(roles);
        userRepository.save(user);
    }
}
