package com.example.utmentor.presentation.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.utmentor.models.docEntities.Role;
import com.example.utmentor.models.docEntities.users.User;
import com.example.utmentor.services.UserService;
import com.example.utmentor.models.webModels.users.*;

@RestController
@RequestMapping("api/admin/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        Optional<User> userOpt = userService.findById(userId);
        return userOpt.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.create(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User user) {
        try {
            User updatedUser = userService.update(userId, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        try {
            userService.delete(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Password management
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("{userId}/password/change")
    public ResponseEntity<Void> changePassword(
            @PathVariable String userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userId, request.currentPassword(), request.newPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("{userId}/password/reset")
    public ResponseEntity<Void> resetPassword(
            @PathVariable String userId,
            @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(userId, request.newPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Role management
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("{userId}/roles/{role}")
    public ResponseEntity<Void> addRoleToUser(
            @PathVariable String userId,
            @PathVariable Role role) {
        try {
            userService.addRole(userId, role);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{userId}/roles/{role}")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable String userId,
            @PathVariable Role role) {
        try {
            userService.removeRole(userId, role);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{userId}/roles")
    public ResponseEntity<Void> setUserRoles(
            @PathVariable String userId,
            @RequestBody List<Role> roles) {
        try {
            userService.setRoles(userId, roles);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
