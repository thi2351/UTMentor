package com.example.utmentor.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import com.example.utmentor.models.webModels.users.LoginRequest;
import com.example.utmentor.models.webModels.users.LoginResponse;
import com.example.utmentor.services.users.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService _service;
    public AuthController(AuthService service) {
        this._service = service;
    }

    @PostMapping("register")
    public ResponseEntity<CreateUserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse result = _service.createUser(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse result = _service.login(request);
        return ResponseEntity.ok(result);
    }

}
