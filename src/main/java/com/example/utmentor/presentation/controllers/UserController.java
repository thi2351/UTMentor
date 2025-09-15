package com.example.utmentor.presentation.controllers;

import com.example.utmentor.models.docEntities.User;
import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import com.example.utmentor.services.UserCrudService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserCrudService _service;
    public UserController(UserCrudService service) {
        this._service = service;
    }
    @PostMapping
    public ResponseEntity<CreateUserResponse> create(@Valid @NotNull @RequestBody  CreateUserRequest request) {
        User entity = _service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserResponse(entity.getUsername()));
    }
}
