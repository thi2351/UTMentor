package com.example.utmentor.presentation.controllers;

import com.example.utmentor.models.exceptions.ApiResponse;
import com.example.utmentor.models.exceptions.GlobalResult;
import com.example.utmentor.models.webModels.users.CreateUserRequest;
import com.example.utmentor.models.webModels.users.CreateUserResponse;
import com.example.utmentor.services.users.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@RequestMapping("api/auth")
public class AuthController extends BaseController {
    private final AuthService _service;
    public AuthController(AuthService service) {
        this._service = service;
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<CreateUserResponse>> register(@Valid @RequestBody CreateUserRequest request) {
        GlobalResult<CreateUserResponse> result = _service.createUser(request);
        return handle(result);
    }

}
